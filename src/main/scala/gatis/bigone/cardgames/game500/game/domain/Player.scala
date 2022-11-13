package gatis.bigone.cardgames.game500.game.domain

import gatis.bigone.cardgames.common.cards.Suit

final case class Player(
  cards: List[Card],
  bid: Int = 0,
  points: Int = 0,
  index: PlayerIndex, // do i need it here?
  trickCount: Int = 0,
  playedCard: Option[Card] = None,
) {
  def cardsSorted: List[Card] = (for {
    s <- Suit.all
    sameSuitCards = cards.filter(_.suit == s)
    sorted = sameSuitCards.sortWith(_.strength > _.strength)
  } yield sorted).toList.flatten
}

object Player {
  def create(playerIndex: PlayerIndex, cards: List[Card] = Nil): Player =
    Player(
      index = playerIndex,
      cards = cards,
    )
}
