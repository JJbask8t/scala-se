package stockpilot.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class StockSpec extends AnyWordSpec with Matchers {

  "Stock" should {
    "calculate FairValue and Verdict correctly" in {
      val sBuy = Stock("B", 1, 10, 100, 0) // FV=150
      sBuy.verdict shouldBe Verdict.Buy

      val sSell = Stock("S", 1, 10, 250, 0) // FV=150, Limit=225
      sSell.verdict shouldBe Verdict.Sell

      val sHold = Stock("H", 1, 10, 160, 0)
      sHold.verdict shouldBe Verdict.Hold
    }

    "provide correct TUI formatting (toLines)" in {
      // FIXED: Changed EPS from 1 to 10.0 to ensure Verdict is BUY
      // FairValue = 10 * 15 = 150. Price = 100. 100 < 150 -> BUY
      val s = Stock("AAPL", 1, 10.0, 100.0, 5.0)

      // Check every line index explicitly
      s.toLines(0) should include("AAPL")
      s.toLines(1) should include("100.0")
      s.toLines(2) should include("5.0")
      s.toLines(3) should include("BUY")
      s.toLines(4) shouldBe ""
    }

    "have correct Verdict string representation and colors" in {
      Verdict.Buy.toString shouldBe "BUY"
      Verdict.Sell.toString shouldBe "SELL"
      Verdict.Hold.toString shouldBe "HOLD"

      Verdict.Buy.color should not be null
      Verdict.Sell.color should not be null
      Verdict.Hold.color should not be null
    }

  }

}
