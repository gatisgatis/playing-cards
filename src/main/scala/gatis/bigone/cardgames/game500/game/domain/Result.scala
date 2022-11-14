package gatis.bigone.cardgames.game500.game.domain

import gatis.bigone.cardgames.game500.game.domain.PlayerIndex._

case class Result(
  bid: Option[Int] = None,
  bigIndex: Option[PlayerIndex] = None,
  roundPoints: Map[PlayerIndex, Int] = Map.empty,
  gamePoints: Map[PlayerIndex, Int],
  quitter: Option[PlayerIndex] = None,
)

object Result {
  def init(initialPoints: Int = 500): Result = Result(
    gamePoints = Map(
      FirstPlayer -> initialPoints,
      SecondPlayer -> initialPoints,
      ThirdPlayer -> initialPoints,
    ),
  )
}
