package stockpilot.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import scala.util.{Success, Failure}

class StockFactorySpec extends AnyWordSpec with Matchers {

  "StockFactory" should {

    "create a valid Stock with all fields correct" in {
      val res = StockFactory.createStock("AAPL", "20.5", "5.0", "150.0", "10")
      res shouldBe a[Success[_]]
      val s   = res.get
      s.ticker shouldBe "AAPL"
      s.quantity shouldBe 10.0
    }

    "handle empty quantity by defaulting to 0.0" in {
      val res = StockFactory.createStock("AAPL", "20", "5", "150", "")
      res.get.quantity shouldBe 0.0
    }

    "fail if Ticker is empty" in {
      val res = StockFactory.createStock("", "20", "5", "150", "10")
      res shouldBe a[Failure[_]]
      res.failed.get.getMessage should include("Ticker")
    }

    "fail if P/E is invalid" in {
      val res = StockFactory.createStock("AAPL", "nan", "5", "150", "10")
      res shouldBe a[Failure[_]]
      res.failed.get.getMessage should include("P/E")
    }

    "fail if EPS is invalid" in {
      val res = StockFactory.createStock("AAPL", "20", "xyz", "150", "10")
      res shouldBe a[Failure[_]]
      res.failed.get.getMessage should include("EPS")
    }

    "fail if Price is invalid" in {
      val res = StockFactory.createStock("AAPL", "20", "5", "free", "10")
      res shouldBe a[Failure[_]]
      res.failed.get.getMessage should include("Price")
    }

    "fail if Quantity is invalid (not empty, but bad string)" in {
      val res = StockFactory.createStock("AAPL", "20", "5", "150", "ten")
      res shouldBe a[Failure[_]]
      res.failed.get.getMessage should include("Quantity")
    }

  }

}
