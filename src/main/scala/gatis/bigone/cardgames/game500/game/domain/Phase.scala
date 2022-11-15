package gatis.bigone.cardgames.game500.game.domain

sealed trait Phase

object Phase {
  // initial state as an 'empty' game instance
  case object NotStarted extends Phase
  // last state. table knows that game is done and should congratulate winner and save results somewhere...
  case object Finished extends Phase

  // Round Phases
  case object Bidding extends Phase
  case object TakingCards extends Phase
  case object PassingCards extends Phase
  case object PlayingCards extends Phase
  case object RoundEnding extends Phase
}
