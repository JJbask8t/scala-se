package stockpilot.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class StockSpec extends AnyWordSpec with Matchers {

  "Stock" should {
    // Fair Value = EPS * 15
    // Case 1: Buy (Price < FV)
    "return BUY verdict when undervalued" in {
      // FV = 10 * 15 = 150. Price = 140. 140 < 150 -> BUY
      val s = Stock("BUY", 20, 10.0, 140.0)
      s.verdict shouldBe Verdict.Buy
      s.verdict.toString shouldBe "BUY"
    }

    // Case 2: Sell (Price > FV * 1.5)
    "return SELL verdict when significantly overvalued" in {
      // FV = 10 * 15 = 150. Threshold = 150 * 1.5 = 225. Price = 230 -> SELL
      val s = Stock("SELL", 20, 10.0, 230.0)
      s.verdict shouldBe Verdict.Sell
      s.verdict.toString shouldBe "SELL"
    }

    // Case 3: Hold (In between)
    "return HOLD verdict when fairly valued" in {
      // FV = 150. Price = 160. 150 < 160 < 225 -> HOLD
      val s = Stock("HOLD", 20, 10.0, 160.0)
      s.verdict shouldBe Verdict.Hold
      s.verdict.toString shouldBe "HOLD"
    }

    "calculate total value correctly" in {
      val s = Stock("T", 1, 1, 10.0, 5.0) // 10 * 5
      s.totalValue shouldBe 50.0
    }

    "format correctly for table view" in {
      val s = Stock("TEST", 1, 1, 100.0, 5.0)
      s.toLines(0) should include("TEST")
      s.toLines(1) should include("100.0")
      s.toLines(99) shouldBe "" // Default case
    }

  }

}
