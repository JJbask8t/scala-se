package stockpilot

import com.google.inject.Guice
import stockpilot.controller.IStockController
import stockpilot.model.Stock

object StockModule {

  /** Builds the application using Google Guice Dependency Injection.
    */
  def setupController(initialData: List[Stock] = Nil): IStockController = {
    // Create the Injector
    val injector = Guice.createInjector(new StockModuleDI)

    // Ask Guice for the Controller instance
    val controller = injector.getInstance(classOf[IStockController])

    // Try to load saved state from file
    controller.load()

    // If app is empty (fresh start), load default initial data
    if (controller.allStocks.isEmpty && initialData.nonEmpty) {

      initialData.foreach { s =>
        controller.addStockFromInput(
          s.ticker,
          s.pe.toString,
          s.eps.toString,
          s.price.toString,
          s.quantity.toString
        )
      }
    }

    controller
  }
}
