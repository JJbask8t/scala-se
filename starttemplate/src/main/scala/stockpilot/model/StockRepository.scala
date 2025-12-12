package stockpilot.model

//---=== All states are here (Model layer) ===---

// Interface
trait IStockRepository extends Iterable[Stock] {
  def all: List[Stock]
  def exists(ticker: String): Boolean
  def get(ticker: String): Option[Stock]
  def add(stock: Stock): Boolean
  def delete(ticker: String): Boolean

  // ! --- MEMENTO PATTERN ---
  def createMemento(): StockMemento
  def setMemento(m: StockMemento): Unit
}

// PATTERN ITERATOR
// make the repository iterable by inheriting from Iterable[Stock]
// directly use for-loops and collection methods
class StockRepository(initial: List[Stock]) extends IStockRepository {

  // Internal map: uppercase ticker -> Stock
  private var stocks: Map[String, Stock] = initial
    .map(s => normalizeTicker(s.ticker) -> s.copy(ticker = normalizeTicker(s.ticker))).toMap

  private def normalizeTicker(t: String): String = t.toUpperCase

  // Implementation of the iterator method (Iterable requirement)
  override def iterator: Iterator[Stock] = stocks.values.iterator

  // Return all stocks (unsorted)
  override def all: List[Stock] = stocks.values.toList

  // Check if ticker exists
  override def exists(ticker: String): Boolean = stocks.contains(normalizeTicker(ticker))

  // Get stock by ticker
  override def get(ticker: String): Option[Stock] = stocks.get(normalizeTicker(ticker))

  // Add stock if ticker is not present: true - if added, false - ticker existed
  override def add(stock: Stock): Boolean = {
    val key = normalizeTicker(stock.ticker)
    if (stocks.contains(key)) { false }
    else {
      stocks = stocks.updated(key, stock.copy(ticker = key))
      true
    }
  }

  // Delete stock by ticker: true - if stock was deleted
  override def delete(ticker: String): Boolean = {
    val key = normalizeTicker(ticker)
    if (stocks.contains(key)) {
      stocks -= key
      true
    } else { false }
  }

  // ! --- MEMENTO IMPLEMENTATION ---
  // Creates a snapshot of the current state
  override def createMemento(): StockMemento = StockMemento(all)

  // Restores state from a snapshot
  override def setMemento(m: StockMemento): Unit = stocks = m.stocks
    .map(s => normalizeTicker(s.ticker) -> s.copy(ticker = normalizeTicker(s.ticker))).toMap

}
