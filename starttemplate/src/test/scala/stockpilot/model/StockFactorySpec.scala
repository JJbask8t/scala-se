package stockpilot.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import scala.util.{Success, Failure}

class StockFactorySpec extends AnyWordSpec with Matchers {
  "StockFactory" should {
    "return Success with valid Stock for valid inputs" in {
      val s = StockFactory.createStock("aapl", "10.5", "1.2", "150.0")
      s shouldBe a[Success[_]]
      s.get.ticker shouldBe "AAPL"
    }

    "return Failure for invalid numbers" in {
      val s = StockFactory.createStock("aapl", "invalid", "1.2", "150.0")
      s shouldBe a[Failure[_]]
      s.failed.get.getMessage should include("valid number")
    }

    "return Failure for empty ticker" in {
      val s = StockFactory.createStock("", "10.5", "1.2", "150.0")
      s shouldBe a[Failure[_]]
    }
  }
}
