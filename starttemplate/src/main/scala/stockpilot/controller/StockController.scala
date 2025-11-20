package stockpilot.controller

import stockpilot.model.{Stock, StockRepository}

// Controller layer: business logic (managing stocks)
//Connects to the Model (StockRepository), but not the View
//View can call that all

class StockController(repo: StockRepository) extends Observable {

  // All stocks, sorted by ticker (for printing)
  def allStocks: List[Stock] =
    repo.all.sortBy(_.ticker)

  // Add stock: true - if added, false - ticker already exists
  def addStock(ticker: String, pe: Double, eps: Double, price: Double): Boolean = {
    val stock = Stock(ticker.toUpperCase, pe, eps, price)
    val added = repo.add(stock)
    if (added) notifyObservers()
    added
  }

  // Get stock by ticker
  def getStock(ticker: String): Option[Stock] =
    repo.get(ticker)

  // Delete stock by ticker: true - if deleted
  def deleteStock(ticker: String): Boolean = {
    val deleted = repo.delete(ticker)
    if (deleted) notifyObservers()
    deleted
  }

  // Filter stocks by price interval [min, max]
  def filterByPrice(min: Double, max: Double): List[Stock] =
    repo.findByPrice(min, max).sortBy(_.ticker)

  // Check if ticker exists
  def exists(ticker: String): Boolean =
    repo.exists(ticker)
}
