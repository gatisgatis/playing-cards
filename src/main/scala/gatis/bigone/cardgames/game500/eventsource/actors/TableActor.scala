package gatis.bigone.cardgames.game500.eventsource.actors

import akka.actor.typed.Behavior
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{Effect, EventSourcedBehavior}
import gatis.bigone.cardgames.game500.eventsource.domain.Command
import gatis.bigone.cardgames.game500.eventsource.domain.Command._
import gatis.bigone.cardgames.game500.eventsource.domain.Domain.{PlayerInfo, Table, TableId}
import gatis.bigone.cardgames.game500.eventsource.domain.Event.TableEvent
import gatis.bigone.cardgames.game500.eventsource.domain.Event.TableEvent._
import gatis.bigone.cardgames.game500.eventsource.domain.Response._
import gatis.bigone.utils.Utils.SetOps

object TableActor {

  type State = Table

  val commandHandler: (State, Command) => Effect[TableEvent, State] = (state: State, command: Command) =>
    command match {
      // validation for this command handled at table manager level
      case StartTable(uuid, playerId, _, timestamp, replyTo) =>
        val table = Table.create(
          timestamp = timestamp,
          tableId = TableId(uuid),
          creator = playerId,
        )
        Effect
          .persist(TableStarted(table))
          .thenReply(replyTo)(_ => Right(TempResponse("table started")))
      case AddPlayer(_, playerId, timestamp, replyTo) =>
        // validation for this command handled at table manager level
        Effect
          .persist(PlayerJoined(timestamp, playerId))
          .thenReply(replyTo)(_ => Right(TempResponse("add player command handled")))
      case _ => ???
    }

  val eventHandler: (Table, TableEvent) => Table = (state: Table, event: TableEvent) =>
    event match {
      case TableStarted(table) =>
        table
      case PlayerJoined(timestamp, playerId) =>
        val index = state.availablePlayerIndexes.randomPick
        val playerInfo = PlayerInfo(index = index)
        val players = state.players.updated(playerId, playerInfo)
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
