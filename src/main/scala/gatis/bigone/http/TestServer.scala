package gatis.bigone.http

import zhttp.http._
import zhttp.service.Server
import zio._

// Receives http request or ws message
// Understands to which service (e.g. game500) must transfer it
// Checks if this request is allowed to go there (auth check etc...)
// Adds or hides some details if necessary (not sure what...)
// Transfers enriched request

// Receives response from service
// Adds or hides some details if necessary (not sure what...)
// Sends responses to original requester and to other involved...

object TestServer extends ZIOAppDefault {

  val port = 8090

  val app: UHttpApp =
    Http.collect[Request] {
      case Method.GET -> _ / "test" => Response.text("Test passed, server works")
      case Method.GET -> _ / "info" => Response.json("""{"info": "No Info"}""")
    }

  def run: ZIO[Any & ZIOAppArgs & Scope, Throwable, Nothing] =
    Server.start(8090, app)
}
