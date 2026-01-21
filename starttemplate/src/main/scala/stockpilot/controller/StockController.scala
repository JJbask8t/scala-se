package stockpilot.controller

import stockpilot.model._
import scala.util.{Try, Success, Failure}

/** Concrete implementation of the Controller Component. hidden from the outside world
  * (package-private). Access is restricted to the Interface IStockController.
  */
private[stockpilot] class StockController(repo: IStockRepository, fileIO: FileIO)
    extends IStockController { // Implements the Component Interface

  // keep the current sorting strategy, by default = by ticker
  private var sortStrategy: StockSortStrategy = SortByTicker
  private val undoManager                     = new UndoManager()

  // Method for changing strategy
  override def setSortStrategy(strategy: StockSortStrategy): Unit = {
    sortStrategy = strategy
    notifyObservers() // notify View that the order has changed
  }

  // use a strategy to sort the list
  override def allStocks: List[Stock] = sortStrategy.sort(repo.all)

  // modified to use Try Monad and Undo Command
  override def addStockFromInput(
      ticker: String,
      pe: String,
      eps: String,
      price: String
  ): Try[Unit] =
    // 1. Try to create stock using Factory
    StockFactory.createStock(ticker, pe, eps, price).flatMap { stock =>
      // 2. Check logic (if exists)
      if (repo.exists(stock.ticker)) {
        Failure(new IllegalArgumentException(s"Ticker '${stock.ticker}' already exists."))
      } else {
        // 3. Create Command and Execute via Manager
        val cmd = new AddStockAction(stock, repo)
        undoManager.execute(cmd)
        notifyObservers()
        Success(())
      }
    }

  // Modified to use Undo Command
  override def deleteStock(ticker: String): Boolean = repo.get(ticker) match {
    case Some(stock) =>
      val cmd = new DeleteStockAction(stock, repo)
      undoManager.execute(cmd)
      notifyObservers()
      true
    case None        => false
  }

  // New method for UI
  override def undoLastAction(): Boolean = {
    val result = undoManager.undo()
    if (result) notifyObservers()
    result
  }

  // treat ‘repo’ like a List, calling the .filter method directly.
  override def filterByPrice(min: Double, max: Double): List[Stock] = {
    val filtered = repo.filter(s => s.price >= min && s.price <= max).toList
    sortStrategy.sort(filtered)
  }

  override def save(): Unit = fileIO.save(repo.createMemento())

  override def load(): Unit = {
    repo.setMemento(fileIO.load)
    notifyObservers()
    println("Loaded successfully.")
  }

  override def exists(ticker: String): Boolean = repo.exists(ticker)

}
