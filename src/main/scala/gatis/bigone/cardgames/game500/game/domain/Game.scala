package gatis.bigone.cardgames.game500.game.domain

import gatis.bigone.cardgames.game500.game.domain.Stage.UnStarted

import java.util.UUID

case class GameId(id: String) extends AnyVal

final case class Game(
  id: GameId,
  round: Round,
  stage: Stage,
  results: List[Result],
)

object Game {
  def create(): Game =
    Game(
      id = GameId(UUID.randomUUID.toString),
      round = Round.empty,
      stage = UnStarted,
      results = Nil,
    )
}
