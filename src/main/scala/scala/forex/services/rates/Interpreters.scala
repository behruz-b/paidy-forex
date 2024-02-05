package scala.forex.services.rates

import cats.MonadThrow
import sttp.client3.SttpBackend

import interpreters._
import scala.forex.config.OneFrameConfig

object Interpreters {
  def oneFrame[F[_]: MonadThrow](config: OneFrameConfig)(implicit sttpBackend: SttpBackend[F, Any]): Algebra[F] =
    new OneFrame[F](config)
}