package gatis.bigone.cardgames.game500.eventsource.domain

import akka.actor.typed.ActorRef
import gatis.bigone.cardgames.game500.game.domain.PlayerIndex.FirstPlayer
import gatis.bigone.cardgames.game500.game.domain.{Game, PlayerIndex, Results}
import gatis.bigone.domain.PlayerId

import java.time.Instant

object Domain {

  case class PlayerParams(
    readyForGame: Boolean = false,
    //    thinkingTime: Int = 0 // maybe something like this is needed here???
    // maybe something else ???
  )

  case class PlayerInfo(
    index: PlayerIndex,
    params: PlayerParams = PlayerParams(),
  )

  case class TableId(value: String) extends AnyVal

  case class TableInfo(
    id: TableId,
    tableActor: ActorRef[Command],
    players: List[PlayerId],
    spectators: List[PlayerId] = Nil,
    admin: PlayerId = PlayerId.empty,
    createdAt: Instant,
    isPrivate: Boolean = false,
  )

  case class TablesFilter(playerId: Option[PlayerId], onlyWithFreeSeats: Boolean)

  case class FinishedGameInfo(
    players: Map[PlayerId, PlayerIndex],
    results: Results,
  )

  case class Table(
    id: TableId,
    createdAt: Instant,
    lastActivity: Instant,
    players: Map[PlayerId, PlayerInfo] = Map.empty,
    spectators: List[PlayerId] = Nil,
    game: Game,
    finishedGames: List[FinishedGameInfo] = Nil,
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
      val playerInfo = PlayerInfo(index = FirstPlayer)
      Table(
        id = tableId,
        createdAt = timestamp,
        lastActivity = timestamp,
        players = Map(creator -> playerInfo),
        game = game,
      )
    }
  }

}
