package gatis.bigone.cardgames.game500.eventsource.domain

import akka.actor.typed.ActorRef
import gatis.bigone.cardgames.game500.eventsource.domain.Domain.{TableId, TablesFilter}
import gatis.bigone.domain.PlayerId

import java.time.Instant

trait Command

object Command {

  case class GetTable( // kind of init request after browser refresh or after log-out/log-in
    tableId: TableId,
    playerId: PlayerId, // this might be a spectator. in that case no cards are known...
    replyTo: ActorRef[Response],
  ) extends Command

  case class GetTables(
    filter: TablesFilter,
    replyTo: ActorRef[Response],
  ) extends Command

  case class UpdatePlayerOnlineStatus(
    // if player logs out, front-end client should send request for all tables where player is present...
    tableId: List[TableId],
    playerId: PlayerId,
    isOnline: Boolean,
    timestamp: Instant,
    replyTo: ActorRef[Response],
  ) extends Command

  case class StartTable(
    uuid: String, // used for table-actor id and table id
    playerId: PlayerId,
    isPrivate: Boolean,
    timestamp: Instant,
    replyTo: ActorRef[Response],
  ) extends Command

  case class RemoveTable(
    tableId: TableId,
    playerId: PlayerId,
    replyTo: ActorRef[Response],
  ) extends Command

  case class AddPlayer(
    tableId: TableId,
    playerId: PlayerId,
    timestamp: Instant,
    replyTo: ActorRef[Response],
  ) extends Command

  case class AddSpectator(
    tableId: TableId,
    playerId: PlayerId,
    replyTo: ActorRef[Response],
  )

  case class RemoveSpectator(
    tableId: TableId,
    playerId: PlayerId,
    replyTo: ActorRef[Response],
  )

  case class KickPlayer(
    tableId: TableId,
    playerId: PlayerId,
    timestamp: Instant,
    replyTo: ActorRef[Response],
  ) extends Command

  case class AgreeToStartGame(
    playerId: PlayerId,
    timestamp: Instant,
    replyTo: ActorRef[Response],
  ) extends Command

  case class StartRound(
    timestamp: Instant,
    replyTo: ActorRef[Response],
  ) extends Command

  case class MakeBid(
    timestamp: Instant,
    replyTo: ActorRef[Response],
  ) extends Command

  case class TakeCards(
    timestamp: Instant,
    replyTo: ActorRef[Response],
  ) extends Command

  case class GiveUp( // other players gets 50/50, you loose.
    timestamp: Instant,
    replyTo: ActorRef[Response],
  )

  case class PassCards(
    timestamp: Instant,
    replyTo: ActorRef[Response],
  ) extends Command

  case class PlayCard(
    timestamp: Instant,
    replyTo: ActorRef[Response],
  ) extends Command

  case class FinishRound(
    timestamp: Instant,
    replyTo: ActorRef[Response],
  ) extends Command

  // when this happens, send game result somewhere for statistics....
  case class FinishGame(
    timestamp: Instant,
    replyTo: ActorRef[Response],
  ) extends Command
}
