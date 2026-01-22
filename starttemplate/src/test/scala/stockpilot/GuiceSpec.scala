package stockpilot

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import com.google.inject.Guice
import controller.{IStockController, StockController}
import model.{FileIO, FileIOJson, StockRepository, IStockRepository, Stock}

class GuiceSpec extends AnyWordSpec with Matchers {

  "Dependency Injection Module" should {

    "configure bindings correctly in StockModuleDI" in {
      val injector = Guice.createInjector(new StockModuleDI)

      // Check Controller binding
      val controller = injector.getInstance(classOf[IStockController])
      controller shouldBe a[StockController]

      // Check Persistence binding
      val fileIO = injector.getInstance(classOf[FileIO])
      fileIO shouldBe a[FileIOJson]

      // Check Repository Binding (via Provider)
      val repo = injector.getInstance(classOf[IStockRepository])
      repo should not be null
    }

    "wire the full application via StockModule object" in {
      // This forces execution of StockModule.setupController logic
      val controller = StockModule.setupController(Nil)
      controller shouldBe a[IStockController]
    }

    "handle initial data loading in StockModule" in {
      val sampleData = List(Stock("INIT", 1, 1, 1, 0))
      // Executes the 'if (initialData.nonEmpty)' branch
      val controller = StockModule.setupController(sampleData)
      controller should not be null
    }

  }

}
