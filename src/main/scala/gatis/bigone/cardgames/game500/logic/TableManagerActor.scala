package gatis.bigone.cardgames.game500.logic

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{Effect, EventSourcedBehavior}
import gatis.bigone.cardgames.game500.logic.Command.*
import gatis.bigone.cardgames.game500.logic.Domain.{Error, TableId}
import gatis.bigone.cardgames.game500.logic.Event.TableManagerEvent
import gatis.bigone.cardgames.game500.logic.Event.TableManagerEvent.TableStarted
import gatis.bigone.cardgames.game500.logic.Response.TestResponse2

class TableManagerActor {

  type State = Map[TableId, ActorRef[Command]]

  def commandHandler(context: ActorContext[Command]): (State, Command) => Effect[TableManagerEvent, State] =
    (state: State, command: Command) =>
      command match {
        case StartTable(id, _, _, _) =>
          val tableActor = context.spawn(TableActor(id), id) // creates new table actor
          val event = TableStarted(id = id)
          Effect
            .persist(event)
            .thenReply(tableActor)(_ => command)
        case AddPlayer(_, tableId, _, replyTo) =>
          state.get(tableId) match {
            case Some(ta) =>
              Effect.reply(ta)(command)
            case None =>
              Effect.reply(replyTo)(TestResponse2(Left(Error(code = "ss", msg = "dsdasdad"))))
          }
        case _ => ???
      }

  def eventHandler(context: ActorContext[Command]): (State, TableManagerEvent) => State =
    (state: State, event: TableManagerEvent) =>
      event match {
        case TableStarted(id) =>
          // exists after command handler, does not exist at recovery mode
          val table = context.child(id).getOrElse(context.spawn(TableActor(id), id)).asInstanceOf[ActorRef[Command]]
          state.updated(TableId(id), table)
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
