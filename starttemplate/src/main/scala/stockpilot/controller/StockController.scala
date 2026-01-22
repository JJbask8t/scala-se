package stockpilot.controller

import _root_.stockpilot.model._
import scala.util.{Try, Success, Failure}
import java.io.{PrintWriter, File}
import com.google.inject.{AbstractModule, Guice, Inject}

/** Concrete implementation of the Controller Component. hidden from the outside world
  * (package-private). Access is restricted to the Interface IStockController.
  */
private[stockpilot] class StockController @Inject() (repo: IStockRepository, fileIO: FileIO)
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
      price: String,
      qty: String
  ): Try[Unit] = StockFactory.createStock(ticker, pe, eps, price, qty).flatMap { stock =>
    if (repo.exists(stock.ticker)) {
      Failure(new IllegalArgumentException(s"Ticker '${stock.ticker}' already exists."))
    } else {
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

  override def undoLastAction(): Boolean = {
    val result = undoManager.undo()
    if (result) notifyObservers()
    result
  }

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

  override def generateReport(): Try[String] = Try {
    val filename = "portfolio_report.csv"
    val pw       = new PrintWriter(new File(filename))
    val sb       = new StringBuilder

    // Header
    sb.append("Ticker;Quantity;Price;Total Value;Verdict\n")

    // Filter only portfolio stocks (qty > 0)
    val portfolio = repo.all.filter(_.quantity > 0)

    portfolio.foreach { s =>
      sb.append(f"${s.ticker};${s.quantity};${s.price}%.2f;${s.totalValue}%.2f;${s.verdict}\n")
    }

    // Total Sum
    val totalSum = portfolio.map(_.totalValue).sum
    sb.append(f"\nTOTAL PORTFOLIO VALUE;;;;$totalSum%.2f\n")

    pw.write(sb.toString())
    pw.close()
    s"Report saved to $filename"
  }

}
