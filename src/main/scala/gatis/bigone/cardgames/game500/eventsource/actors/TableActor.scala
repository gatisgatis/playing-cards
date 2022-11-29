package gatis.bigone.cardgames.game500.eventsource.actors

import akka.actor.typed.Behavior
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{Effect, EventSourcedBehavior}
import gatis.bigone.cardgames.game500.ErrorG500
import gatis.bigone.cardgames.game500.eventsource.domain.{Command, GameProgressCommand, PlayerSpecificCommand}
import gatis.bigone.cardgames.game500.eventsource.domain.Command._
import gatis.bigone.cardgames.game500.eventsource.domain.Domain.{PlayerInfo, Table, TableId}
import gatis.bigone.cardgames.game500.eventsource.domain.Event.TableEvent
import gatis.bigone.cardgames.game500.eventsource.domain.Event.TableEvent._
import gatis.bigone.cardgames.game500.eventsource.domain.Response._
import gatis.bigone.cardgames.game500.eventsource.logic.TableHelpers.handleGameProgressCommand
import gatis.bigone.cardgames.game500.game.domain.Action._
import gatis.bigone.cardgames.game500.game.domain.GameError.DefaultGameError
import gatis.bigone.cardgames.game500.game.domain.Game
import gatis.bigone.cardgames.game500.game.logic.{
  FinishRoundAction,
  GiveUpAction,
  MakeBidAction,
  PassCardsAction,
  PlayCardAction,
  StartGameAction,
  StartRoundAction,
  TakeCardsAction,
}
import gatis.bigone.utils.Utils.{ListOps, SetOps}

object TableActor {

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
        // if game has been started, must somehow save info about player who quit
        // and trigger finish round command
        // which will eventually trigger finish game command
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

      case UpdatePlayerParams(_, playerId, params, timestamp, replyTo) =>
        Effect
          .persist(PlayerParamsUpdated(timestamp, playerId, params))
          .thenReply(replyTo)(_ => Right(UpdatePlayerParamsResponse(playerId, params)))

      case psProgressCmd: PlayerSpecificCommand with GameProgressCommand =>
        state.players.get(psProgressCmd.playerId) match {
          case Some(playerInfo) if playerInfo.index == state.game.round.activeIndex =>
            psProgressCmd match {
              case MakeBid(_, _, bid, timestamp, replyTo) =>
                handleGameProgressCommand(
                  game = MakeBidAction.apply(state.game, bid),
                  timestamp = timestamp,
                  replyTo = replyTo,
                  action = BidMade,
                )

              case TakeCards(_, _, timestamp, replyTo) =>
                handleGameProgressCommand(
                  game = TakeCardsAction.apply(state.game),
                  timestamp = timestamp,
                  replyTo = replyTo,
                  action = CardsTaken,
                )

              case GiveUp(_, _, timestamp, replyTo) =>
                handleGameProgressCommand(
                  game = GiveUpAction.apply(state.game),
                  timestamp = timestamp,
                  replyTo = replyTo,
                  action = GaveUp,
                )

              case PassCards(_, _, leftCard, rightCard, timestamp, replyTo) =>
                handleGameProgressCommand(
                  game = PassCardsAction.apply(state.game, leftCard, rightCard),
                  timestamp = timestamp,
                  replyTo = replyTo,
                  action = CardsPassed,
                )

              case PlayCard(_, _, card, timestamp, replyTo) =>
                handleGameProgressCommand(
                  game = PlayCardAction.apply(state.game, card),
                  timestamp = timestamp,
                  replyTo = replyTo,
                  action = CardPlayed,
                )

            }
          case None =>
            Effect.reply(psProgressCmd.replyTo)(Left(DefaultGameError(msg = "This player is not at this table")))
          case _ => Effect.reply(psProgressCmd.replyTo)(Left(DefaultGameError(msg = "Not player's turn")))
        }

      case StartGame(_, timestamp, replyTo) =>
        val gameUpdated: Either[ErrorG500, Game] = for {
          _ <-
            if (state.players.size == 3) Right(())
            else Left(DefaultGameError(msg = "Cannot start game with less than 3 players"))
          _ <-
            if (state.players.values.forall(_.params.readyForGame)) Right(())
            else Left(DefaultGameError(msg = "Not all agreed"))
          game <- StartGameAction.apply(game = state.game)
        } yield game
        handleGameProgressCommand(
          game = gameUpdated,
          timestamp = timestamp,
          replyTo = replyTo,
          action = GameStarted,
        )

      case StartRound(_, timestamp, replyTo) =>
        handleGameProgressCommand(
          game = StartRoundAction.apply(state.game),
          timestamp = timestamp,
          replyTo = replyTo,
          action = RoundStarted,
        )

      case FinishRound(_, timestamp, replyTo) =>
        // if game.stage=Finished, then after this should trigger finishGame command!?
        handleGameProgressCommand(
          game = FinishRoundAction.apply(state.game),
          timestamp = timestamp,
          replyTo = replyTo,
          action = RoundFinished,
        )

      case FinishGame(tableId, timestamp, replyTo) => ??? // TODO
      // should change playerinfo - agreed to false for all
      // ends with GameProgressEvent and action=GameFinished

      case _ => Effect.reply(command.replyTo)(Left(DefaultGameError(msg = s"unexpected command: $command")))
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
      case PlayerLeft(timestamp, playerId) =>
        val playersUpdated = state.players.removed(playerId)
        state.copy(lastActivity = timestamp, players = playersUpdated)
      case SpectatorJoined(playerId) =>
        val spectatorsUpdated = state.spectators :+ playerId
        state.copy(spectators = spectatorsUpdated)
      case SpectatorLeft(playerId) =>
        val spectatorsUpdated = state.spectators.remove(playerId)
        state.copy(spectators = spectatorsUpdated)
      case PlayerParamsUpdated(timestamp, playerId, params) =>
        val infoUpdated = state.players(playerId).copy(params = params)
        val playersUpdated = state.players.updated(playerId, infoUpdated)
        state.copy(lastActivity = timestamp, players = playersUpdated)
      case gameProgressEvent: GameProgressEvent =>
        state.copy(
          game = gameProgressEvent.game,
          lastActivity = gameProgressEvent.timestamp,
        )

      case _ => state // TODO should log something about unexpected event etc... create unexpected method

    }

  def apply(id: String): Behavior[Command] =
    EventSourcedBehavior[Command, TableEvent, Table](
      persistenceId = PersistenceId.ofUniqueId(id),
      emptyState = Table.empty,
      commandHandler = commandHandler,
      eventHandler = eventHandler,
    )

}
