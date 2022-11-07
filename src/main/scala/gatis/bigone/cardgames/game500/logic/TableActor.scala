package gatis.bigone.cardgames.game500.logic

import akka.actor.typed.Behavior
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{Effect, EventSourcedBehavior}
import gatis.bigone.cardgames.game500.logic.Command.*
import gatis.bigone.cardgames.game500.logic.Domain.{PlayerInfo, TableId}
import gatis.bigone.cardgames.game500.logic.Event.TableEvent
import gatis.bigone.cardgames.game500.logic.Event.TableEvent.{PlayerJoined, TableStarted}
import gatis.bigone.cardgames.game500.logic.Response.{Info, TestResponse, TestResponse2}
import gatis.bigone.utils.Utils.SetOps

import scala.util.Random

// After persisting an event, eventhandler runs and after that responds to msg

object TableActor {

  val commandHandler: (Table, Command) => Effect[TableEvent, Table] = (state: Table, command: Command) =>
    command match {
      case StartTable(uuid, playerId, timestamp, replyTo) =>
        val table = Table.create(
          timestamp = timestamp,
          tableId = TableId(uuid),
          creator = playerId,
        )
        Effect
          .persist(TableStarted(table))
          .thenReply(replyTo)(_ => TestResponse2(Right(Info(table = table, msg = "table started good"))))
      case AddPlayer(playerId, _, timestamp, replyTo) if state.hasFreeSeats =>
        // maybe check if joined player is not the same as already at the table!? check if no restrictions for this player...
        Effect
          .persist(PlayerJoined(timestamp, playerId))
          .thenReply(replyTo)(_ => TestResponse("add player command handled"))
      case _ => ???
    }

  val eventHandler: (Table, TableEvent) => Table = (state: Table, event: TableEvent) =>
    event match {
      case TableStarted(table) =>
        table
      case PlayerJoined(timestamp, playerId) =>
        val pi = state.availablePlayerIndexes.randomPick
        val info = PlayerInfo(pi)
        val players = state.players.updated(playerId, info)
        // if three players, change phase to waiting for game to start!?
        state.copy(lastActivity = timestamp, players = players)
      case _ => ???

    }

  def apply(id: String): Behavior[Command] =
    EventSourcedBehavior[Command, TableEvent, Table](
      persistenceId = PersistenceId.ofUniqueId(id),
      emptyState = Table.empty,
      commandHandler = commandHandler,
      eventHandler = eventHandler,
    )

}
