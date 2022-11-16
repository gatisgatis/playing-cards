package gatis.bigone.cardgames.game500.game.logic

import gatis.bigone.cardgames.game500.ErrorG500
import gatis.bigone.cardgames.game500.game.domain.GameError.DefaultGameError
import gatis.bigone.cardgames.game500.game.domain.{Game, Result, Round}
import gatis.bigone.cardgames.game500.game.domain.Phase.{Bidding, PassingCards}
import gatis.bigone.cardgames.game500.game.logic.FinishRoundAction.determineRoundPoints

object GiveUpAction {
  def apply(game: Game): Either[ErrorG500, Game] = for {
    _ <- checkIfValidPhase(game)
    _ <- checkIfPlayerIsAllowedToGiveUp(game)
    activeIndex = game.round.activeIndex
    nextIndex = activeIndex.next
    prevIndex = activeIndex.previous
  } yield {
    // giving up is only allowed before passing cards
    // giving up means - skip passing and playing cards, 'BIG' loses and others get 50 each
    val activePlayerPoints = determineRoundPoints(game, activeIndex, 0)
    val nextPlayerPoints = determineRoundPoints(game, activeIndex.next, 50)
    val prevPlayerPoints = determineRoundPoints(game, activeIndex.previous, 50)

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

    val newRound = Round.create(
      roundNumber = game.round.roundNumber + 1,
      startIndex = nextIndex,
    )
    game.copy(
      round = newRound,
      phase = Bidding,
      results = game.results :+ line,
    )

  }

  private def checkIfValidPhase(game: Game): Either[ErrorG500, Unit] =
    if (game.phase != PassingCards)
      Left(DefaultGameError(msg = s"Giving Up is not allowed in \"${game.phase}\" phase"))
    else Right(())

  private def checkIfPlayerIsAllowedToGiveUp(game: Game): Either[ErrorG500, Unit] =
    if (!game.round.bigIndex.contains(game.round.activeIndex))
      Left(DefaultGameError(msg = s"Only 'big' player can give up"))
    else Right(())

}
