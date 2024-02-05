package scala.forex.domain

import enumeratum._
import enumeratum.EnumEntry.Uppercase

sealed trait Currency extends Uppercase

object Currency extends Enum[Currency] with CirceEnum[Currency] {
  case object AUD extends Currency
  case object CAD extends Currency
  case object CHF extends Currency
  case object EUR extends Currency
  case object GBP extends Currency
  case object NZD extends Currency
  case object JPY extends Currency
  case object SGD extends Currency
  case object USD extends Currency

  override def values: IndexedSeq[Currency] = findValues

}
