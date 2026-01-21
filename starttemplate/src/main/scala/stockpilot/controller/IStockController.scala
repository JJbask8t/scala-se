package stockpilot.controller

package stockpilot.controller

import stockpilot.model.Stock
import scala.util.Try

/** Component Interface for the Controller layer. Following Task 10: Encapsulate every Component
  * with Interfaces.
  */
trait IStockController extends Observable {
  def allStocks: List[Stock]
  def addStockFromInput(ticker: String, pe: String, eps: String, price: String): Try[Unit]
  def deleteStock(ticker: String): Boolean
  def undoLastAction(): Boolean
  def filterByPrice(min: Double, max: Double): List[Stock]
  def setSortStrategy(strategy: _root_.stockpilot.model.StockSortStrategy): Unit

  // Persistence calls delegated from Main/Module
  def save(): Unit
  def load(): Unit
}
