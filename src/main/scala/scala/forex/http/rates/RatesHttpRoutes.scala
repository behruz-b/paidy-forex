package scala.forex.http
package rates

import cats.MonadThrow
import cats.data.EitherT
import cats.syntax.flatMap._

import forex.programs.RatesProgram
import forex.programs.rates.{ Protocol => RatesProgramProtocol }
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

class RatesHttpRoutes[F[_]: MonadThrow](rates: RatesProgram[F]) extends Http4sDsl[F] {
  import QueryParams._

  private[http] val prefixPath = "/rates"

  private val httpRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root :? FromQueryParam(from) +& ToQueryParam(to) =>
      EitherT(rates.get(RatesProgramProtocol.GetRatesRequest(from, to))).rethrowT
        .flatMap { rate =>
          Ok(rate)
        }
  }

  val routes: HttpRoutes[F] = Router(
    prefixPath -> httpRoutes
  )

}
