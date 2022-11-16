package gatis.bigone.cardgames.game500

import gatis.bigone.cardgames.game500.ErrorCode.Default

trait ErrorG500 {
  val msg: String
  val code: ErrorCode
}

object ErrorG500 {
  case class NotSpecified(code: ErrorCode = Default, msg: String) extends ErrorG500
}

trait ErrorCode {
  val value: Int
}

object ErrorCode {
  case object Default extends ErrorCode { val value = 1 }
}
