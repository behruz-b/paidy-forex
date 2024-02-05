package scala.forex.domain
import io.circe.generic.JsonCodec

@JsonCodec
case class Price(value: BigDecimal) extends AnyVal

object Price {
  def apply(value: Integer): Price =
    Price(BigDecimal(value))
}
