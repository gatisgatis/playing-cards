package gatis.bigone.cardgames.game500.eventsource.domain

import gatis.bigone.cardgames.game500.eventsource.domain.Domain.{PlayerParams, Table, TableId}
import gatis.bigone.cardgames.game500.game.domain.{Action, Game}
import gatis.bigone.domain.PlayerId

import java.time.Instant

object Event {

  sealed trait TableEvent

  object TableEvent {
    case class TableStarted(table: Table) extends TableEvent

    case class PlayerJoined(timestamp: Instant, playerId: PlayerId) extends TableEvent
    case class PlayerLeft(timestamp: Instant, playerId: PlayerId) extends TableEvent
    case class SpectatorJoined(playerId: PlayerId) extends TableEvent
    case class SpectatorLeft(playerId: PlayerId) extends TableEvent

    case class PlayerParamsUpdated(timestamp: Instant, playerId: PlayerId, params: PlayerParams) extends TableEvent

    case class GameProgressEvent(timestamp: Instant, game: Game, action: Action) extends TableEvent
  }

  sealed trait TableManagerEvent

  sealed trait TableSpecificEvent { val tableId: TableId }

  object TableManagerEvent {

    case class TableStarted(
      uuid: String,
      playerId: PlayerId,
      isPrivate: Boolean,
      timestamp: Instant,
    ) extends TableManagerEvent

    case class TableClosed(tableId: TableId) extends TableManagerEvent with TableSpecificEvent

    case class PlayerAdded(tableId: TableId, playerId: PlayerId) extends TableManagerEvent with TableSpecificEvent

    case class PlayerRemoved(tableId: TableId, playerId: PlayerId) extends TableManagerEvent with TableSpecificEvent

    case class SpectatorAdded(tableId: TableId, playerId: PlayerId) extends TableManagerEvent with TableSpecificEvent

    case class SpectatorRemoved(tableId: TableId, playerId: PlayerId) extends TableManagerEvent with TableSpecificEvent

  }
}
