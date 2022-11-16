package gatis.bigone.cardgames.game500.game.logic

import gatis.bigone.cardgames.game500.ErrorG500
import gatis.bigone.cardgames.game500.game.domain.GameError.DefaultGameError
import gatis.bigone.cardgames.game500.game.domain.Phase.{Bidding, TakingCards}
import gatis.bigone.cardgames.game500.game.domain.{Game, Player, PlayerIndex, Result}
import gatis.bigone.cardgames.game500.game.logic.Helpers.MapOps

object MakeBidAction {

  def apply(game: Game, bid: Int): Either[ErrorG500, Game] = for {
    _ <- checkIfBiddingPhase(game)
    activeIndex = game.round.activeIndex
    activePlayer <- game.round.players.getE(activeIndex)
    _ <- checkIfPassed(activePlayer)
    _ <- checkIfPointsOver1000(game.results, activeIndex)
    _ <- checkIfValidBid(game, bid)
  } yield {

    val nextToBidIndex = getNextToBidIndex(game, bid)

    val phaseUpdated = if (nextToBidIndex.nonEmpty) game.phase else TakingCards

    val activeIndexUpdated = nextToBidIndex.getOrElse(activeIndex)

    val highestBidUpdated = if (bid > game.round.highestBid) bid else game.round.highestBid

    val bigIndexUpdated = if (bid > game.round.highestBid) Some(activeIndex) else game.round.bigIndex

    val activePlayerUpdated = activePlayer.copy(bid = bid)
    val playersUpdated = game.round.players.updated(activeIndex, activePlayerUpdated)

    val roundUpdated = game.round.copy(
      activeIndex = activeIndexUpdated,
      highestBid = highestBidUpdated,
      bigIndex = bigIndexUpdated,
      players = playersUpdated,
    )

    game.copy(
      phase = phaseUpdated,
      round = roundUpdated,
    )
  }

  private def checkIfBiddingPhase(game: Game): Either[ErrorG500, Unit] =
    if (game.phase != Bidding)
      Left(DefaultGameError(msg = s"Bidding is not allowed in \"${game.phase}\" phase"))
    else Right(())

  private def checkIfPassed(player: Player): Either[ErrorG500, Unit] =
    if (player.bid < 0) Left(DefaultGameError(msg = s"${player.index} has already passed in this round"))
    else Right(())

  private def checkIfPointsOver1000(results: List[Result], activeIndex: PlayerIndex): Either[ErrorG500, Unit] = {
    val playerGamePoints = results.lastOption.map(_.gamePoints(activeIndex)).getOrElse(0)
    if (playerGamePoints >= 1000)
      Left(DefaultGameError(msg = s"$activeIndex is not allowed to bid if total points above 1000."))
    else
      Right(())
  }

  private def checkIfValidBid(game: Game, bid: Int): Either[ErrorG500, Unit] =
    if (bid >= 0 && bid % 5 != 0) Left(DefaultGameError(msg = s"Bid must be with a step of 5"))
    else if (bid >= 0 && bid < 60) Left(DefaultGameError(msg = "Bid must be greater or equal to 60"))
    else if (bid > 205) Left(DefaultGameError(msg = "Bid is too high"))
    else if (bid >= 0 && bid <= game.round.highestBid)
      Left(DefaultGameError(msg = s"Bid must be greater than bid form previous bidder (${game.round.highestBid})"))
    else
      Right(())

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
