package gatis.bigone.cardgames.game500.eventsource.logic

import akka.actor.typed.ActorRef
import akka.persistence.typed.scaladsl.Effect
import gatis.bigone.cardgames.game500.ErrorG500
import gatis.bigone.cardgames.game500.eventsource.actors.TableActor.State
import gatis.bigone.cardgames.game500.eventsource.domain.Event.TableEvent
import gatis.bigone.cardgames.game500.eventsource.domain.Event.TableEvent.GameProgressEvent
import gatis.bigone.cardgames.game500.eventsource.domain.Response
import gatis.bigone.cardgames.game500.eventsource.domain.Response.GameProgressResponse
import gatis.bigone.cardgames.game500.game.domain.{Action, Game}

import java.time.Instant

object TableHelpers {

  private[eventsource] def handleGameProgressCommand(
    game: Either[ErrorG500, Game],
    action: Action,
    timestamp: Instant,
    replyTo: ActorRef[Either[ErrorG500, Response]],
  ): Effect[TableEvent, State] =
    game match {
      case Left(error) =>
        Effect.reply(replyTo)(Left(error))
      case Right(gameUpdated) =>
        Effect
          .persist(GameProgressEvent(timestamp, gameUpdated, action))
          .thenReply(replyTo)(_ => Right(GameProgressResponse(gameUpdated)))
    }

}
