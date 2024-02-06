package scala.forex.http
package rates

import scala.forex.programs.rates.Errors.Error.RateLookupFailed
import cats.MonadThrow
import cats.data.EitherT
import cats.implicits.catsSyntaxApplicativeError
import cats.syntax.flatMap._

import forex.programs.RatesProgram
import forex.programs.rates.{ Protocol => RatesProgramProtocol }
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.typelevel.log4cats.Logger

class RatesHttpRoutes[F[_]: MonadThrow: Logger](rates: RatesProgram[F]) extends Http4sDsl[F] {
  import QueryParams._

  private[http] val prefixPath = "/rates"

  private val httpRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root :? FromQueryParam(from) +& ToQueryParam(to) =>
      EitherT(rates.get(RatesProgramProtocol.GetRatesRequest(from, to))).rethrowT
        .flatMap { rate =>
          Ok(rate)
        }
        .recoverWith {
          case error: RateLookupFailed =>
            UnprocessableEntity(error.msg)
          case error =>
            Logger[F].error(error)("Error occurred while retrieve rates") >>
              BadGateway("Something went wrong. Please try again.")
        }
  }

  val routes: HttpRoutes[F] = Router(
    prefixPath -> httpRoutes
  )
}
