package gatis.bigone.cardgames.game500.game.logic

import gatis.bigone.cardgames.game500.game.domain.Code.DefaultGameError
import gatis.bigone.cardgames.game500.game.domain.Phase.{PassingCards, PlayingCards}
import gatis.bigone.cardgames.game500.game.domain.{Card, Error, Game}
import gatis.bigone.cardgames.game500.game.logic.Helpers.MapOps

object PassCards {

  def apply(game: Game, cardToLeft: Card, cardToRight: Card): Either[Error, Game] = for {
    _ <- checkIfPassingCardsPhase(game)
    activeIndex = game.round.activeIndex
    activePlayer <- game.round.players.getE(activeIndex)
    _ <-
      if (!activePlayer.cards.contains(cardToLeft) || !activePlayer.cards.contains(cardToRight))
        Left(Error(code = DefaultGameError, message = "Does not have selected cards"))
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
    val roundUpdated = game.round.copy(players = playersUpdated)

    game.copy(round = roundUpdated, phase = PlayingCards)
  }

  private def checkIfPassingCardsPhase(game: Game): Either[Error, Unit] =
    if (game.phase != PassingCards)
      Left(Error(code = DefaultGameError, message = "Cannot pass cards. No pass-cards phase now"))
    else Right(())

}
