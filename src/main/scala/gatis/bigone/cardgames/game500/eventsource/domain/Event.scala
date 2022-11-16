package gatis.bigone.cardgames.game500.eventsource.domain

import gatis.bigone.cardgames.game500.eventsource.domain.Domain.{Table, TableId}
import gatis.bigone.cardgames.game500.game.domain.{Card, Game}
import gatis.bigone.domain.PlayerId

import java.time.Instant

object Event {

  sealed trait TableEvent

  sealed trait GameEvent {
    val timestamp: Instant
    val game: Game
  }

  object TableEvent {
    case class TableStarted(table: Table) extends TableEvent
    case class PlayerJoined(timestamp: Instant, playerId: PlayerId) extends TableEvent
    case class PlayerLeft(timestamp: Instant, playerId: PlayerId) extends TableEvent
    case class SpectatorJoined(playerId: PlayerId) extends TableEvent
    case class SpectatorLeft(playerId: PlayerId) extends TableEvent
    case class PlayerStatusUpdated(timestamp: Instant, playerId: PlayerId, isOnline: Boolean) extends TableEvent
    case class AgreedToStartGame(timestamp: Instant, playerId: PlayerId) extends TableEvent
    case class GameStarted(timestamp: Instant) extends TableEvent
    case class GameFinished(timestamp: Instant) extends TableEvent

    // game events ... maybe change them to one single event GameActionEvent ???
    case class RoundStarted(timestamp: Instant, game: Game) extends TableEvent with GameEvent
    case class BidMade(timestamp: Instant, game: Game) extends TableEvent with GameEvent
    case class CardsTaken(timestamp: Instant, game: Game) extends TableEvent with GameEvent
    case class GaveUp(timestamp: Instant, game: Game) extends TableEvent with GameEvent
    case class CardsPassed(timestamp: Instant, game: Game) extends TableEvent with GameEvent
    case class CardPlayed(timestamp: Instant, game: Game) extends TableEvent with GameEvent
    case class RoundFinished(timestamp: Instant, game: Game) extends TableEvent with GameEvent

    case class GameActionMadeEvent(timestamp: Instant, game: Game) extends TableEvent with GameEvent
  }

  sealed trait TableManagerEvent

  sealed trait TableSpecificEvent {
    val tableId: TableId
  }

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
