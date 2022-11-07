package gatis.bigone.cardgames.common.cards

import scala.reflect.ClassTag

abstract class CardTemplate(rank: Rank, suit: Suit) {
  def toPrettyString: String = suit.color + rank.toString + suit.toString + Console.RESET
  override def toString: String = rank.toString + suit.toString
}

//object CardTemplate {
//  def fromString[T >: CardTemplate](input: String): Option[T] =
//    if (input.trim.length != 2) None
//    else {
//      for {
//        r <- Rank.fromSymbol(input(0))
//        s <- Suit.fromSymbol(input(1))
//      } yield new T(r, s)
//    }
//}
