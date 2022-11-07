package gatis.bigone.domain

// unique player identificator, used to map specific player to game's player aggregate.
// for example, in game500 this maps to PlayerInfo
case class PlayerId(value: String) extends AnyVal

object PlayerId {
  val empty: PlayerId = PlayerId("")
}
