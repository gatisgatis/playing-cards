package gatis.bigone.cardgames.game500

// here should be only visible api from outside
object FakeServer extends App {

  trait RequestG500

  trait ResponseG500

  // receives request, parses it to a command. sends command to table manager. receives response. parses it to json and sends back.
  def handleRequest: Any = ???

}
