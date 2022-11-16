package gatis.bigone.cardgames.game500.eventsource.actors

import akka.actor.typed.{ActorRef, Behavior}
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{Effect, EventSourcedBehavior}
import gatis.bigone.cardgames.game500.ErrorG500
import gatis.bigone.cardgames.game500.eventsource.domain.{Command, PlayerGameActionCommand, Response}
import gatis.bigone.cardgames.game500.eventsource.domain.Command._
import gatis.bigone.cardgames.game500.eventsource.domain.Domain.{PlayerInfo, Table, TableId}
import gatis.bigone.cardgames.game500.eventsource.domain.Event.{GameEvent, TableEvent}
import gatis.bigone.cardgames.game500.eventsource.domain.Event.TableEvent._
import gatis.bigone.cardgames.game500.eventsource.domain.Response._
import gatis.bigone.cardgames.game500.game.domain.GameError.DefaultGameError
import gatis.bigone.cardgames.game500.game.domain.Game
import gatis.bigone.cardgames.game500.game.logic.{
  FinishRoundAction,
  GiveUpAction,
  MakeBidAction,
  PassCardsAction,
  PlayCardAction,
  TakeCardsAction,
}
import gatis.bigone.utils.Utils.SetOps

import java.time.Instant

object TableActor {

  private def handleRoundProgressCommand(
    game: Either[ErrorG500, Game],
    timestamp: Instant,
    replyTo: ActorRef[Either[ErrorG500, Response]],
  ): Effect[TableEvent, State] =
    game match {
      case Left(error) =>
        Effect.reply(replyTo)(Left(error))
      case Right(gameUpdated) =>
        Effect
          .persist(GameActionMadeEvent(timestamp, gameUpdated))
          .thenReply(replyTo)(_ => Right(RoundProgressResponse(gameUpdated)))
    }

  type State = Table

  val commandHandler: (State, Command) => Effect[TableEvent, State] = (state: State, command: Command) =>
    command match {
      case GetTable(_, _, replyTo) =>
        Effect.reply(replyTo)(Right(GetTableResponse(state)))

      case StartTable(uuid, playerId, _, timestamp, replyTo) =>
        val table = Table.create(
          timestamp = timestamp,
          tableId = TableId(uuid),
          creator = playerId,
        )
        Effect
          .persist(TableStarted(table))
          .thenReply(replyTo)(_ => Right(StartTableResponse))

      case AddPlayer(_, playerId, timestamp, replyTo) =>
        Effect
          .persist(PlayerJoined(timestamp, playerId))
          .thenReply(replyTo)(_ => Right(AddPlayerResponse(playerId)))

      case RemovePlayer(_, playerId, timestamp, replyTo) =>
        Effect
          .persist(PlayerLeft(timestamp, playerId))
          .thenReply(replyTo)(_ => Right(RemovePlayerResponse(playerId)))

      case AddSpectator(_, playerId, replyTo) =>
        Effect
          .persist(SpectatorJoined(playerId))
          .thenReply(replyTo)(_ => Right(AddSpectatorResponse(playerId)))

      case RemoveSpectator(_, playerId, replyTo) =>
        Effect
          .persist(SpectatorLeft(playerId))
          .thenReply(replyTo)(_ => Right(RemoveSpectatorResponse(playerId)))

      case UpdatePlayerOnlineStatus(_, playerId, isOnline, timestamp, replyTo) =>
        Effect
          .persist(PlayerStatusUpdated(timestamp, playerId, isOnline))
          .thenReply(replyTo)(_ => Right(UpdatePlayerOnlineStatusResponse(playerId, isOnline)))

      case AgreeToStartGame(_, playerId, timestamp, replyTo) =>
        Effect
          .persist(AgreedToStartGame(timestamp, playerId))
          .thenReply(replyTo)(_ => Right(AgreeToStartGameResponse(playerId)))

      case pgaCommand: PlayerGameActionCommand =>
        state.players.get(pgaCommand.playerId) match {
          case Some(playerInfo) if playerInfo.index == state.game.round.activeIndex =>
            pgaCommand match {
              case MakeBid(_, _, bid, timestamp, replyTo) =>
                handleRoundProgressCommand(
                  game = MakeBidAction(state.game, bid),
                  timestamp = timestamp,
                  replyTo = replyTo,
                )

              case TakeCards(_, _, timestamp, replyTo) =>
                handleRoundProgressCommand(
                  game = TakeCardsAction(state.game),
                  timestamp = timestamp,
                  replyTo = replyTo,
                )

              case GiveUp(_, _, timestamp, replyTo) =>
                handleRoundProgressCommand(
                  game = GiveUpAction(state.game),
                  timestamp = timestamp,
                  replyTo = replyTo,
                )

              case PassCards(_, _, leftCard, rightCard, timestamp, replyTo) =>
                handleRoundProgressCommand(
                  game = PassCardsAction(state.game, leftCard, rightCard),
                  timestamp = timestamp,
                  replyTo = replyTo,
                )

              case PlayCard(_, _, card, timestamp, replyTo) =>
                handleRoundProgressCommand(
                  game = PlayCardAction(state.game, card),
                  timestamp = timestamp,
                  replyTo = replyTo,
                )

            }
          case None =>
            Effect.reply(pgaCommand.replyTo)(Left(DefaultGameError(msg = "This player is not at this table")))
          case _ => Effect.reply(pgaCommand.replyTo)(Left(DefaultGameError(msg = "Not player's turn")))
        }

      case StartRound(tableId, timestamp, replyTo) => ???

      case FinishRound(_, timestamp, replyTo) =>
        handleRoundProgressCommand(
          game = FinishRoundAction(state.game),
          timestamp = timestamp,
          replyTo = replyTo,
        )
    }

  val eventHandler: (Table, TableEvent) => Table = (state: Table, event: TableEvent) =>
    event match {
      case TableStarted(table) =>
        table
      case PlayerJoined(timestamp, playerId) =>
        val index = state.availablePlayerIndexes.randomPick
        val playerInfo = PlayerInfo(index = index)
        val players = state.players.updated(playerId, playerInfo)
        state.copy(lastActivity = timestamp, players = players)
      case gameEvent: GameEvent =>
        state.copy(
          game = gameEvent.game,
          lastActivity = gameEvent.timestamp,
        )

    }

  def apply(id: String): Behavior[Command] =
    EventSourcedBehavior[Command, TableEvent, Table](
      persistenceId = PersistenceId.ofUniqueId(id),
      emptyState = Table.empty,
      commandHandler = commandHandler,
      eventHandler = eventHandler,
    )

}
