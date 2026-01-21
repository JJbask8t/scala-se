package stockpilot

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import stockpilot.model.Stock

class StockModuleSpec extends AnyWordSpec with Matchers {
  "StockModule" should {
    "properly initialize a StockController with a repository" in {
      val controller = StockModule.setupController()
      controller should not be null
      // Check if it's functional (assuming default data or loaded data exists)
      controller.allStocks should not be empty
    }
  }
}
