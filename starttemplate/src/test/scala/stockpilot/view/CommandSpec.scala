package stockpilot.view

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import stockpilot.controller.IStockController
import stockpilot.model.{Stock, StockSortStrategy}
import scala.util.Success

class CommandSpec extends AnyWordSpec with Matchers {

  // Stub Controller for commands
  class ControllerStub extends IStockController {
    override def allStocks: List[Stock] = Nil

    // Updated to 5 arguments (Quantity support)
    override def addStockFromInput(t: String, p: String, e: String, pr: String, q: String) =
      Success(())

    override def deleteStock(t: String) = true

    // Fixed: Added missing method implementation
    override def exists(ticker: String): Boolean = false

    override def undoLastAction()                        = true
    override def filterByPrice(min: Double, max: Double) = Nil
    override def setSortStrategy(s: StockSortStrategy)   = {}
    override def save()                                  = {}
    override def load()                                  = {}

    // Fixed: Added missing method implementation
    override def generateReport() = Success("Report generated")

    override def addObserver(o: stockpilot.controller.Observer): Unit    = {}
    override def removeObserver(o: stockpilot.controller.Observer): Unit = {}
    override def notifyObservers(): Unit                                 = {}
  }

  "Commands" should {
    val ctrl = new ControllerStub()

    "have correct descriptions" in {
      new AddStockCommand(ctrl).description shouldBe "Add stock (Unified Flow)"
      new ShowAllCommand(ctrl).description shouldBe "Show all stocks"
      new DeleteStockCommand(ctrl).description shouldBe "Delete stock by ticker"
      new FilterStockCommand(ctrl).description shouldBe "Filter by price [min-max]"
      new ChangeStrategyCommand(ctrl).description shouldBe "Change Sort Strategy"
      new UndoCommand(ctrl).description shouldBe "Undo last add/delete"
      // Added check for the new command
      new GenerateReportCommand(ctrl).description shouldBe "Generate Portfolio Report (CSV)"
    }
  }
}
