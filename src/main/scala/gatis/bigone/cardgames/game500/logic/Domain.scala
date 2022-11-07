package gatis.bigone.cardgames.game500.logic

import gatis.bigone.cardgames.game500.domain.PlayerIndex

object Domain {

  case class PlayerInfo(index: PlayerIndex, isAtTable: Boolean = true, isOnline: Boolean = true)

  case class TableId(value: String)

  case class Error(code: String, msg: String)

}
