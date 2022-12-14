package gatis.bigone.cardgames.game500.game.domain

import gatis.bigone.cardgames.common.cards.Suit
import gatis.bigone.cardgames.game500.ErrorG500
import gatis.bigone.cardgames.game500.game.domain.GameError.DefaultGameError
import gatis.bigone.cardgames.game500.game.domain.Phase.NotStarted
import gatis.bigone.cardgames.game500.game.domain.PlayerIndex._

case class Round(
  number: Int,
  phase: Phase = NotStarted,
  cardsOnBoard: List[Card] = Nil,
  previousTrick: List[Card] = Nil,
  requiredSuit: Option[Suit] = None,
  cardsToTake: List[Card] = Nil,
  trumpSuit: Option[Suit] = None,
  highestBid: Int = 0,
  activeIndex: PlayerIndex = FirstPlayer,
  bigIndex: Option[PlayerIndex] = None, // index of a player who won bidding and plays as a BIG
  marriagePoints: Int = 0,
  canSmallMarriage: Boolean = false,
  startIndex: PlayerIndex = FirstPlayer, // when game starts, this should be selected at random
  players: Map[PlayerIndex, Player] = Map.empty,
)

object Round {

  implicit class PlayersOps(map: Map[PlayerIndex, Player]) {
    def getE(key: PlayerIndex): Either[ErrorG500, Player] =
      map.get(key).toRight(DefaultGameError(msg = s"Could not find value for key $key"))
  }

  val empty: Round = Round(number = 0)

  def create(startIndex: PlayerIndex, number: Int = 1): Round = {
    val deck = Deck.shuffled
    val cardsToTake = deck.slice(0, 3)
    val player1 = Player.create(playerIndex = FirstPlayer, cards = deck.slice(3, 10))
    val player2 = Player.create(playerIndex = SecondPlayer, cards = deck.slice(10, 17))
    val player3 = Player.create(playerIndex = ThirdPlayer, cards = deck.slice(17, 24))
    val players: Map[PlayerIndex, Player] = Map(FirstPlayer -> player1, SecondPlayer -> player2, ThirdPlayer -> player3)
    Round(
      cardsToTake = cardsToTake,
      players = players,
      number = number,
      activeIndex = startIndex,
      startIndex = startIndex,
    )
  }
}
