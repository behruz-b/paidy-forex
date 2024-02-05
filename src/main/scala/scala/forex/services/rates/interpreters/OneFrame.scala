package scala.forex.services.rates.interpreters

import forex.services.rates.Algebra
import cats.{ MonadThrow, Traverse }
import cats.syntax.all._

import cats.syntax.either._
import io.circe
import io.circe.{ Decoder, Json }
import sttp.client3.circe.asJson
import sttp.client3.{ basicRequest, DeserializationException, HttpError, Response, ResponseException, SttpBackend }
import sttp.model.Uri

import forex.domain.Rate
import forex.services.rates.Errors._
import scala.concurrent.duration.DurationInt
import scala.forex.config.OneFrameConfig
import scala.forex.services.rates.Errors.Error._

class OneFrame[F[_]: MonadThrow](config: OneFrameConfig)(implicit sttpBackend: SttpBackend[F, Any])
    extends Algebra[F] {

  override def get(pair: Rate.Pair): F[Error Either Rate] =
    basicRequest
      .get(Uri(config.uri).withParams("pair" -> s"${pair.from.entryName}${pair.to.entryName}"))
      .headers(Map("token" -> config.token))
      .readTimeout(10.seconds)
      .response(asJson[List[Json]])
      .send(sttpBackend)
      .map(responseDecoder[List, Rate])
      .map(_.flatMap(_.headOption.toRight(OneFrameLookupFailed("Currency rates not found"))))

  private def responseDecoder[M[_]: Traverse, A: Decoder](
      response: Response[Either[ResponseException[String, circe.Error], M[Json]]]
  ): Error Either M[A] =
    for {
      body <- response.body.leftMap {
               case HttpError(body, statusCode) => SttpError(statusCode, body)
               case DeserializationException(body, error) =>
                 DeserializationError(
                   s"DeserializationException: ${error.getMessage}.\nStatus: ${response.statusText}.\n Body $body"
                 )
             }
      result <- body
                 .traverse(_.as[A])
                 .leftMap { parsingError =>
                   val message =
                     s"""
               |Http JSON Parsing Error: ${parsingError.message}
               |Error reason: ${parsingError.reason}
               |Raw object: ${body.map(_.spaces2).mkString_("\n")}
               |""".stripMargin
                   DecodeError(message)
                 }
    } yield result

}
