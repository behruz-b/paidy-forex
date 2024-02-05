package scala.forex.services.rates
import sttp.model.StatusCode

object Errors {

  sealed trait Error
  object Error {
    final case class OneFrameLookupFailed(msg: String) extends Error
    final case class SttpError(statusCode: StatusCode, message: String) extends Error
    final case class DeserializationError(message: String) extends Error
    final case class DecodeError(message: String) extends Error
  }

}
