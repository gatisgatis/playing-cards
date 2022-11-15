package gatis.bigone.cardgames.game500.game.domain

case class Error(code: Code, message: String)

sealed trait Code

object Code {
  // TODO improve this
  case object DefaultGameError extends Code
  case object UnexpectedGameError extends Code
}
