package gatis.bigone.cardgames.game500.game.domain

import gatis.bigone.cardgames.game500.game.domain.PlayerIndex._

case class Results(
  gamePoints: Map[PlayerIndex, Int],
  lines: List[ResultLine] = Nil,
  quitter: Option[PlayerIndex] = None,
  winner: Option[PlayerIndex] = None,
)

case class ResultLine(
  bid: Option[Int] = None,
  bigIndex: Option[PlayerIndex] = None,
  points: Map[PlayerIndex, Int] = Map.empty,
)

object Results {
  def init(initialPoints: Int = 500): Results = Results(
    gamePoints = Map(
      FirstPlayer -> initialPoints,
      SecondPlayer -> initialPoints,
      ThirdPlayer -> initialPoints,
    ),
  )
}
