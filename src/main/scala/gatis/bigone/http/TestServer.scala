package gatis.bigone.http

import zhttp.http._
import zhttp.service.Server
import zio._

object TestServer extends ZIOAppDefault {

  val port = 8090

  val app: UHttpApp =
    Http.collect[Request] {
      case Method.GET -> _ / "test" => Response.text("Test passed, server works")
      case Method.GET -> _ / "info" => Response.json("""{"info": "No Info"}""")
    }

  // receives all kind of requests. transfers them to according game server...

  // comunication between this and game server is through imaginary protocol...

  def run: ZIO[Any & ZIOAppArgs & Scope, Throwable, Nothing] =
    Server.start(8090, app)
}
