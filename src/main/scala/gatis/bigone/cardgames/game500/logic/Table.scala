package gatis.bigone.cardgames.game500.logic

import gatis.bigone.cardgames.game500.domain.{Game, PlayerIndex}
import gatis.bigone.cardgames.game500.domain.Phase.WaitingForPlayers
import gatis.bigone.cardgames.game500.domain.PlayerIndex.FirstPlayer
import gatis.bigone.cardgames.game500.logic.Domain.{PlayerInfo, TableId}
import gatis.bigone.domain.PlayerId

import java.time.Instant

case class Table(
  id: TableId,
  createdAt: Instant,
  lastActivity: Instant,
  players: Map[PlayerId, PlayerInfo], // maps players at the table to the game
  admin: PlayerId,
  game: Game,
) {
  def hasFreeSeats: Boolean = game.phase != WaitingForPlayers
  def availablePlayerIndexes: Set[PlayerIndex] = PlayerIndex.all -- players.values.map(_.index).toSet
}

object Table {
  val empty: Table = Table(
    id = TableId(""),
    createdAt = Instant.MIN,
    lastActivity = Instant.MIN,
    players = Map.empty,
    admin = PlayerId.empty,
    game = Game.create(),
  )

  def create(timestamp: Instant, tableId: TableId, creator: PlayerId): Table = {
    val game = Game.create()
    val playerInfo = PlayerInfo(FirstPlayer)
    Table(
      id = tableId,
      createdAt = timestamp,
      lastActivity = timestamp,
      players = Map(creator -> playerInfo),
      admin = creator,
      game = game,
    )
  }
}
