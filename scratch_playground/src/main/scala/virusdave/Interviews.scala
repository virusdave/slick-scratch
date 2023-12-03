package virusdave

import scala.collection.mutable

object Interviews {


  def main(args: Array[String]): Unit = {

    def compare_trades(
        house_trades: Array[String],
        street_trades: Array[String]): Array[String] = {

      val newHouse = house_trades.diff(street_trades).toSeq.sorted
      val newStreet = street_trades.diff(house_trades).toSeq.sorted

      val nextNewHouse = newHouse.diffBy(newStreet, _.dropRight(6))
      val nextNewStreet = newStreet.diffBy(newHouse, _.dropRight(6))

      //

      val (bHouse, sHouse) = nextNewHouse.partition(_.drop(5).take(1) == "B")
      val (bStreet, sStreet) = nextNewHouse.partition(_.drop(5).take(1) == "B")

      // Ugly, don't look here
      val newBHouse = bHouse.diffBy(sHouse, x => x.take(5) + x.slice(7, 11))
      println(s"bHouse: ${bHouse}")
      println(s"sHouse: ${sHouse}")
      println(s"newBHouse: ${newBHouse}")
      val newSHouse = sHouse.diffBy(bHouse, x => x.take(5) + x.slice(7, 11))
      val newBStreet = bStreet.diffBy(sStreet, x => x.take(5) + x.slice(7, 11))
      val newSStreet = sStreet.diffBy(bStreet, x => x.take(5) + x.slice(7, 11))

      (newBHouse ++ newSHouse ++ newBStreet ++ newSStreet).sorted.toArray
    }

    implicit class SeqOps[A](me: Seq[A]) {
      private def occCounts[B](sq: Seq[B]): mutable.Map[B, Int] = {
        val occ = new mutable.HashMap[B, Int] { override def default(k: B) = 0 }
        for (y <- sq) occ(y) += 1
        occ
      }

      def diffBy[B](you: Seq[A], fn: A => B): Seq[A] = {
        val occ = occCounts(you.map(fn))
        val b = Seq.newBuilder[A]
        for (x <- me) {
          val ox = occ(fn(x))  // Avoid multiple map lookups
          if (ox == 0) b += x
          else occ(fn(x)) = ox - 1
        }
        b.result()
      }
    }
  }
}