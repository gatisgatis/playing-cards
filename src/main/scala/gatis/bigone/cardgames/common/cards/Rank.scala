package gatis.bigone.cardgames.common.cards

sealed trait Rank {
  def toString: String
  val strength: Int
  val points: Int
}

object Rank {

  object Two extends Rank {
    override def toString: String = "2"
    override val points: Int = 0
    override val strength: Int = 2
  }

  object Three extends Rank {
    override def toString: String = "3"
    override val points: Int = 0
    override val strength: Int = 3
  }

  object Four extends Rank {
    override def toString: String = "4"
    override val points: Int = 0
    override val strength: Int = 4
  }

  object Five extends Rank {
    override def toString: String = "5"
    override val points: Int = 0
    override val strength: Int = 5
  }

  object Six extends Rank {
    override def toString: String = "6"
    override val points: Int = 0
    override val strength: Int = 6
  }

  object Seven extends Rank {
    override def toString: String = "7"
    override val points: Int = 0
    override val strength: Int = 7
  }

  object Eight extends Rank {
    override def toString: String = "8"
    override val points: Int = 0
    override val strength: Int = 8
  }

  object Nine extends Rank {
    override def toString: String = "9"
    override val points: Int = 0
    override val strength: Int = 9
  }

  object Ten extends Rank {
    override def toString: String = "T"
    override val points: Int = 10
    override val strength: Int = 10
  }

  object Jack extends Rank {
    override def toString: String = "J"
    override val points: Int = 2
    override val strength: Int = 11
  }

  object Queen extends Rank {
    override def toString: String = "Q"
    override val points: Int = 3
    override val strength: Int = 12
  }

  object King extends Rank {
    override def toString: String = "K"
    override val points: Int = 4
    override val strength: Int = 13
  }

  object Ace extends Rank {
    override def toString: String = "A"
    override val points: Int = 11
    override val strength: Int = 14
  }

  val all = Set(Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten, Jack, Queen, King, Ace)

  def fromSymbol(rank: Char): Option[Rank] =
    rank.toUpper match {
      case '2' => Some(Two)
      case '3' => Some(Three)
      case '4' => Some(Four)
      case '5' => Some(Five)
      case '6' => Some(Six)
      case '7' => Some(Seven)
      case '8' => Some(Eight)
      case '9' => Some(Nine)
      case 'T' => Some(Ten)
      case 'J' => Some(Jack)
      case 'Q' => Some(Queen)
      case 'K' => Some(King)
      case 'A' => Some(Ace)
      case _ => None
    }

}
