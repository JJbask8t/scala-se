package stockpilot.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
// FIXED: Use absolute import to avoid package ambiguity
import _root_.stockpilot.model._
import scala.util.{Success, Failure}
import java.io.File

class StockControllerSpec extends AnyWordSpec with Matchers {

  // --- Mocks ---
  class MockRepo extends IStockRepository {
    var stocks                                     = scala.collection.mutable.Map[String, Stock]()
    override def all: List[Stock]                  = stocks.values.toList
    override def exists(t: String): Boolean        = stocks.contains(t.toUpperCase)
    override def get(t: String): Option[Stock]     = stocks.get(t.toUpperCase)
    override def add(s: Stock): Boolean            =
      if (exists(s.ticker)) false else { stocks(s.ticker.toUpperCase) = s; true }
    override def delete(t: String): Boolean        =
      if (exists(t)) { stocks.remove(t.toUpperCase); true }
      else false
    override def createMemento(): StockMemento     = StockMemento(all)
    override def setMemento(m: StockMemento): Unit = {
      stocks.clear()
      m.stocks.foreach(s => stocks(s.ticker) = s)
    }
    override def iterator: Iterator[Stock]         = stocks.values.iterator
  }

  class MockFileIO extends FileIO {
    var saved: Option[StockMemento]          = None
    override def save(m: StockMemento): Unit = saved = Some(m)
    override def load: StockMemento          = saved.getOrElse(StockMemento(Nil))
  }

  "StockController" should {

    // 1. Success Path
    "add valid stock successfully" in {
      val repo = new MockRepo
      val ctrl = new StockController(repo, new MockFileIO)
      val res  = ctrl.addStockFromInput("AAPL", "10", "1", "100", "0")
      res shouldBe a[Success[_]]
      repo.exists("AAPL") shouldBe true
    }

    // 2. Factory Failure Path (Invalid Number)
    "fail when input format is invalid" in {
      val repo = new MockRepo
      val ctrl = new StockController(repo, new MockFileIO)
      val res  = ctrl.addStockFromInput("AAPL", "NOT_A_NUMBER", "1", "100", "0")
      res shouldBe a[Failure[_]]
      // Verify nothing changed
      repo.all shouldBe empty
    }

    // 3. Repository Failure Path (Duplicate)
    "fail when adding duplicate stock" in {
      val repo = new MockRepo
      repo.add(Stock("AAPL", 1, 1, 1, 0))
      val ctrl = new StockController(repo, new MockFileIO)

      val res = ctrl.addStockFromInput("AAPL", "10", "1", "100", "0")
      res shouldBe a[Failure[_]]
      res.failed.get.getMessage should include("exists")
    }

    // 4. Delete Logic (True/False)
    "delete stock returns true if found, false otherwise" in {
      val repo = new MockRepo
      repo.add(Stock("DEL", 1, 1, 1, 0))
      val ctrl = new StockController(repo, new MockFileIO)

      // Existing
      ctrl.deleteStock("DEL") shouldBe true
      repo.exists("DEL") shouldBe false

      // Non-existing
      ctrl.deleteStock("GHOST") shouldBe false
    }

    // 5. Undo Logic (Empty/Non-empty)
    "handle undo operations" in {
      val repo = new MockRepo
      val ctrl = new StockController(repo, new MockFileIO)

      // Empty stack undo -> should return false
      ctrl.undoLastAction() shouldBe false

      // Perform action
      ctrl.addStockFromInput("UNDO_ME", "1", "1", "1", "0")
      repo.exists("UNDO_ME") shouldBe true

      // Undo success -> should return true
      ctrl.undoLastAction() shouldBe true
      repo.exists("UNDO_ME") shouldBe false
    }

    // 6. Report and Filter
    "generate CSV report" in {
      val repo = new MockRepo
      val ctrl = new StockController(repo, new MockFileIO)
      ctrl.generateReport() shouldBe a[Success[_]]
      new File("portfolio_report.csv").delete()
    }

    "filter stocks" in {
      val repo = new MockRepo
      repo.add(Stock("A", 1, 1, 10, 0))
      val ctrl = new StockController(repo, new MockFileIO)
      ctrl.filterByPrice(0, 20).size shouldBe 1
    }

    "sort strategies" in {
      val repo = new MockRepo
      val ctrl = new StockController(repo, new MockFileIO)
      // Just ensure method call works (coverage), logic tested in StrategySpec
      ctrl.setSortStrategy(SortByTicker)
      ctrl.allStocks
    }

  }

}
