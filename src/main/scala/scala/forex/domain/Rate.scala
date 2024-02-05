package scala.forex.domain

import java.time.OffsetDateTime

import io.circe.generic.JsonCodec
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.ConfiguredJsonCodec

@ConfiguredJsonCodec
case class Rate(
    from: Currency,
    to: Currency,
    bid: BigDecimal,
    ask: BigDecimal,
    price: BigDecimal,
    timeStamp: OffsetDateTime,
  )

object Rate {
  implicit val configuration: Configuration = Configuration.default.withSnakeCaseMemberNames

  @JsonCodec
  final case class Pair(
      from: Currency,
      to: Currency,
    ) {
    def toKey = s"${from.entryName}${to.entryName}"
  }
}
