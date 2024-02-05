package scala.forex.http.rates

import forex.domain._
import io.circe.generic.JsonCodec

import java.time.OffsetDateTime

object Protocol {

  final case class GetApiRequest(
      from: Currency,
      to: Currency
  )

  @JsonCodec
  final case class GetApiResponse(
      from: Currency,
      to: Currency,
      price: Price,
      timestamp: OffsetDateTime
  )

}
