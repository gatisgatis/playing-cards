package gatis.bigone.cardgames.game500.game.logic

import gatis.bigone.cardgames.game500.ErrorG500
import gatis.bigone.cardgames.game500.game.domain.GameError.DefaultGameError
import gatis.bigone.cardgames.game500.game.domain.Phase.{Bidding, NotStarted}
import gatis.bigone.cardgames.game500.game.domain.{Game, PlayerIndex, Result, Round}
import gatis.bigone.utils.Utils.SetOps

object StartGameAction {

  def apply(game: Game): Either[ErrorG500, Game] = for {
    _ <- checkIfNotStartedPhase(game)
  } yield {
    val round = Round.create(startIndex = PlayerIndex.all.randomPick)
    val result = Result.init()
    game.copy(
      round = round,
      phase = Bidding,
      results = List(result),
    )
  }

  private def checkIfNotStartedPhase(game: Game): Either[ErrorG500, Unit] =
    if (game.phase != NotStarted)
      Left(DefaultGameError(msg = s"Cannot restart already started game"))
    else Right(())

}
