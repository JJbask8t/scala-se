package stockpilot.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class StrategySpec extends AnyWordSpec with Matchers {
  val s1 = Stock("A", 10.0, 1.0, 100.0) // Exepnsive
  val s2 = Stock("B", 10.0, 1.0, 10.0) // Cheap
  val s3 = Stock("C", 10.0, 1.0, 50.0) // Middle

  val list = List(s3, s1, s2)

  "SortByTicker" should {
    "sort stocks alphabetically" in {
      SortByTicker.sort(list) shouldBe List(s1, s2, s3) // A, B, C
    }
  }

  "SortByPriceAsc" should {
    "sort stocks by price ascending" in {
      SortByPriceAsc.sort(list) shouldBe List(s2, s3, s1) // 10, 50, 100
    }
  }
}
