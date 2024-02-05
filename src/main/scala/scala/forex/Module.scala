package scala.forex

import scala.forex.config.ApplicationConfig
import scala.forex.http.rates.RatesHttpRoutes
import scala.forex.programs._
import scala.forex.redis.RedisClient
import scala.forex.services._

import cats.effect.Temporal
import org.http4s._
import org.http4s.implicits._
import org.http4s.server.middleware.AutoSlash
import org.http4s.server.middleware.Timeout
import sttp.client3.SttpBackend

class Module[F[_]: Temporal](
    config: ApplicationConfig
  )(implicit
    sttpBackend: SttpBackend[F, Any],
    redis: RedisClient[F],
  ) {
  private val ratesService: RatesService[F] = RatesServices.oneFrame[F](config.oneFrame)

  private val ratesProgram: RatesProgram[F] = RatesProgram[F](ratesService)

  private val ratesHttpRoutes: HttpRoutes[F] = new RatesHttpRoutes[F](ratesProgram).routes

  type PartialMiddleware = HttpRoutes[F] => HttpRoutes[F]
  type TotalMiddleware = HttpApp[F] => HttpApp[F]

  private val routesMiddleware: PartialMiddleware = { http: HttpRoutes[F] =>
    AutoSlash(http)
  }
  private val appMiddleware: TotalMiddleware = { http: HttpApp[F] =>
    Timeout(config.http.timeout)(http)
  }

  private val http: HttpRoutes[F] = ratesHttpRoutes

  val httpApp: HttpApp[F] = appMiddleware(routesMiddleware(http).orNotFound)
}
