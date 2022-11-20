package gatis.bigone.cardgames.game500.game.logic

import gatis.bigone.cardgames.game500.ErrorG500
import gatis.bigone.cardgames.game500.game.domain.GameError.DefaultGameError
import gatis.bigone.cardgames.game500.game.domain.Phase.{PassingCards, PlayingCards}
import gatis.bigone.cardgames.game500.game.domain.{Card, Game}
import gatis.bigone.cardgames.game500.game.logic.Helpers.MapOps

object PassCardsAction {

  def apply(game: Game, cardToLeft: Card, cardToRight: Card): Either[ErrorG500, Game] = for {
    _ <- checkIfPassingCardsPhase(game)
    activeIndex = game.round.activeIndex
    activePlayer <- game.round.players.getE(activeIndex)
    _ <-
      if (!activePlayer.cards.contains(cardToLeft) || !activePlayer.cards.contains(cardToRight))
        Left(DefaultGameError(msg = "Does not have selected cards"))
      else Right(())
    leftPlayer <- game.round.players.getE(activeIndex.next)
    rightPlayer <- game.round.players.getE(activeIndex.previous)
  } yield {
    val cardsUpdated = activePlayer.cards.filter(card => card != cardToRight && card != cardToLeft)
    val activePlayerUpdated = activePlayer.copy(cards = cardsUpdated)

    val leftPlayerUpdated = leftPlayer.copy(cards = leftPlayer.cards :+ cardToLeft)

    val rightPlayerUpdated = rightPlayer.copy(cards = rightPlayer.cards :+ cardToRight)

    val playersUpdated = Map(
      activeIndex -> activePlayerUpdated,
      activeIndex.next -> leftPlayerUpdated,
      activeIndex.previous -> rightPlayerUpdated,
    )
    val roundUpdated = game.round.copy(
      phase = PlayingCards,
      players = playersUpdated,
    )

    game.copy(round = roundUpdated)
  }

  private def checkIfPassingCardsPhase(game: Game): Either[ErrorG500, Unit] =
    if (game.round.phase != PassingCards)
      Left(DefaultGameError(msg = "Cannot pass cards. No pass-cards phase now"))
    else Right(())

}
