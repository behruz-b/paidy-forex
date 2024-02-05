package scala.forex.domain

import io.circe.generic.JsonCodec

import java.time.OffsetDateTime

case class Rate(
    pair: Rate.Pair,
    price: Price,
    timestamp: OffsetDateTime
)

object Rate {
  @JsonCodec
  final case class Pair(
      from: Currency,
      to: Currency
  )
}
