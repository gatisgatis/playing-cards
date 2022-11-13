package gatis.bigone.cardgames.game500.eventsource.domain

trait Response // change back to Response. indexing glitch

object Response {
  case class TempResponse(msg: String) extends Response
}
