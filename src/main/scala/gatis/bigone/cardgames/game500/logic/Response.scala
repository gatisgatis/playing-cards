package gatis.bigone.cardgames.game500.logic

import gatis.bigone.cardgames.game500.logic.Domain.Error

sealed trait Response

object Response {
  case class TestResponse(msg: String) extends Response

  case class TestResponse2(info: Either[Error, Info]) extends Response

  case class Info(table: Table, msg: String)
}
