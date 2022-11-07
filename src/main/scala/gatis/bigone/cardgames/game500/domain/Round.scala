package gatis.bigone.cardgames.game500.domain

import gatis.bigone.cardgames.common.cards.Suit
import gatis.bigone.cardgames.game500.domain.PlayerIndex.{FirstPlayer, SecondPlayer, ThirdPlayer}

case class Round(
  roundNumber: Int,
  cardsOnBoard: List[Card] = Nil,
  previousTrick: List[Card] = Nil,
  requiredSuit: Option[Suit] = None,
  cardsToTake: List[Card] = Nil,
  trump: Option[Suit] = None,
  highestBid: Int = 0,
  activePlayer: PlayerIndex = FirstPlayer,
  biddingWinnerIndex: Option[PlayerIndex] = None,
  marriagePoints: Int = 0,
  isSmallMarriageAllowed: Boolean = false,
  startPlayer: PlayerIndex = FirstPlayer, // when round starts, this should be selected at random
  players: Map[PlayerIndex, Player] = Map.empty, // when updating this, we should check which player index
)

object Round {
  val empty: Round = Round(roundNumber = 0)

  def create(deck: List[Card], roundNumber: Int = 1, startPlayerIndex: PlayerIndex = FirstPlayer): Round = {
    val cardsToTake = deck.slice(0, 3)
    val player1 = Player.create(playerIndex = FirstPlayer, cards = deck.slice(3, 10))
    val player2 = Player.create(playerIndex = SecondPlayer, cards = deck.slice(10, 17))
    val player3 = Player.create(playerIndex = ThirdPlayer, cards = deck.slice(17, 24))
    val players: Map[PlayerIndex, Player] = Map(FirstPlayer -> player1, SecondPlayer -> player2, ThirdPlayer -> player3)
    Round(
      cardsToTake = cardsToTake,
      players = players,
      roundNumber = roundNumber,
      activePlayer = startPlayerIndex,
      startPlayer = startPlayerIndex,
    )
  }
}
