package gatis.bigone.cardgames.game500.game.domain

sealed trait Phase

object Phase {
  case object NotStarted extends Phase
  case object Bidding extends Phase
  case object TakingCards extends Phase
  case object PassingCards extends Phase
  case object PlayingCards extends Phase
  case object RoundEnding extends Phase
}
