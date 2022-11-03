package gatis.bigone

import zhttp.http.*
import zhttp.service.Server
import zio.*

object TestServer extends ZIOAppDefault {

  val app: HttpApp[Any, Nothing] =
    Http.collect[Request] {
      case Method.GET -> _ / "text" => Response.text("Hello World!")
      case Method.GET -> _ / "json" =>
        Response.json("""{"greetings": "Hello World!"}""")
    }

  def run =
    Server.start(8090, app)
}
