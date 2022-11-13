package gatis.bigone.cardgames.game500.game.domain

import gatis.bigone.cardgames.game500.game.domain.Phase.WaitingForPlayers

import java.util.UUID

case class Game(
  id: GameId,
  round: Round, // current round
  phase: Phase,
  results: List[Result],
)

object Game {
  def create(): Game =
    Game(
      id = GameId(UUID.randomUUID.toString),
      round = Round(roundNumber = 0),
      phase = WaitingForPlayers,
      results = Nil, // TODO init with all players 500 points
    )
}
