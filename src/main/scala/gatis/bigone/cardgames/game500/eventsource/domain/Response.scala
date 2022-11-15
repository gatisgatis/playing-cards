package gatis.bigone.cardgames.game500.eventsource.domain

import gatis.bigone.cardgames.game500.eventsource.domain.Domain.{Table, TableInfo}
import gatis.bigone.cardgames.game500.game.domain.{Game, Phase, Round}
import gatis.bigone.domain.PlayerId

trait Response

object Response {

  case class GetTableResponse(table: Table) extends Response

  case class GetTablesResponse(tables: List[TableInfo]) extends Response

  case object StartTableResponse extends Response

  case object CloseTableResponse extends Response

  case class AddPlayerResponse(playerId: PlayerId) extends Response

  case class RemovePlayerResponse(playerId: PlayerId) extends Response

  case class AddSpectatorResponse(playerId: PlayerId) extends Response

  case class RemoveSpectatorResponse(playerId: PlayerId) extends Response

  case class UpdatePlayerOnlineStatusResponse(playerId: PlayerId, isOnline: Boolean) extends Response

  case class AgreeToStartGameResponse(playerId: PlayerId) extends Response

  case class StartGameResponse(game: Game) extends Response

  case class StartRoundResponse(game: Game) extends Response

  // it could be optimised by sending only affected fields from Round
  case class MakeBidResponse(round: Round, phase: Phase) extends Response

  case class TakeCardsResponse(round: Round, phase: Phase)

  case class GiveUpResponse(game: Game) extends Response

  case class PassCards(round: Round, phase: Phase) extends Response

  case class PlayCardResponse(round: Round, phase: Phase) extends Response

  case class FinishRoundResponse(game: Game) extends Response

  case class FinishGameResponse(game: Game) extends Response

}
