package stockpilot.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class StockFactorySpec extends AnyWordSpec with Matchers {
  "StockFactory" should {
    "create a valid Stock from valid strings" in {
      val s = StockFactory.createStock("aapl", "10.5", "1.2", "150.0")
      s should be(defined)
      s.get.ticker shouldBe "AAPL"
      s.get.price shouldBe 150.0
    }

    "return None if numbers are invalid" in {
      StockFactory.createStock("aapl", "invalid", "1.2", "150.0") shouldBe None
    }

    "return None if ticker is empty" in {
      StockFactory.createStock("", "10.5", "1.2", "150.0") shouldBe None
    }
  }
}
