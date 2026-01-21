package stockpilot.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
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

    "add valid stock successfully" in {
      val repo = new MockRepo
      val ctrl = new StockController(repo, new MockFileIO)
      val res  = ctrl.addStockFromInput("AAPL", "10", "1", "100", "0")
      res shouldBe a[Success[_]]
      repo.exists("AAPL") shouldBe true
    }

    // Branch: StockFactory fails
    "fail when input is invalid (Factory failure)" in {
      val repo = new MockRepo
      val ctrl = new StockController(repo, new MockFileIO)
      val res  = ctrl.addStockFromInput("AAPL", "nan", "1", "100", "0")
      res shouldBe a[Failure[_]]
    }

    // Branch: Repository fails (Duplicate)
    "fail when adding duplicate stock (Repo failure)" in {
      val repo = new MockRepo
      repo.add(Stock("AAPL", 1, 1, 1))
      val ctrl = new StockController(repo, new MockFileIO)

      val res = ctrl.addStockFromInput("AAPL", "10", "1", "100", "0")
      res shouldBe a[Failure[_]]
      res.failed.get.getMessage should include("exists")
    }

    "generate CSV report successfully" in {
      val repo = new MockRepo
      repo.add(Stock("TEST", 1, 1, 100, 5)) // In portfolio
      val ctrl = new StockController(repo, new MockFileIO)

      val res = ctrl.generateReport()
      res shouldBe a[Success[_]]

      val f = new File("portfolio_report.csv")
      f.exists() shouldBe true
      f.delete() // Cleanup
    }

    // Test filter logic
    "filter stocks by price range" in {
      val repo = new MockRepo
      repo.add(Stock("A", 1, 1, 10))
      repo.add(Stock("B", 1, 1, 100))
      val ctrl = new StockController(repo, new MockFileIO)

      val res = ctrl.filterByPrice(0, 50)
      res.length shouldBe 1
      res.head.ticker shouldBe "A"
    }

    // Test persistence delegation
    "save and load via FileIO" in {
      val repo = new MockRepo
      val io   = new MockFileIO
      val ctrl = new StockController(repo, io)

      repo.add(Stock("A", 1, 1, 1))
      ctrl.save()
      io.saved.isDefined shouldBe true

      repo.delete("A")
      ctrl.load()
      repo.exists("A") shouldBe true
    }
  }
}
