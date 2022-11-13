package gatis.bigone.cardgames.game500

// here should be only visible api from outside
object FakeServer {

  // receives request, parses it to a command. receives response. parses it to json and sends back.

  // init request - get table state after browser refresh etc..
  // GetTable

  // get all tables (with filter values)
  // GetTables

  // create table
  // StartTable

  // join table
  // AddPlayer

  // leave table
  // KickPlayer

  // update player status for table
  // UpdatePlayerStatus

  // agree to start game
  // AgreeToStartGame

  // do move
  // one of MakeBid,TakeCards,PassCards, GiveUp, PlayCard

  // destroy table ???
  // RemoveTable
}
