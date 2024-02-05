package scala.forex.programs.rates

import sttp.model.StatusCode

import forex.services.rates.Errors.{ Error => RatesServiceError }

object Errors {

  sealed trait Error extends Exception
  object Error {
    final case class RateLookupFailed(msg: String) extends Error
    final case class SttpError(statusCode: StatusCode, message: String) extends Error {
      override def getMessage: String = s"HttpError $statusCode: $message"
    }
    final case class DeserializationError(message: String) extends Error {
      override def getMessage: String = message
    }
    final case class DecodeError(message: String) extends Error {
      override def getMessage: String = message
    }
  }

  def toProgramError(error: RatesServiceError): Error = error match {
    case RatesServiceError.OneFrameLookupFailed(msg)  => Error.RateLookupFailed(msg)
    case RatesServiceError.SttpError(statusCode, msg) => Error.SttpError(statusCode, msg)
    case RatesServiceError.DeserializationError(msg)  => Error.DeserializationError(msg)
    case RatesServiceError.DecodeError(msg)           => Error.DecodeError(msg)
  }
}
