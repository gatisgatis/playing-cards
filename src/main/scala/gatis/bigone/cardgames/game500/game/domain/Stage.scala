package gatis.bigone.cardgames.game500.game.domain

sealed trait Stage

object Stage {
  // initial state as an 'empty' game instance
  case object UnStarted extends Stage

  case object InProgress extends Stage

  // last state. table knows that game is done and should congratulate winner and save results somewhere...
  case object Finished extends Stage
}
