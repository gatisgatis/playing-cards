package gatis.bigone.cardgames.game500.game.logic

import gatis.bigone.cardgames.game500.ErrorG500
import gatis.bigone.cardgames.game500.game.domain.GameError.DefaultGameError
import gatis.bigone.cardgames.game500.game.domain.Phase.{PassingCards, TakingCards}
import gatis.bigone.cardgames.game500.game.domain.Game
import gatis.bigone.cardgames.game500.game.logic.Helpers.MapOps

object TakeCardsAction {

  def apply(game: Game): Either[ErrorG500, Game] = for {
    _ <- checkIfTakingCardsPhase(game)
    activeIndex = game.round.activeIndex
    activePlayer <- game.round.players.getE(activeIndex)
  } yield {
    val cardsUpdated = activePlayer.cards ::: game.round.cardsToTake
    val playerUpdated = activePlayer.copy(cards = cardsUpdated)
    val playersUpdated = game.round.players.updated(activeIndex, playerUpdated)
    val roundUpdated = game.round.copy(players = playersUpdated, cardsToTake = Nil)
    game.copy(phase = PassingCards, round = roundUpdated)
  }

  private def checkIfTakingCardsPhase(game: Game): Either[ErrorG500, Unit] =
    if (game.phase != TakingCards)
      Left(DefaultGameError(msg = s"Bidding is not allowed in \"${game.phase}\" phase"))
    else Right(())

}
