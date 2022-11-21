package gatis.bigone.cardgames.game500.eventsource.logic

import akka.actor.typed.ActorRef
import akka.persistence.typed.scaladsl.Effect
import gatis.bigone.cardgames.game500.ErrorG500
import gatis.bigone.cardgames.game500.eventsource.actors.TableActor.State
import gatis.bigone.cardgames.game500.eventsource.domain.Domain.{TableId, TableInfo}
import gatis.bigone.cardgames.game500.eventsource.domain.Event.TableManagerEvent
import gatis.bigone.cardgames.game500.eventsource.domain.Response
import gatis.bigone.domain.PlayerId

object TableManagerHelpers {

  def createSpectatorAddedEvent(
    tableInfo: TableInfo,
    tableId: TableId,
    playerId: PlayerId,
    replyTo: ActorRef[Either[ErrorG500, Response]],
  ): Effect[TableManagerEvent, State] = ???

  // chechk if this player not already at the specatotor list

}
