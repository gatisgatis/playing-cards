package gatis.bigone.cardgames.game500.game.logic

import gatis.bigone.cardgames.game500.game.domain.Code.DefaultGameError
import gatis.bigone.cardgames.game500.game.domain.Phase.{Bidding, Finished, RoundEnding}
import gatis.bigone.cardgames.game500.game.domain.{Error, Game, PlayerIndex, Result, Round}
import gatis.bigone.cardgames.game500.game.logic.Helpers.MapOps

object FinishRound {

  def apply(game: Game): Either[Error, Game] = for {
    _ <- checkIfRoundEndingPhase(game)
    activeIndex = game.round.activeIndex
    activePlayer <- game.round.players.getE(activeIndex)
    nextIndex = activeIndex.next
    nextPlayer <- game.round.players.getE(nextIndex)
    prevIndex = activeIndex.previous
    prevPlayer <- game.round.players.getE(prevIndex)
  } yield {

    val activePlayerPoints = determineRoundPoints(game, activeIndex, activePlayer.points)
    val nextPlayerPoints = determineRoundPoints(game, nextIndex, nextPlayer.points)
    val prevPlayerPoints = determineRoundPoints(game, prevIndex, prevPlayer.points)

    val roundPointsUpdated = Map(
      activeIndex -> activePlayerPoints,
      nextIndex -> nextPlayerPoints,
      prevIndex -> prevPlayerPoints,
    )

    val gamePoints = game.results.last.gamePoints

    val gamePointsUpdated = Map(
      activeIndex -> (gamePoints(activeIndex) - activePlayerPoints),
      nextIndex -> (gamePoints(nextIndex) - nextPlayerPoints),
      prevIndex -> (gamePoints(prevIndex) - prevPlayerPoints),
    )

    val line = Result(
      bid = Some(game.round.highestBid),
      bigIndex = game.round.bigIndex,
      roundPoints = roundPointsUpdated,
      gamePoints = gamePointsUpdated,
    )

    val resultsUpdated = game.results :+ line

    val isGameFinished = gamePointsUpdated.exists { case (_, points) => points <= 0 }

    if (isGameFinished) {
      game.copy(
        phase = Finished,
        results = resultsUpdated,
      )
    } else {
      val newRound = Round.create(
        roundNumber = game.round.roundNumber + 1,
        startIndex = nextIndex,
      )
      game.copy(
        round = newRound,
        phase = Bidding,
        results = resultsUpdated,
      )
    }

  }

  private def checkIfRoundEndingPhase(game: Game): Either[Error, Unit] =
    if (game.phase != RoundEnding)
      Left(Error(code = DefaultGameError, message = s"Bidding is not allowed in \"${game.phase}\" phase"))
    else Right(())

  private[logic] def determineRoundPoints(game: Game, index: PlayerIndex, points: Int): Int =
    if (game.round.bigIndex.contains(index)) {
      val highestBid = game.round.highestBid
      if (highestBid <= points) highestBid
      else -highestBid
    } else {
      val diffOfFive = points % 5
      val pointsRounded =
        if (diffOfFive > 2) points - diffOfFive + 5
        else points - diffOfFive
      game.results.lastOption match {
        case None => pointsRounded
        case Some(line) =>
          val gamePoints = line.gamePoints(index)
          if (gamePoints < 100) 0 else pointsRounded
      }
    }

}
