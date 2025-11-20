package stockpilot.view

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import stockpilot.model.Stock

class CLIViewHelpersSpec extends AnyWordSpec with Matchers {

  "CLIViewHelpers.toDouble" should {
    "parse valid numbers and return None for invalid" in {
      CLIViewHelpers.toDouble("23.5") shouldBe Some(23.5)
      CLIViewHelpers.toDouble("notnum") shouldBe None
      CLIViewHelpers.toDouble("23,5") shouldBe None
    }
  }

  "CLIViewHelpers.round2" should {
    "round correctly to 2 decimals" in {
      CLIViewHelpers.round2(1.234) shouldBe 1.23
      CLIViewHelpers.round2(1.235) shouldBe 1.24
    }
  }

  "CLIViewHelpers.drawStockRow" should {
    "render a grid for a single stock" in {
      val s = Stock("AAPL", 28.5, 5.51, 420.33)
      val grid = CLIViewHelpers.drawStockRow(List(s), 20)
      grid should include("Stock AAPL")
      grid should include("P/E = 28.50")
      grid should include("EPS = 5.5100")
      grid should include("Price = 420.3300")
    }

    "return 'No stocks to print' for empty list" in {
      CLIViewHelpers.drawStockRow(Nil, 20) shouldBe "No stocks to print"
    }

    "render multiple columns when multiple stocks given" in {
      val s1 = Stock("AAPL", 28.5, 5.51, 420.33)
      val s2 = Stock("GOOG", 12.01, 0.3, 98.01)
      val grid = CLIViewHelpers.drawStockRow(List(s1, s2), 20)
      grid should include("Stock AAPL")
      grid should include("Stock GOOG")
      // top border should have at least two '+' for two columns
      grid.split('\n').head.count(_ == '+') should be >= 2
    }
  }
}
