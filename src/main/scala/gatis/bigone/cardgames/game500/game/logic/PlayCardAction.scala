package gatis.bigone.cardgames.game500.game.logic

import gatis.bigone.cardgames.common.cards.Rank.{King, Queen}
import gatis.bigone.cardgames.common.cards.Suit
import gatis.bigone.cardgames.game500.ErrorG500
import gatis.bigone.cardgames.game500.game.domain.GameError.DefaultGameError
import gatis.bigone.cardgames.game500.game.domain.Phase.{PlayingCards, RoundEnding}
import gatis.bigone.cardgames.game500.game.domain.{Card, Game}
import gatis.bigone.cardgames.game500.game.logic.Helpers.MapOps

object PlayCardAction {

  def apply(game: Game, card: Card): Either[ErrorG500, Game] = for {
    _ <- checkIfPlayingCardsPhase(game)
    activeIndex = game.round.activeIndex
    activePlayer <- game.round.players.getE(activeIndex)
    _ <- checkIfHasCard(activePlayer.cards, card)
    _ <- checkIfCardAllowedToPlay(game.round.trumpSuit, game.round.requiredSuit, activePlayer.cards, card)
    nextIndex = activeIndex.next
    prevIndex = activeIndex.previous
    nextPlayer <- game.round.players.getE(nextIndex)
    prevPlayer <- game.round.players.getE(prevIndex)
  } yield {

    val cardsOnBoardUpdated = game.round.cardsOnBoard :+ card
    val activePlayerCardsUpdated = activePlayer.cards.filter(_ != card)
    val activePlayerUpdated = activePlayer.copy(cards = activePlayerCardsUpdated, playedCard = Some(card))

    game.round.requiredSuit match {
      case Some(requiredSuit) =>
        if (cardsOnBoardUpdated.length >= 3) /* 3rd CARD PLAYED */ {
          val trickPoints = cardsOnBoardUpdated.foldLeft(0)((acc, cur) => acc + cur.points)

          val cardTakingTrick = strongestCard(cardsOnBoardUpdated, requiredSuit, game.round.trumpSuit.get)

          val playerTakingTrick = cardsOnBoardUpdated.indexOf(cardTakingTrick) match {
            case 0 => nextPlayer
            case 1 => prevPlayer
            case _ => activePlayerUpdated
          }

          val playerTakingTrickUpdated = playerTakingTrick.copy(
            points = playerTakingTrick.points + trickPoints,
            trickCount = playerTakingTrick.points + 1,
          )

          // Order matters
          val playersUpdated = game.round.players
            .updated(activeIndex, activePlayerUpdated)
            .updated(playerTakingTrickUpdated.index, playerTakingTrickUpdated)

          val roundUpdated = game.round.copy(
            cardsOnBoard = Nil,
            previousTrick = cardsOnBoardUpdated,
            requiredSuit = None,
            activeIndex = playerTakingTrick.index,
            players = playersUpdated,
          )

          val phaseUpdated = if (activePlayerUpdated.cards.isEmpty) RoundEnding else game.phase

          game.copy(round = roundUpdated, phase = phaseUpdated)
        } else /* 2nd CARD PLAYED */
          {
            val playersUpdated = game.round.players.updated(activeIndex, activePlayerUpdated)
            val roundUpdated = game.round.copy(
              cardsOnBoard = cardsOnBoardUpdated,
              activeIndex = nextIndex,
              players = playersUpdated,
            )
            game.copy(round = roundUpdated)
          }
      case None /* 1st CARD PLAYED */ =>
        val trumpSuitUpdated = if (game.round.trumpSuit.isEmpty) Some(card.suit) else game.round.trumpSuit

        val marriagePoints = getMarriageSuit(card, activePlayer.cards) match {
          case Some(suit) if trumpSuitUpdated.contains(suit) => 40
          case Some(_) if game.round.canSmallMarriage => 20
          case _ => 0
        }

        val canSmallMarriageUpdated = if (marriagePoints == 40) true else game.round.canSmallMarriage

        val playersUpdated = Map(
          activeIndex -> activePlayerUpdated.copy(points = activePlayerUpdated.points + marriagePoints),
          nextIndex -> nextPlayer.copy(playedCard = None),
          prevIndex -> prevPlayer.copy(playedCard = None),
        )

        val roundUpdated = game.round.copy(
          cardsOnBoard = cardsOnBoardUpdated,
          requiredSuit = Some(card.suit),
          trumpSuit = trumpSuitUpdated,
          activeIndex = nextIndex,
          marriagePoints = marriagePoints,
          canSmallMarriage = canSmallMarriageUpdated,
          players = playersUpdated,
        )

        game.copy(round = roundUpdated)
    }
  }

  private def checkIfPlayingCardsPhase(game: Game): Either[ErrorG500, Unit] =
    if (game.phase != PlayingCards)
      Left(DefaultGameError(msg = "Cannot play card. No playing-cards phase now"))
    else Right(())

  private def checkIfHasCard(cards: List[Card], card: Card): Either[ErrorG500, Unit] =
    if (!cards.contains(card)) Left(DefaultGameError(msg = "Does not have a card"))
    else Right(())

  private def checkIfCardAllowedToPlay(
    trumpSuit: Option[Suit],
    requiredSuit: Option[Suit],
    cards: List[Card],
    card: Card,
  ): Either[ErrorG500, Unit] = {
    val allowedCards = requiredSuit match {
      case Some(rs) =>
        trumpSuit match {
          case Some(ts) =>
            val rsCards = cards.filter(_.suit == rs)
            if (rsCards.isEmpty) {
              val tsCards = cards.filter(_.suit == ts)
              if (tsCards.isEmpty) cards
              else tsCards
            } else
              rsCards
          case None => cards
        }
      case None => cards
    }
    if (!allowedCards.contains(card)) Left(DefaultGameError(msg = "Not allowed to play this card"))
    else Right(())
  }

  private def strongestCard(cards: List[Card], requiredSuit: Suit, trumpSuit: Suit): Card = {
    val trumpsPlayed = cards.filter(_.suit == trumpSuit)
    if (trumpsPlayed.nonEmpty) trumpsPlayed.max
    else cards.filter(_.suit == requiredSuit).max
  }

  private def getMarriageSuit(card: Card, restOfCards: List[Card]): Option[Suit] = {
    val suit = card.suit
    if (
      (card.rank == King && restOfCards.contains(Card(Queen, suit))) ||
      (card.rank == Queen && restOfCards.contains(Card(King, suit)))
    ) Some(suit)
    else None
  }

}
