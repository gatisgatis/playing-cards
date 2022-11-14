package gatis.bigone.cardgames.game500.game.domain

sealed trait Phase {
  def toString: String
  def next: Phase
}

object Phase {
  case object NotStarted extends Phase {
    // init state to create an 'empty' game instance
    override def toString: String = "Game Not Started"
    override def next: Phase = Bidding
  }
  case object Finished extends Phase {
    // last state. table knows that game is done and should congratulate winner and save results somewhere...
    override def toString: String = "Game Finished"
    override def next: Phase = NotStarted
  }

  // Round Phases
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
}
