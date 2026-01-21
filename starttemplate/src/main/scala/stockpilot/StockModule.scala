package stockpilot

import stockpilot.model._
import stockpilot.controller.{IStockController, StockController} // Import implementation internally

/** Component Module: The only place where concrete classes are wired together.
  */
object StockModule {

  private val persistence: FileIO = new FileIOJson()

  /** Returns the Component Interface (IStockController), not the class.
    */
  def setupController(initialData: List[Stock] = Nil): IStockController = {
    // 1. Check if we have saved data
    val loaded = persistence.load
    val data   = if (loaded.stocks.nonEmpty) loaded.stocks else initialData

    // 2. Wire components (encapsulated)
    val baseRepo      = new StockRepository(data)
    val decoratedRepo = new LoggingRepository(baseRepo)

    // 3. Return the interface
    new StockController(decoratedRepo, persistence)

  }

}
