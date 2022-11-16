package gatis.bigone.cardgames.game500.game.logic

import gatis.bigone.cardgames.game500.ErrorG500
import gatis.bigone.cardgames.game500.game.domain.GameError.DefaultGameError

object Helpers {

  implicit class MapOps[A, B](map: Map[A, B]) {
    def getE(key: A): Either[ErrorG500, B] =
      map.get(key).toRight(DefaultGameError(msg = s"Could not find value for key $key"))
  }

}
