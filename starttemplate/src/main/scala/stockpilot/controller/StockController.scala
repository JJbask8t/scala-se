package stockpilot.controller

import stockpilot.model._
import scala.util.{Try, Success, Failure}

// Controller layer: business logic (managing stocks)
//Connects to the Model (StockRepository), but not the View
//View can call that all
//! Dependency Injection: Controller needs a FileIO implementation

class StockController(repo: IStockRepository, fileIO: FileIO) extends Observable {

  // keep the current sorting strategy, by default = by ticker
  private var sortStrategy: StockSortStrategy = SortByTicker
  private val undoManager                     = new UndoManager()

  // Method for changing strategy
  def setSortStrategy(strategy: StockSortStrategy): Unit = {
    sortStrategy = strategy
    notifyObservers() // notify View that the order has changed
  }

  // use a strategy to sort the list
  def allStocks: List[Stock] = sortStrategy.sort(repo.all)

  // modified to use Try Monad and Undo Command
  def addStockFromInput(ticker: String, pe: String, eps: String, price: String): Try[Unit] =
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
  def deleteStock(ticker: String): Boolean = repo.get(ticker) match {
    case Some(stock) =>
      val cmd = new DeleteStockAction(stock, repo)
      undoManager.execute(cmd)
      notifyObservers()
      true
    case None        => false
  }

  // New method for UI
  def undoLastAction(): Boolean = {
    val result = undoManager.undo()
    if (result) notifyObservers()
    result
  }

  // use Iterator/Iterable:
  // treat ‘repo’ like a List, calling the .filter method directly.
  def filterByPrice(min: Double, max: Double): List[Stock] = {
    val filtered = repo.filter(s => s.price >= min && s.price <= max).toList
    sortStrategy.sort(filtered)
  }

  def getStock(ticker: String): Option[Stock] = repo.get(ticker)
  def exists(ticker: String): Boolean         = repo.exists(ticker)

  // ! --- PERSISTENCE METHODS ---
  def save(): Unit = {
    val memento = repo.createMemento()
    fileIO.save(memento)
  }

  def load(): Unit = {
    val memento = fileIO.load
    repo.setMemento(memento)
    notifyObservers()
    println("Loaded successfully.")
  }

}
