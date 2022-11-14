package gatis.bigone.cardgames.game500.game.logic

import gatis.bigone.cardgames.game500.game.domain.Code.DefaultGameError
import gatis.bigone.cardgames.game500.game.domain.Phase.{Bidding, NotStarted}
import gatis.bigone.cardgames.game500.game.domain.{Deck, Error, Game, PlayerIndex, Result, Round}
import gatis.bigone.utils.Utils.SetOps

object StartGame {

  def apply(game: Game): Either[Error, Game] = for {
    _ <- checkIfNotStartedPhase(game)
  } yield {
    val round = Round.create(startIndex = PlayerIndex.all.randomPick)
    // TODO init results here. 500 points start
    val result = Result.create("start results 500 points each")
    game.copy(
      round = round,
      phase = Bidding,
      results = List(result),
    )
  }

  private def checkIfNotStartedPhase(game: Game): Either[Error, Unit] =
    if (game.phase != NotStarted)
      Left(Error(code = DefaultGameError, message = s"Cannot restart already started game"))
    else Right(())

}
