package stockpilot.model

//---=== All states are here (Model layer) ===---

class StockRepository(initial: List[Stock]) {

  // Internal map: uppercase ticker -> Stock
  private var stocks: Map[String, Stock] =
    initial
      .map(s => normalizeTicker(s.ticker) -> s.copy(ticker = normalizeTicker(s.ticker)))
      .toMap

  private def normalizeTicker(t: String): String =
    t.toUpperCase

  // Return all stocks (unsorted)
  def all: List[Stock] =
    stocks.values.toList

  // Check if ticker exists
  def exists(ticker: String): Boolean =
    stocks.contains(normalizeTicker(ticker))

  // Get stock by ticker
  def get(ticker: String): Option[Stock] =
    stocks.get(normalizeTicker(ticker))

  // Add stock if ticker is not present: true - if added, false - ticker existed
  def add(stock: Stock): Boolean = {
    val key = normalizeTicker(stock.ticker)
    if (stocks.contains(key)) {
      false
    } else {
      stocks = stocks.updated(key, stock.copy(ticker = key))
      true
    }
  }

  // Delete stock by ticker: true - if stock was deleted
  def delete(ticker: String): Boolean = {
    val key = normalizeTicker(ticker)
    if (stocks.contains(key)) {
      stocks -= key
      true
    } else {
      false
    }
  }

  // Find all stocks whose price is in [min, max]
  def findByPrice(min: Double, max: Double): List[Stock] =
    stocks.values.filter(s => s.price >= min && s.price <= max).toList
}
