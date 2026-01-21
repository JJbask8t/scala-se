package stockpilot.view

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import stockpilot.controller.IStockController
import stockpilot.model.{Stock, StockSortStrategy}
import scala.util.Success

class ViewIsolationSpec extends AnyWordSpec with Matchers {

  /** TEST DOUBLE: Controller Stub. Allows testing View without any real business logic dependency.
    * Conforms to SE-10 requirements on Testing Components.
    */
  class ControllerStub extends IStockController {
    override def allStocks: List[Stock] = List(Stock("MOCK", 1, 1, 1))

    // Updated to 5 arguments
    override def addStockFromInput(t: String, p: String, e: String, pr: String, q: String) =
      Success(())

    override def deleteStock(t: String) = true

    // Fixed: Added missing method
    override def exists(ticker: String): Boolean = true

    override def undoLastAction()                        = true
    override def filterByPrice(min: Double, max: Double) = Nil
    override def setSortStrategy(s: StockSortStrategy)   = {}
    override def save()                                  = {}
    override def load()                                  = {}

    // Fixed: Added missing method
    override def generateReport() = Success("Stub report")

    // Stubbing Observable methods
    override def addObserver(o: stockpilot.controller.Observer): Unit    = {}
    override def removeObserver(o: stockpilot.controller.Observer): Unit = {}
    override def notifyObservers(): Unit                                 = {}
  }

  "CLIView" should {
    "depend only on IStockController interface" in {
      val stub = new ControllerStub()
      // If this compiles, the View is correctly decoupled from the concrete Controller class
      val tui  = new CLIView(stub)
      tui should not be null
    }

  }

}
