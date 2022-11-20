package gatis.bigone.utils

import scala.util.Random

object Utils {

  implicit class SetOps[A](set: Set[A]) {
    // unsafe - throws on empty set...
    def randomPick: A = {
      val index = Random.nextInt(set.size)
      set.toVector(index)
    }
  }

  implicit class ListOps[A](list: List[A]) {
    def remove(el: A): List[A] = list.diff(List(el))
  }

}
