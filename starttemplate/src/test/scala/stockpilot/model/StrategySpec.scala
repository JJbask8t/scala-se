package stockpilot.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class StrategySpec extends AnyWordSpec with Matchers {
  "Sorting Strategies" should {
    val s1     = Stock("B_TICKER", 1, 1, 20.0, 0)
    val s2     = Stock("A_TICKER", 1, 1, 10.0, 0)
    val stocks = List(s1, s2)

    "sort by ticker alphabetically (SortByTicker)" in {
      val sorted = SortByTicker.sort(stocks)
      sorted.map(_.ticker) shouldBe List("A_TICKER", "B_TICKER")
    }

    "sort by price ascending (SortByPriceAsc)" in {
      val sorted = SortByPriceAsc.sort(stocks)
      sorted.map(_.price) shouldBe List(10.0, 20.0)
    }

    "sort by price descending (SortByPriceDesc)" in {
      val sorted = SortByPriceDesc.sort(stocks)
      sorted.map(_.price) shouldBe List(20.0, 10.0)
    }

  }

}
