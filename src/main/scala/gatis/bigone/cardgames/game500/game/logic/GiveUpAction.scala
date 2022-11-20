package gatis.bigone.cardgames.game500.game.logic

import gatis.bigone.cardgames.game500.ErrorG500
import gatis.bigone.cardgames.game500.game.domain.GameError.DefaultGameError
import gatis.bigone.cardgames.game500.game.domain.Game
import gatis.bigone.cardgames.game500.game.domain.Phase.{PassingCards, RoundEnding}
import gatis.bigone.cardgames.game500.game.logic.Helpers.MapOps

object GiveUpAction {
  def apply(game: Game): Either[ErrorG500, Game] = for {
    _ <- checkIfValidPhase(game)
    _ <- checkIfPlayerIsAllowedToGiveUp(game)
    activeIndex = game.round.activeIndex
    nextIndex = activeIndex.next
    prevIndex = activeIndex.previous
    nextPlayer <- game.round.players.getE(nextIndex)
    prevPlayer <- game.round.players.getE(prevIndex)
  } yield {
    // giving up is only allowed before passing cards
    // giving up means - skip passing and playing cards, 'BIG' loses and others get 50 each

    val nextPlayerUpdated = nextPlayer.copy(points = 50)
    val prevPlayerUpdated = prevPlayer.copy(points = 50)

    val playersUpdated = game.round.players
      .updated(nextIndex, nextPlayerUpdated)
      .updated(prevIndex, prevPlayerUpdated)

    // update round with new points... and change phase to RoundEnding...

    val roundUpdated = game.round.copy(
      phase = RoundEnding,
      players = playersUpdated,
    )

    game.copy(round = roundUpdated)

  }

  private def checkIfValidPhase(game: Game): Either[ErrorG500, Unit] =
    if (game.round.phase != PassingCards)
      Left(DefaultGameError(msg = s"Giving Up is not allowed in \"${game.round.phase}\" phase"))
    else Right(())

  private def checkIfPlayerIsAllowedToGiveUp(game: Game): Either[ErrorG500, Unit] =
    if (!game.round.bigIndex.contains(game.round.activeIndex))
      Left(DefaultGameError(msg = s"Only 'big' player can give up"))
    else Right(())

}
