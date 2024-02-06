package scala.forex.http.rates

import cats.syntax.all.catsSyntaxEither
import forex.domain.Currency
import org.http4s.ParseFailure
import org.http4s.QueryParamDecoder
import org.http4s.dsl.impl.QueryParamDecoderMatcher

object QueryParams {
  implicit private[http] val currencyQueryParam: QueryParamDecoder[Currency] =
    QueryParamDecoder[String].emap(
      str =>
        Currency
          .withNameEither(str.toUpperCase)
          .leftMap(error => ParseFailure(s"Currency not found ${error.notFoundName}", error.getMessage()))
    )

  object FromQueryParam extends QueryParamDecoderMatcher[Currency]("from")
  object ToQueryParam extends QueryParamDecoderMatcher[Currency]("to")
}
