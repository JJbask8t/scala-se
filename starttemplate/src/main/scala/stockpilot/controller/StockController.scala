package stockpilot.controller

import stockpilot.model._

// Controller layer: business logic (managing stocks)
//Connects to the Model (StockRepository), but not the View
//View can call that all

class StockController(repo: StockRepository) extends Observable {

  // keep the current sorting strategy, by default = by ticker
  private var sortStrategy: StockSortStrategy = SortByTicker

  // Method for changing strategy
  def setSortStrategy(strategy: StockSortStrategy): Unit = {
    sortStrategy = strategy
    notifyObservers() // notify View that the order has changed
  }

  // use a strategy to sort the list
  def allStocks: List[Stock] =
    sortStrategy.sort(repo.all)

  // Accepts strings rather than ready-made Doubles, delegating parsing to the Factory
  def addStockFromInput(ticker: String, pe: String, eps: String, price: String): Boolean = {
    // Используем Factory Method для создания [cite: 531]
    StockFactory.createStock(ticker, pe, eps, price) match {
      case Some(stock) =>
        val added = repo.add(stock)
        if (added) notifyObservers()
        added
      case None =>
        false // Data validation error
    }
  }

  // Old method (current is addStockFromInput)

  /* def addStock(ticker: String, pe: Double, eps: Double, price: Double): Boolean = {
    val stock = Stock(ticker.toUpperCase, pe, eps, price)
    val added = repo.add(stock)
    if (added) notifyObservers()
    added
  } */

  def getStock(ticker: String): Option[Stock] = repo.get(ticker)

  def deleteStock(ticker: String): Boolean = {
    val deleted = repo.delete(ticker)
    if (deleted) notifyObservers()
    deleted
  }

  /* def filterByPrice(min: Double, max: Double): List[Stock] =
    // Here: also apply the sorting strategy to the filtered list
    sortStrategy.sort(repo.findByPrice(min, max)) */

  // use Iterator/Iterable:
  // treat ‘repo’ like a List, calling the .filter method directly.
  def filterByPrice(min: Double, max: Double): List[Stock] = {
    val filtered = repo.filter(s => s.price >= min && s.price <= max).toList
    sortStrategy.sort(filtered)
  }

  def exists(ticker: String): Boolean = repo.exists(ticker)
}
