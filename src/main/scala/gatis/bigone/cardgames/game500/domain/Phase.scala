package gatis.bigone.cardgames.game500.domain

sealed trait Phase {
  def toString: String
  def nextPhase: Phase
}

object Phase {
  case object WaitingForPlayers extends Phase {
    override def toString: String = "Waiting For Players"
    override def nextPhase: Phase = WaitingForGameToStart
  }
  case object WaitingForGameToStart extends Phase {
    override def toString: String = "Waiting For Game To Start"
    override def nextPhase: Phase = Bidding
  }
  case object Bidding extends Phase {
    override def toString: String = "Bidding"
    override def nextPhase: Phase = TakingCards
  }
  case object TakingCards extends Phase {
    override def toString: String = "Taking Cards"
    override def nextPhase: Phase = PassingCards
  }
  case object PassingCards extends Phase {
    override def toString: String = "Passing Cards"
    override def nextPhase: Phase = PlayingCards
  }
  case object PlayingCards extends Phase {
    override def toString: String = "Playing Cards"
    override def nextPhase: Phase = RoundEnding
  }
  case object RoundEnding extends Phase {
    override def toString: String = "Round Ending"
    override def nextPhase: Phase = Bidding
  }
  case object GameFinished extends Phase {
    override def toString: String = "Game Finished"
    override def nextPhase: Phase = WaitingForGameToStart
  }
}
