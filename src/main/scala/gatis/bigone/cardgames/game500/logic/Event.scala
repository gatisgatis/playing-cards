package gatis.bigone.cardgames.game500.logic

import gatis.bigone.cardgames.game500.domain.PlayerIndex
import gatis.bigone.domain.PlayerId

import java.time.Instant

object Event {

  trait TableEvent
  object TableEvent {
    case class TableStarted(table: Table) extends TableEvent
    case class PlayerJoined(timestamp: Instant, playerId: PlayerId) extends TableEvent
    case class PlayerLeft(timestamp: Instant) extends TableEvent
    case class RoundStarted(timestamp: Instant) extends TableEvent
    case class BidMade(timestamp: Instant, player: PlayerIndex, bid: Int) extends TableEvent
    case class CardsTaken(timestamp: Instant) extends TableEvent
    case class CardsPassed(timestamp: Instant) extends TableEvent
    case class CardPlayed(timestamp: Instant) extends TableEvent
    case class RoundFinished(timestamp: Instant) extends TableEvent
    case class GameFinished(timestamp: Instant) extends TableEvent
  }

  trait TableManagerEvent
  object TableManagerEvent {
    case class TableStarted(id: String) extends TableManagerEvent
    case class TableClosed(id: String) extends TableManagerEvent
  }
}
