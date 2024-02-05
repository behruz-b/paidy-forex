package scala.forex.services.rates.interpreters

import java.time.OffsetDateTime
import java.time.ZoneId
import scala.concurrent.duration.DurationInt
import scala.forex.config.OneFrameConfig
import scala.forex.redis.RedisClient
import scala.forex.services.rates.Errors.Error._
import cats.MonadThrow
import cats.Traverse
import cats.data.EitherT
import cats.data.OptionT
import cats.syntax.all._

import forex.domain.Rate
import forex.services.rates.Algebra
import forex.services.rates.Errors._
import io.circe
import io.circe.Decoder
import io.circe.DecodingFailure
import io.circe.Json
import io.circe.parser.decode
import io.circe.syntax.EncoderOps
import org.typelevel.log4cats.Logger
import sttp.client3.DeserializationException
import sttp.client3.HttpError
import sttp.client3.Response
import sttp.client3.ResponseException
import sttp.client3.SttpBackend
import sttp.client3.basicRequest
import sttp.client3.circe.asJson
import sttp.model.Uri

import java.time.OffsetDateTime.now

class OneFrame[F[_]: MonadThrow](
    config: OneFrameConfig
)(implicit
  sttpBackend: SttpBackend[F, Any],
  redis: RedisClient[F],
  logger: Logger[F])
    extends Algebra[F] {
  private val RedisKey   = "rates"
  private val Expiration = 1.day
  override def get(pair: Rate.Pair): F[Error Either Rate] =
    OptionT(redis.get(RedisKey)).cataF(
      getAndUpdate(pair)(Map.empty),
      str =>
        EitherT
          .fromEither[F](decode[Map[String, Rate]](str))
          .leftMap(error => toDecodeError(error.asInstanceOf[DecodingFailure], str.asJson.spaces2))
          .flatMapF { implicit cache =>
            cache
              .get(pair.toKey)
              .fold(getAndUpdate(pair))(
                rate =>
                  if (rate.timeStamp.plusMinutes(5).isBefore(OffsetDateTime.now(ZoneId.of("UTC"))))
                    getAndUpdate(pair)
                  else {
                    logger.info(s"Requesting to to REDIS, timestamp: ${now}") *>
                      rate.asRight[Error].pure[F]
                }
              )
          }
          .value,
    )

  private def getAndUpdate(pair: Rate.Pair)(implicit cache: Map[String, Rate]) =
    EitherT(getFromOneFrame(pair)).semiflatTap { rate =>
      redis.put(RedisKey, cache.updated(pair.toKey, rate), Expiration)
    }.value

  private def getFromOneFrame(pair: Rate.Pair) =
    logger.info(s"Requesting to One Frame, timestamp: ${now}") *>
      basicRequest
        .get(Uri(config.uri).withParams("pair" -> pair.toKey))
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
                 .leftMap { decodeFailure =>
                   toDecodeError(decodeFailure, body.map(_.spaces2).mkString_("\n"))
                 }
    } yield result

  private def toDecodeError(decodeFailure: DecodingFailure, jsonRaw: String): DecodeError = {
    val message =
      s"""
         |Http JSON Parsing Error: ${decodeFailure.message}
         |Raw object: $jsonRaw
         |""".stripMargin
    DecodeError(message)
  }
}
