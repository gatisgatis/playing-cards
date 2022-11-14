package gatis.bigone.cardgames.game500.game.logic

import gatis.bigone.cardgames.game500.game.domain.Code.DefaultGameError
import gatis.bigone.cardgames.game500.game.domain.Phase.Bidding
import gatis.bigone.cardgames.game500.game.domain.{Error, Game, Player, PlayerIndex}
import gatis.bigone.cardgames.game500.game.logic.Helpers.MapOps

object MakeBid {

  def apply(game: Game, bid: Int): Either[Error, Game] = for {
    _ <- checkIfBiddingPhase(game)
    activeIndex = game.round.activeIndex
    activePlayer <- game.round.players.getE(activeIndex)
    _ <- checkIfPassed(activePlayer)
    _ <- checkIfPointsOver1000(game)
    _ <- checkIfValidBid(game, bid)
  } yield {

    val nextToBidIndex = getNextToBidIndex(game, bid)

    val phaseUpdated = if (nextToBidIndex.nonEmpty) game.phase else game.phase.next

    val activeIndexUpdated = nextToBidIndex.getOrElse(activeIndex)

    val highestBidUpdated = if (bid > game.round.highestBid) bid else game.round.highestBid

    val activePlayerUpdated = activePlayer.copy(bid = bid)
    val playersUpdated = game.round.players.updated(activeIndex, activePlayerUpdated)

    val roundUpdated =
      game.round.copy(activeIndex = activeIndexUpdated, highestBid = highestBidUpdated, players = playersUpdated)

    game.copy(
      phase = phaseUpdated,
      round = roundUpdated,
    )
  }

  private def checkIfBiddingPhase(game: Game): Either[Error, Unit] =
    if (game.phase != Bidding)
      Left(Error(code = DefaultGameError, message = s"Bidding is not allowed in \"${game.phase}\" phase"))
    else Right(())

  private def checkIfPassed(player: Player): Either[Error, Unit] =
    if (player.bid < 0)
      Left(Error(code = DefaultGameError, message = s"${player.index} has already passed in this round"))
    else Right(())

  private def checkIfPointsOver1000(game: Game): Either[Error, Unit] = {
    val playerGamePoints = 123 // TODO. Get player's current points from game's results table
    if (playerGamePoints >= 0)
      Left(
        Error(
          code = DefaultGameError,
          message = s"${game.round.activeIndex} is not allowed to bid if total points above 1000.",
        ),
      )
    else Right(())
  }

  private def checkIfValidBid(game: Game, bid: Int): Either[Error, Unit] =
    if (bid >= 0 && bid % 5 != 0) Left(Error(code = DefaultGameError, message = s"Bid must be with a step of 5"))
    else if (bid >= 0 && bid < 60) Left(Error(code = DefaultGameError, message = "Bid must be greater or equal to 60"))
    else if (bid > 205) Left(Error(code = DefaultGameError, message = "Bid is too high"))
    else if (bid >= 0 && bid <= game.round.highestBid)
      Left(
        Error(
          code = DefaultGameError,
          message = s"Bid must be greater than bid form previous bidder (${game.round.highestBid})",
        ),
      )
    else Right(())

  private def getNextToBidIndex(game: Game, bid: Int): Option[PlayerIndex] = {
    val nextIndex = game.round.activeIndex.next
    val prevIndex = game.round.activeIndex.previous
    for {
      next <- game.round.players.get(nextIndex)
      prev <- game.round.players.get(prevIndex)
      index <-
        if (next.bid >= 0) Some(nextIndex)
        else if (prev.bid >= 0) Some(prevIndex)
        else if (bid >= 0) Some(game.round.activeIndex)
        else None
    } yield index
  }

}
