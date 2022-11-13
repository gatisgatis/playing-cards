package gatis.bigone.cardgames.game500.game.domain

sealed trait Phase {
  def toString: String
  def next: Phase
}

object Phase {
  case object WaitingForPlayers extends Phase {
    override def toString: String = "Waiting For Players"
    override def next: Phase = WaitingForGameToStart
  }
  case object WaitingForGameToStart extends Phase {
    override def toString: String = "Waiting For Game To Start"
    override def next: Phase = Bidding
  }

  // Round Phases. Maybe refactor
  case object Bidding extends Phase {
    override def toString: String = "Bidding"
    override def next: Phase = TakingCards
  }
  case object TakingCards extends Phase {
    override def toString: String = "Taking Cards"
    override def next: Phase = PassingCards
  }
  case object PassingCards extends Phase {
    override def toString: String = "Passing Cards"
    override def next: Phase = PlayingCards
  }
  case object PlayingCards extends Phase {
    override def toString: String = "Playing Cards"
    override def next: Phase = RoundEnding
  }
  case object RoundEnding extends Phase {
    override def toString: String = "Round Ending"
    override def next: Phase = Bidding
  }

  case object GameFinished extends Phase {
    override def toString: String = "Game Finished"
    override def next: Phase = WaitingForGameToStart
  }
}
