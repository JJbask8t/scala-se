package stockpilot

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
// Fix imports: use relative paths since we are already inside 'package stockpilot'
import model.Stock
import controller.{IStockController, StockController}

class GuiceIntegrationSpec extends AnyWordSpec with Matchers {

  "StockModule (Guice Configuration)" should {

    "wire the application correctly using setupController" in {
      // This executes the code in StockModule and StockModuleDI
      val controller = StockModule.setupController(Nil)

      // Verify that Guice actually created an instance
      controller should not be null

      // Check that the resulting object implements the interface
      controller shouldBe a[IStockController]

      // Check that it is indeed the concrete implementation we bound
      controller shouldBe a[StockController]
    }

    "load default data if initialized with stocks" in {
      val initial = List(Stock("TEST", 1, 1, 1, 0))

      // This hits the branch inside StockModule.setupController
      // where it checks "if (initialData.nonEmpty)"
      val controller = StockModule.setupController(initial)

      controller should not be null
      // Verify data was loaded (simulated via manual addition in Module)
      controller.allStocks.exists(_.ticker == "TEST") shouldBe true
    }

  }

}
