package gatis.bigone.cardgames.game500.eventsource.domain

import akka.actor.typed.ActorRef
import gatis.bigone.cardgames.game500.ErrorG500
import gatis.bigone.cardgames.game500.eventsource.domain.Domain.{TableId, TablesFilter}
import gatis.bigone.cardgames.game500.game.domain.Card
import gatis.bigone.domain.PlayerId

import java.time.Instant

trait Command {
  val replyTo: ActorRef[Either[ErrorG500, Response]]
}

trait TableSpecificCommand {
  val tableId: TableId
}

trait TableListNotAffectingCommand

trait PlayerGameActionCommand {
  val playerId: PlayerId
}

trait RoundProgressCommand {
  val timestamp: Instant
}

object Command {

  case class GetTable( // kind of init request after browser refresh or after log-out/log-in
    tableId: TableId,
    playerId: PlayerId, // this might be a spectator. in that case no cards are known...
    replyTo: ActorRef[Either[ErrorG500, Response]],
  ) extends Command
      with TableSpecificCommand

  case class GetTables(
    filter: TablesFilter,
    replyTo: ActorRef[Either[ErrorG500, Response]],
  ) extends Command

  case class StartTable(
    uuid: String, // used for table-actor id and table id
    playerId: PlayerId,
    isPrivate: Boolean,
    timestamp: Instant,
    replyTo: ActorRef[Either[ErrorG500, Response]],
  ) extends Command

  case class CloseTable(
    tableId: TableId,
    playerId: PlayerId,
    replyTo: ActorRef[Either[ErrorG500, Response]],
  ) extends Command
      with TableSpecificCommand

  case class AddPlayer(
    tableId: TableId,
    playerId: PlayerId,
    timestamp: Instant,
    replyTo: ActorRef[Either[ErrorG500, Response]],
  ) extends Command
      with TableSpecificCommand

  case class RemovePlayer(
    tableId: TableId,
    playerId: PlayerId,
    timestamp: Instant,
    replyTo: ActorRef[Either[ErrorG500, Response]],
  ) extends Command
      with TableSpecificCommand

  case class AddSpectator(
    tableId: TableId,
    playerId: PlayerId,
    replyTo: ActorRef[Either[ErrorG500, Response]],
  ) extends Command
      with TableSpecificCommand

  case class RemoveSpectator(
    tableId: TableId,
    playerId: PlayerId,
    replyTo: ActorRef[Either[ErrorG500, Response]],
  ) extends Command
      with TableSpecificCommand

  case class UpdatePlayerOnlineStatus(
    // if player logs out, front-end client should send request for all tables where player is present...
    tableIds: List[TableId],
    playerId: PlayerId,
    isOnline: Boolean,
    timestamp: Instant,
    replyTo: ActorRef[Either[ErrorG500, Response]],
  ) extends Command

  case class AgreeToStartGame(
    tableId: TableId,
    playerId: PlayerId,
    timestamp: Instant,
    replyTo: ActorRef[Either[ErrorG500, Response]],
  ) extends Command
      with TableSpecificCommand
      with TableListNotAffectingCommand

  // auto-called by table-actor. Or maybe only Event auto produced...?
  case class StartGame(
    tableId: TableId,
    timestamp: Instant,
    replyTo: ActorRef[Either[ErrorG500, Response]],
  ) extends Command
      with TableSpecificCommand
      with TableListNotAffectingCommand

  // auto-called by table-actor
  case class StartRound(
    tableId: TableId,
    timestamp: Instant,
    replyTo: ActorRef[Either[ErrorG500, Response]],
  ) extends Command
      with TableSpecificCommand
      with TableListNotAffectingCommand
      with RoundProgressCommand

  case class MakeBid(
    tableId: TableId,
    playerId: PlayerId,
    bid: Int,
    timestamp: Instant,
    replyTo: ActorRef[Either[ErrorG500, Response]],
  ) extends Command
      with TableSpecificCommand
      with TableListNotAffectingCommand
      with PlayerGameActionCommand
      with RoundProgressCommand

  case class TakeCards(
    tableId: TableId,
    playerId: PlayerId,
    timestamp: Instant,
    replyTo: ActorRef[Either[ErrorG500, Response]],
  ) extends Command
      with TableSpecificCommand
      with TableListNotAffectingCommand
      with PlayerGameActionCommand
      with RoundProgressCommand

  case class GiveUp( // other players gets 50/50, you loose.
    tableId: TableId,
    playerId: PlayerId,
    timestamp: Instant,
    replyTo: ActorRef[Either[ErrorG500, Response]],
  ) extends Command
      with TableSpecificCommand
      with TableListNotAffectingCommand
      with PlayerGameActionCommand
      with RoundProgressCommand

  case class PassCards(
    tableId: TableId,
    playerId: PlayerId,
    cardToLeft: Card,
    cardToRight: Card,
    timestamp: Instant,
    replyTo: ActorRef[Either[ErrorG500, Response]],
  ) extends Command
      with TableSpecificCommand
      with TableListNotAffectingCommand
      with PlayerGameActionCommand
      with RoundProgressCommand

  case class PlayCard(
    tableId: TableId,
    playerId: PlayerId,
    card: Card,
    timestamp: Instant,
    replyTo: ActorRef[Either[ErrorG500, Response]],
  ) extends Command
      with TableSpecificCommand
      with TableListNotAffectingCommand
      with PlayerGameActionCommand
      with RoundProgressCommand

  // auto called by table-actor
  case class FinishRound(
    tableId: TableId,
    timestamp: Instant,
    replyTo: ActorRef[Either[ErrorG500, Response]],
  ) extends Command
      with TableSpecificCommand
      with TableListNotAffectingCommand
      with RoundProgressCommand

//  auto-called by table-actor, when this happens, send game result somewhere for statistics....
  case class FinishGame(
    tableId: TableId,
    timestamp: Instant,
    replyTo: ActorRef[Either[ErrorG500, Response]],
  ) extends Command
      with TableSpecificCommand
      with TableListNotAffectingCommand
}
