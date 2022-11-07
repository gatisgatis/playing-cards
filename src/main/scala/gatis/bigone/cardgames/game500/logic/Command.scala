package gatis.bigone.cardgames.game500.logic

import akka.actor.typed.ActorRef
import gatis.bigone.cardgames.game500.logic.Domain.TableId
import gatis.bigone.domain.PlayerId

import java.time.Instant

trait Command

object Command {

  case class StartTable(
    uuid: String, // used for actor id and table id
    playerId: PlayerId,
    timestamp: Instant,
    replyTo: ActorRef[Response],
  ) extends Command

  case class RemoveTable(
    tableId: TableId,
    replyTo: ActorRef[Response],
  )

  case class AddPlayer(
    playerId: PlayerId,
    tableId: TableId,
    timestamp: Instant,
    replyTo: ActorRef[Response],
  ) extends Command

  case class KickPlayer(
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
