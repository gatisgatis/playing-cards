package gatis.bigone.cardgames.game500.eventsource.domain

import akka.actor.typed.ActorRef
import gatis.bigone.cardgames.game500.game.domain.PlayerIndex.FirstPlayer
import gatis.bigone.cardgames.game500.game.domain.{Game, PlayerIndex}
import gatis.bigone.domain.PlayerId

import java.time.Instant

object Domain {

  case class PlayerInfo(
    index: PlayerIndex,
    agreedToStartGame: Boolean = false,
    rating: String = "not-rated",
    isOnline: Boolean = true,
  )

  case class TableId(value: String)

  case class TableInfo(
    tableActor: ActorRef[Command],
    players: List[PlayerId],
    spectators: List[PlayerId] = Nil,
    createdAt: Instant,
    isPrivate: Boolean = false,
  )

  case class TablesFilter(playerId: Option[PlayerId], onlyWithFreeSeats: Boolean)

  case class Table(
    id: TableId,
    createdAt: Instant,
    lastActivity: Instant,
    players: Map[PlayerId, PlayerInfo] = Map.empty,
    spectators: List[PlayerId] = Nil,
    admin: PlayerId = PlayerId.empty,
    game: Game,
  ) {
    def availablePlayerIndexes: Set[PlayerIndex] = PlayerIndex.all -- players.values.map(_.index).toSet
  }

  object Table {
    val empty: Table = Table(
      id = TableId(""),
      createdAt = Instant.MIN,
      lastActivity = Instant.MIN,
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

}
