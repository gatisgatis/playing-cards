package gatis.bigone.cardgames.game500.game.logic

import gatis.bigone.cardgames.game500.game.domain.Code.DefaultGameError
import gatis.bigone.cardgames.game500.game.domain.Phase.{Finished, RoundEnding}
import gatis.bigone.cardgames.game500.game.domain.{Deck, Error, Game, Player, Round}
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

    val a = determineRoundPoints(game, activePlayer)
    val n = determineRoundPoints(game, nextPlayer)
    val p = determineRoundPoints(game, prevPlayer)

    // TODO update results
    val resultsUpdated = game.results
    // TODO figure out if game is finished
    val isGameFinished = false

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
        phase = game.phase.next,
        results = resultsUpdated,
      )
    }

  }

  private def checkIfRoundEndingPhase(game: Game): Either[Error, Unit] =
    if (game.phase != RoundEnding)
      Left(Error(code = DefaultGameError, message = s"Bidding is not allowed in \"${game.phase}\" phase"))
    else Right(())

  private def determineRoundPoints(game: Game, player: Player): Int = {
    val points = player.points
    if (game.round.biddingWinnerIndex.contains(player.index)) {
      val highestBid = game.round.highestBid
      if (highestBid <= points) highestBid
      else -highestBid
    } else {
      val diffOfFive = points % 5
      if (diffOfFive > 2) points - diffOfFive + 5
      else points - diffOfFive
      // TODO check if player's game points is under 100 from results...
    }
  }

}
