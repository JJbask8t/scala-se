package stockpilot.model

// Base decorator (passes calls through)
abstract class RepositoryDecorator(wraps: IStockRepository) extends IStockRepository {
  def all: List[Stock]                   = wraps.all
  def exists(ticker: String): Boolean    = wraps.exists(ticker)
  def get(ticker: String): Option[Stock] = wraps.get(ticker)
  def add(stock: Stock): Boolean         = wraps.add(stock)
  def delete(ticker: String): Boolean    = wraps.delete(ticker)
  def iterator: Iterator[Stock]          = wraps.iterator

  // ! Forwarding Memento methods
  def createMemento(): StockMemento     = wraps.createMemento()
  def setMemento(m: StockMemento): Unit = wraps.setMemento(m)
}

// Specific decorator: Logging
class LoggingRepository(wraps: IStockRepository) extends RepositoryDecorator(wraps) {
  override def delete(ticker: String): Boolean = {
    println(s"[LOG] Attempting to delete stock: $ticker")
    val result = super.delete(ticker)
    if (result) println(s"[LOG] Successfully deleted: $ticker")
    else println(s"[LOG] Failed to delete (not found): $ticker")
    result
  }

  // It is also possible to decorate add
  override def add(stock: Stock): Boolean = {
    println(s"[LOG] Adding stock: ${stock.ticker}")
    val res = super.add(stock)
    if (res) println("[LOG] Add success") else println("[LOG] Add failed (duplicate)")
    res
  }
}
