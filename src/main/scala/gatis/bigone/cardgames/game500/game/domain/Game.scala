package gatis.bigone.cardgames.game500.game.domain

import gatis.bigone.cardgames.game500.game.domain.Phase.NotStarted

import java.util.UUID

case class Game(
  id: GameId,
  round: Round,
  phase: Phase,
  results: List[Result],
)

object Game {
  def create(): Game =
    Game(
      id = GameId(UUID.randomUUID.toString),
      round = Round(roundNumber = 0),
      phase = NotStarted,
      results = Nil,
    )
}
