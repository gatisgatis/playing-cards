package gatis.bigone.cardgames.game500.eventsource.actors

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{Effect, EventSourcedBehavior}
import gatis.bigone.cardgames.game500.eventsource.domain.{Command, TableListNotAffectingCommand, TableSpecificCommand}
import gatis.bigone.cardgames.game500.eventsource.domain.Command._
import gatis.bigone.cardgames.game500.eventsource.domain.Domain.{ResponseError, TableId, TableInfo}
import gatis.bigone.cardgames.game500.eventsource.domain.Event.{TableManagerEvent, TableSpecificEvent}
import gatis.bigone.cardgames.game500.eventsource.domain.Event.TableManagerEvent._
import gatis.bigone.cardgames.game500.eventsource.domain.Response.{CloseTableResponse, GetTablesResponse}

object TableManagerActor {

  type State = Map[TableId, TableInfo]

  def commandHandler(context: ActorContext[Command]): (State, Command) => Effect[TableManagerEvent, State] = {
    (state: State, command: Command) =>
      command match {
        // Handles commands which affects one specific table
        case tableSpecificCommand: TableSpecificCommand =>
          state.get(tableSpecificCommand.tableId) match {
            case Some(tableInfo) =>
              tableSpecificCommand match {
                case CloseTable(tableId, playerId, replyTo) =>
                  // TODO add validation
                  // before persisting this event we must be sure that this command won't fail at table actor...
                  // this command should be auto-called when 3rd player lefts table or table admin closes it manually
                  val event = TableClosed(tableId = tableId)
                  Effect
                    .persist(event)
                    .thenReply(replyTo)(_ => Right(CloseTableResponse))

                case AddPlayer(tableId, playerId, _, _) =>
                  // TODO add validation
                  // before persisting this event we must be sure that this command won't fail at table actor...
                  val event = PlayerAdded(tableId = tableId, playerId = playerId)
                  Effect
                    .persist(event)
                    .thenReply(tableInfo.tableActor)(_ => tableSpecificCommand)

                case RemovePlayer(tableId, playerId, _, _) =>
                  // TODO add validation
                  // before persisting this event we must be sure that this command won't fail at table actor...
                  val event = PlayerRemoved(tableId = tableId, playerId = playerId)
                  Effect
                    .persist(event)
                    .thenReply(tableInfo.tableActor)(_ => tableSpecificCommand)

                case AddSpectator(tableId, playerId, _) =>
                  // TODO add validation
                  // before persisting this event we must be sure that this command won't fail at table actor...
                  val event = SpectatorAdded(tableId = tableId, playerId = playerId)
                  Effect
                    .persist(event)
                    .thenReply(tableInfo.tableActor)(_ => tableSpecificCommand)

                case RemoveSpectator(tableId, playerId, _) =>
                  // TODO add validation
                  // before persisting this event we must be sure that this command won't fail at table actor...
                  val event = SpectatorRemoved(tableId = tableId, playerId = playerId)
                  Effect
                    .persist(event)
                    .thenReply(tableInfo.tableActor)(_ => tableSpecificCommand)

                case cmd: TableListNotAffectingCommand =>
                  Effect.reply(tableInfo.tableActor)(cmd)

                case _ =>
                  Effect.reply(tableSpecificCommand.replyTo)(
                    Left(ResponseError(s"Unexpected table specific command: $tableSpecificCommand")),
                  )
              }

            case None =>
              Effect.reply(tableSpecificCommand.replyTo)(
                Left(ResponseError(s"Table ${tableSpecificCommand.tableId} not found")),
              )
          }

        // Handles not one specific table commands

        case GetTables(filter, replyTo) =>
          // TODO refactor this to look cleaner
          val filtered =
            state
              .map { case (_, ti) => ti }
              .filter { ti =>
                if (filter.onlyWithFreeSeats) ti.players.size < 3
                else true
              }
              .filter { ti =>
                filter.playerId match {
                  case Some(playerId) => ti.players.contains(playerId)
                  case None => true
                }
              }
              .toList
          Effect.reply(replyTo)(Right(GetTablesResponse(filtered)))

        case StartTable(uuid, playerId, isPrivate, timestamp, _) =>
          // TODO add validation
          // before persisting this event we must be sure that this command won't fail at table actor...
          // check if table with that tableId is not already created? check max tables by one player?
          val tableActor = context.spawn(behavior = TableActor(uuid), name = uuid) // creates new table actor
          val event = TableStarted(uuid = uuid, playerId = playerId, isPrivate = isPrivate, timestamp = timestamp)
          Effect
            .persist(event)
            .thenReply(tableActor)(_ => command)

        case UpdatePlayerOnlineStatus(tableIds, playerId, isOnline, timestamp, replyTo) => ??? // TODO implement

        case _ =>
          Effect.reply(command.replyTo)(
            Left(ResponseError(s"Unexpected command: $command")),
          )
      }
  }

  def eventHandler(context: ActorContext[Command]): (State, TableManagerEvent) => State =
    (state: State, event: TableManagerEvent) =>
      event match {
        case tableSpecificEvent: TableSpecificEvent =>
          state.get(tableSpecificEvent.tableId) match {
            case Some(tableInfo) =>
              tableSpecificEvent match {
                case TableClosed(tableId) =>
                  context.stop(tableInfo.tableActor)
                  state - tableId
                case PlayerAdded(tableId, playerId) =>
                  val players = tableInfo.players :+ playerId
                  state.updated(tableId, tableInfo.copy(players = players))
                case PlayerRemoved(tableId, playerId) =>
                  val players = tableInfo.players.filterNot(_ == playerId)
                  state.updated(tableId, tableInfo.copy(players = players))
                case SpectatorAdded(tableId, playerId) =>
                  val spectators = tableInfo.spectators :+ playerId
                  state.updated(tableId, tableInfo.copy(spectators = spectators))
                case SpectatorRemoved(tableId, playerId) =>
                  val spectators = tableInfo.spectators.filterNot(_ == playerId)
                  state.updated(tableId, tableInfo.copy(spectators = spectators))
              }
            case None => state // unexpected case. should log something here
          }

        case TableStarted(uuid, playerId, isPrivate, timestamp) =>
          // table actor exists after command handler, does not exist at recovery mode (spawn needed)
          val tableActor =
            context.child(uuid).getOrElse(context.spawn(TableActor(uuid), uuid)).asInstanceOf[ActorRef[Command]]
          val tableId = TableId(uuid)
          val tableInfo = TableInfo(
            id = tableId,
            tableActor = tableActor,
            players = List(playerId),
            isPrivate = isPrivate,
            createdAt = timestamp,
          )
          state ++ (tableId -> tableInfo)
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
