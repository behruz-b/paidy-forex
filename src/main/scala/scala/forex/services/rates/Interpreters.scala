package scala.forex.services.rates

import scala.forex.config.OneFrameConfig
import scala.forex.redis.RedisClient
import cats.MonadThrow
import org.typelevel.log4cats.Logger

import interpreters._
import sttp.client3.SttpBackend

object Interpreters {
  def oneFrame[F[_]: MonadThrow: Logger](
      config: OneFrameConfig
  )(implicit
    sttpBackend: SttpBackend[F, Any],
    redis: RedisClient[F],
  ): Algebra[F] =
    new OneFrame[F](config)
}
