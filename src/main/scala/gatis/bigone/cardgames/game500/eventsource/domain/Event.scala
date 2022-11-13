package gatis.bigone.cardgames.game500.eventsource.domain

import gatis.bigone.cardgames.game500.eventsource.domain.Domain.{Table, TableId}
import gatis.bigone.domain.PlayerId

import java.time.Instant

object Event {

  sealed trait TableEvent

  object TableEvent {
    case class TableStarted(table: Table) extends TableEvent
    case class PlayerJoined(timestamp: Instant, playerId: PlayerId) extends TableEvent
    case class PlayerLeft(timestamp: Instant) extends TableEvent
    case class PlayerStatusUpdated(timestamp: Instant) extends TableEvent
    case class AgreedToStartGame(timestamp: Instant) extends TableEvent
    case class GameFinished(timestamp: Instant) extends TableEvent

    // game events
    case class RoundStarted(timestamp: Instant) extends TableEvent
    case class BidMade(timestamp: Instant, bid: Int) extends TableEvent
    case class CardsTaken(timestamp: Instant) extends TableEvent
    case class GaveUp(timestamp: Instant) extends TableEvent
    case class CardsPassed(timestamp: Instant) extends TableEvent
    case class CardPlayed(timestamp: Instant) extends TableEvent
    case class RoundFinished(timestamp: Instant) extends TableEvent
  }

  sealed trait TableManagerEvent

  object TableManagerEvent {
    case class TableStarted(
      uuid: String,
      playerId: PlayerId,
      isPrivate: Boolean,
      timestamp: Instant,
    ) extends TableManagerEvent
    case class TableClosed(id: TableId) extends TableManagerEvent
    case class PlayerJoined(tableId: TableId, playerId: PlayerId) extends TableManagerEvent
    case class PlayerLeft(tableId: TableId, playerId: PlayerId) extends TableManagerEvent
  }
}
