package gatis.bigone.cardgames.game500.game.logic

import gatis.bigone.cardgames.game500.game.domain.Code.DefaultGameError
import gatis.bigone.cardgames.game500.game.domain.Phase.TakingCards
import gatis.bigone.cardgames.game500.game.domain.{Error, Game}
import gatis.bigone.cardgames.game500.game.logic.Helpers.MapOps

object TakeCards {

  def apply(game: Game): Either[Error, Game] = for {
    _ <- checkIfTakingCardsPhase(game)
    activeIndex = game.round.activeIndex
    player <- game.round.players.getE(activeIndex)
  } yield {
    val cardsUpdated = player.cards ::: game.round.cardsToTake
    val playerUpdated = player.copy(cards = cardsUpdated)
    val playersUpdated = game.round.players.updated(activeIndex, playerUpdated)
    val roundUpdated = game.round.copy(players = playersUpdated, cardsToTake = Nil)
    game.copy(phase = game.phase.next, round = roundUpdated)
  }

  private def checkIfTakingCardsPhase(game: Game): Either[Error, Unit] =
    if (game.phase != TakingCards)
      Left(Error(code = DefaultGameError, message = s"Bidding is not allowed in \"${game.phase}\" phase"))
    else Right(())

}
