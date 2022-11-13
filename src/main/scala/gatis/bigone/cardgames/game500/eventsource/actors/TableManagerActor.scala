package gatis.bigone.cardgames.game500.eventsource.actors

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{Effect, EventSourcedBehavior}
import gatis.bigone.cardgames.game500.eventsource.domain.Command
import gatis.bigone.cardgames.game500.eventsource.domain.Command._
import gatis.bigone.cardgames.game500.eventsource.domain.Domain.{TableId, TableInfo}
import gatis.bigone.cardgames.game500.eventsource.domain.Event.TableManagerEvent
import gatis.bigone.cardgames.game500.eventsource.domain.Event.TableManagerEvent._
import gatis.bigone.cardgames.game500.eventsource.domain.Response.TempResponse

object TableManagerActor {

  type State = Map[TableId, TableInfo]

  def commandHandler(context: ActorContext[Command]): (State, Command) => Effect[TableManagerEvent, State] =
    (state: State, command: Command) =>
      command match {
        case GetTable(tableId, playerId, replyTo) => ???
        case GetTables(filter, replyTo) => ???
        case StartTable(uuid, playerId, isPrivate, timestamp, _) =>
          // some validation needed maybe? can one player open 10000 different tables? before persisting this event
          // we must be sure that this command won't fail at table actor...
          // check if table with that tableId is not already created? unlikely scenario though
          val tableActor = context.spawn(TableActor(uuid), uuid) // creates new table actor
          val event = TableStarted(uuid = uuid, playerId = playerId, isPrivate = isPrivate, timestamp = timestamp)
          Effect
            .persist(event)
            .thenReply(tableActor)(_ => command)
        case AddPlayer(tableId, _, _, replyTo) =>
          state.get(tableId) match {
            case Some(tableInfo) =>
              // some validation if this player can actually join the table...
              Effect.reply(tableInfo.tableActor)(command)
            case None =>
              Effect.reply(replyTo)(TempResponse("added player"))
          }
        case _ => ???
      }

  def eventHandler(context: ActorContext[Command]): (State, TableManagerEvent) => State =
    (state: State, event: TableManagerEvent) =>
      event match {
        case TableStarted(uuid, playerId, isPrivate, timestamp) =>
          // table actor exists after command handler, does not exist at recovery mode (spawn needed)
          val tableActor =
            context.child(uuid).getOrElse(context.spawn(TableActor(uuid), uuid)).asInstanceOf[ActorRef[Command]]
          val tableInfo = TableInfo(
            tableActor = tableActor,
            players = List(playerId),
            isPrivate = isPrivate,
            createdAt = timestamp,
          )
          state ++ (TableId(uuid) -> tableInfo)
        case PlayerJoined(tableId, playerId) =>
          state.get(tableId) match {
            case Some(tableInfo) =>
              val players = tableInfo.players :+ playerId
              state.updated(tableId, tableInfo.copy(players = players))
            case None => state // should log something here. this is unexpected case...
          }
        case _ => ???
      }

  def apply(): Behavior[Command] = Behaviors.setup { context =>
    EventSourcedBehavior[Command, TableManagerEvent, State](
      persistenceId = PersistenceId.ofUniqueId("game500-table-manager"),
      emptyState = Map.empty,
      commandHandler = commandHandler(context),
      eventHandler = eventHandler(context),
    )
  }

}
