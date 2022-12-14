package gatis.bigone.cardgames.game500.game.domain

import gatis.bigone.cardgames.common.cards.Rank._
import gatis.bigone.cardgames.common.cards.Suit

import scala.util.Random

object Deck {
  val deck: List[Card] = (for {
    r <- Set(Nine, Ten, Jack, Queen, King, Ace)
    s <- Suit.all
  } yield Card(r, s)).toList

  def shuffled: List[Card] = Random.shuffle(deck)
}
