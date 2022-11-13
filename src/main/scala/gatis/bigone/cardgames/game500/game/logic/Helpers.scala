package gatis.bigone.cardgames.game500.game.logic

import gatis.bigone.cardgames.game500.game.domain.Code.UnexpectedGameError
import gatis.bigone.cardgames.game500.game.domain.Error

object Helpers {

  implicit class MapOps[A, B](map: Map[A, B]) {
    def getE(key: A): Either[Error, B] =
      map.get(key).toRight(Error(code = UnexpectedGameError, message = s"Could not find value for key $key"))
  }

}
