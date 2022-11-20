package gatis.bigone.cardgames.game500.game.domain

sealed trait Action

object Action {
  case object GameStarted extends Action
  case object RoundStarted extends Action
  case object BidMade extends Action
  case object CardsTaken extends Action
  case object CardsPassed extends Action
  case object CardPlayed extends Action
  case object GaveUp extends Action
  case object RoundFinished extends Action
  case object GameFinished extends Action

}
