package gatis.bigone.cardgames.game500.game.domain

import gatis.bigone.cardgames.game500.{ErrorCode, ErrorG500}
import gatis.bigone.cardgames.game500.ErrorCode._

object GameError {
  case class DefaultGameError(
    override val code: ErrorCode = Default,
    override val msg: String,
  ) extends ErrorG500
}
