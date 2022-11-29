package gatis.bigone.cardgames.game500.game.logic

import gatis.bigone.cardgames.game500.ErrorG500
import gatis.bigone.cardgames.game500.game.domain.GameError.DefaultGameError
import gatis.bigone.cardgames.game500.game.domain.Phase.{Bidding, NotStarted}
import gatis.bigone.cardgames.game500.game.domain.Stage.InProgress
import gatis.bigone.cardgames.game500.game.domain.Game

object StartRoundAction {

  def apply(game: Game): Either[ErrorG500, Game] = for {
    _ <- checkIfGameInProgressStage(game)
    _ <- checkIfNotStartedPhase(game)
  } yield {
    val roundUpdated = game.round.copy(phase = Bidding)
    game.copy(round = roundUpdated)
  }

  private def checkIfNotStartedPhase(game: Game): Either[ErrorG500, Unit] =
    if (game.round.phase != NotStarted)
      Left(DefaultGameError(msg = s"Not in '${game.round.phase}' phase"))
    else Right(())

  private def checkIfGameInProgressStage(game: Game): Either[ErrorG500, Unit] =
    if (game.stage != InProgress)
      Left(DefaultGameError(msg = s"Not in GameInProgress phase"))
    else Right(())

}
