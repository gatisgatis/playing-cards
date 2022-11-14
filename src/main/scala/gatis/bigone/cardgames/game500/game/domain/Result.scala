package gatis.bigone.cardgames.game500.game.domain

case class Result(value: String) {
  def update(value: String): Result = Result(this.value + " " + value)
}

object Result {
  def create(value: String = ""): Result = Result(value)
}
