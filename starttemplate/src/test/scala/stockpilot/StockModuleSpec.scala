package stockpilot

package stockpilot

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import _root_.stockpilot.controller.IStockController

class StockModuleSpec extends AnyWordSpec with Matchers {

  "StockModule" should {
    "initialize the controller via setupController" in {
      val controller = StockModule.setupController()

      // Check that we got a valid interface implementation
      controller shouldBe a[IStockController]

      // Basic smoke test to ensure it's functional
      // Note: This relies on the default FileIOJson strategy used in Module
      noException should be thrownBy controller.allStocks
    }
  }
}
