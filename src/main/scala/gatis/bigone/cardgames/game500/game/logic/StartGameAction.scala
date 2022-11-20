package gatis.bigone.cardgames.game500.game.logic

import gatis.bigone.cardgames.game500.ErrorG500
import gatis.bigone.cardgames.game500.game.domain.GameError.DefaultGameError
import gatis.bigone.cardgames.game500.game.domain.Stage.{InProgress, UnStarted}
import gatis.bigone.cardgames.game500.game.domain.{Game, PlayerIndex, Result, Round}
import gatis.bigone.utils.Utils.SetOps

object StartGameAction {

  def apply(game: Game): Either[ErrorG500, Game] = for {
    _ <- checkIfUnStartedStage(game)
  } yield {
    val round = Round.create(startIndex = PlayerIndex.all.randomPick)
    val result = Result.init()
    game.copy(
      round = round,
      stage = InProgress,
      results = List(result),
    )
  }

  private def checkIfUnStartedStage(game: Game): Either[ErrorG500, Unit] =
    if (game.stage != UnStarted)
      Left(DefaultGameError(msg = s"Cannot restart already started game"))
    else Right(())

}
