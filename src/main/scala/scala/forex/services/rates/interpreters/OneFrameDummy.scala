package scala.forex.services.rates.interpreters

import forex.services.rates.Algebra
import cats.Applicative
import cats.syntax.applicative._
import cats.syntax.either._

import java.time.OffsetDateTime
import forex.domain.{ Price, Rate }
import forex.services.rates.errors._

class OneFrameDummy[F[_]: Applicative] extends Algebra[F] {

  override def get(pair: Rate.Pair): F[Error Either Rate] =
    Rate(pair, Price(BigDecimal(100)), OffsetDateTime.now()).asRight[Error].pure[F]

}