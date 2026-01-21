package stockpilot

import stockpilot.model._
import stockpilot.controller.StockController

/** Component Module: hides concrete implementation choices from the high-level application
  */
object StockModule {

  // switch strategy here easily (XML/JSON)
  private val persistence: FileIO = new FileIOJson()

  /** Encapsulates the entire setup logic: default data vs loaded data
    */
  def setupController(initialData: List[Stock] = Nil): StockController = {
    // 1. Try to load state
    val loadedMemento = persistence.load

    val initialStocks = if (loadedMemento.stocks.nonEmpty) {
      loadedMemento.stocks
    } else {
      // Default data is now kept inside the module or a config file
      List(
        Stock("RR.L", 16.64, 0.6852, 798.99),
        Stock("AAPL", 28.50, 5.5100, 420.33),
        Stock("GOOGL", 26.80, 110.23, 120.01),
        Stock("GOOG", 12.01, 0.3000, 98.01)
      )
    }

    // 2. Wire components (encapsulated)
    val baseRepo = new StockRepository(initialStocks)
    val decoratedRepo = new LoggingRepository(baseRepo)

    // 3. Return the public interface (Controller)
    new StockController(decoratedRepo, persistence)
  }
}
