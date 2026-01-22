package stockpilot.coverage

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import stockpilot.model.{StockFactory, Stock, Verdict}
import scala.util.Failure

class EdgeCaseSpec extends AnyWordSpec with Matchers {

  "StockFactory (Error Branches)" should {

    "fail when EPS is not a number" in {
      val res = StockFactory.createStock("A", "1", "NOT_NUMBER", "1", "0")
      res shouldBe a[Failure[_]]
    }

    "fail when Price is not a number" in {
      val res = StockFactory.createStock("A", "1", "1", "FREE", "0")
      res shouldBe a[Failure[_]]
    }

    "fail when Quantity is not a number" in {
      val res = StockFactory.createStock("A", "1", "1", "1", "MANY")
      res shouldBe a[Failure[_]]
    }

    "fail when Ticker is empty string" in {
      val res = StockFactory.createStock("", "1", "1", "1", "0")
      res shouldBe a[Failure[_]]
    }
  }

  "Stock Verdict Logic (Boundary Branches)" should {

    "be BUY if Price < FairValue" in {
      // FV = 10 * 15 = 150. Price 149.
      val s = Stock("T", 1, 10, 149, 0)
      s.verdict shouldBe Verdict.Buy
    }

    "be SELL if Price > FairValue * 1.5" in {
      // FV = 150. Limit = 225. Price 226.
      val s = Stock("T", 1, 10, 226, 0)
      s.verdict shouldBe Verdict.Sell
    }

    "be HOLD if Price equals FairValue" in {
      // Boundary check
      val s = Stock("T", 1, 10, 150, 0)
      s.verdict shouldBe Verdict.Hold
    }

    "be HOLD if Price equals Limit" in {
      // Boundary check: 150 * 1.5 = 225
      val s = Stock("T", 1, 10, 225, 0)
      s.verdict shouldBe Verdict.Hold
    }

  }

}
