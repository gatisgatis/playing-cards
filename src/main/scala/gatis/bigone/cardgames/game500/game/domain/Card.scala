package gatis.bigone.cardgames.game500.game.domain

import gatis.bigone.cardgames.common.cards.{CardTemplate, Rank, Suit}

import scala.math.Ordering

case class Card(override val rank: Rank, override val suit: Suit) extends CardTemplate(rank, suit) {
  def strength: Int = rank.points // to compare which card takes a trick
  def points: Int = rank.points // to count points at the end of the round
}

object Card {
  def fromString(input: String): Option[Card] =
    if (input.trim.length != 2) None
    else {
      for {
        r <- Rank.fromSymbol(input(0))
        s <- Suit.fromSymbol(input(1))
      } yield new Card(r, s)
    }

  implicit object Ordering extends Ordering[Card] {
    def compare(x: Card, y: Card): Int = x.strength.compare(y.strength)
  }
}
