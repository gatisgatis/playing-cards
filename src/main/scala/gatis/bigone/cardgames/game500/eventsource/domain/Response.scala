package gatis.bigone.cardgames.game500.eventsource.domain

import gatis.bigone.cardgames.game500.eventsource.domain.Domain.{PlayerParams, Table, TableInfo}
import gatis.bigone.cardgames.game500.game.domain.Game
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

  case class UpdatePlayerParamsResponse(playerId: PlayerId, params: PlayerParams) extends Response

  case class GameProgressResponse(game: Game) extends Response

}
