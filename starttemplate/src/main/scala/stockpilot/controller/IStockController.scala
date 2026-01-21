package stockpilot.controller

import stockpilot.model.{Stock, StockSortStrategy}
import scala.util.Try

/** Component Interface for the Controller layer. Defines the contract for interaction between Views
  * (TUI/GUI) and the Business Logic.
  */
trait IStockController extends Observable {
  def allStocks: List[Stock]
  def addStockFromInput(ticker: String, pe: String, eps: String, price: String): Try[Unit]
  def deleteStock(ticker: String): Boolean
  def undoLastAction(): Boolean
  def filterByPrice(min: Double, max: Double): List[Stock]
  def setSortStrategy(strategy: StockSortStrategy): Unit

  // Persistence methods exposed to the View/Main
  def save(): Unit
  def load(): Unit

  def exists(ticker: String): Boolean

}
