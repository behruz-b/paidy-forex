package scala.forex.domain
import io.circe.generic.JsonCodec
import io.circe.generic.extras.{ Configuration, ConfiguredJsonCodec }

import java.time.OffsetDateTime

@ConfiguredJsonCodec
case class Rate(
    from: Currency,
    to: Currency,
    bid: BigDecimal,
    ask: BigDecimal,
    price: BigDecimal,
    timeStamp: OffsetDateTime
)

object Rate {
  implicit val configuration: Configuration = Configuration.default.withSnakeCaseMemberNames

  @JsonCodec
  final case class Pair(
      from: Currency,
      to: Currency
  )
}
