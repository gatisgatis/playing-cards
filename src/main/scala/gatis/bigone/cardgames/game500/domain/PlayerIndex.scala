package gatis.bigone.cardgames.game500.domain

sealed trait PlayerIndex {
  def toString: String
  def next: PlayerIndex
  def previous: PlayerIndex
}

object PlayerIndex {
  case object FirstPlayer extends PlayerIndex {
    override def next: PlayerIndex = SecondPlayer
    override def previous: PlayerIndex = ThirdPlayer
    override def toString: String = "FirstPlayer"
  }
  case object SecondPlayer extends PlayerIndex {
    override def next: PlayerIndex = ThirdPlayer
    override def previous: PlayerIndex = FirstPlayer
    override def toString: String = "SecondPlayer"
  }
  case object ThirdPlayer extends PlayerIndex {
    override def next: PlayerIndex = FirstPlayer
    override def previous: PlayerIndex = SecondPlayer
    override def toString: String = "ThirdPlayer"
  }
  val all: Set[PlayerIndex] = Set(FirstPlayer, SecondPlayer, ThirdPlayer)
}
