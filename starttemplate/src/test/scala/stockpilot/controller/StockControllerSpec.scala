package stockpilot.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
// Use absolute path to avoid naming conflicts with 'stockpilot.controller.model'
import _root_.stockpilot.model._
import scala.util.{Success, Failure}

class StockControllerSpec extends AnyWordSpec with Matchers {

  // Helper data
  val sA = Stock("AAPL", 10.0, 1.0, 100.0)
  val sB = Stock("GOOG", 10.0, 1.0, 10.0)

  // Simple Mock for FileIO to test persistence logic without real files
  class TestFileIO extends FileIO {
    var saved: Option[StockMemento] = None

    override def save(m: StockMemento): Unit = saved = Some(m)

    // Returns the saved memento or an empty one if nothing was saved
    override def load: StockMemento = saved.getOrElse(StockMemento(Nil))
  }

  "StockController" should {

    // --- FACTORY & TRY MONAD TESTS ---

    "add valid stocks returning Success" in {
      val repo       = new StockRepository(Nil)
      val io         = new TestFileIO
      val controller = new StockController(repo, io)

      val res = controller.addStockFromInput("MSFT", "25.5", "1.2", "300.50")
      res shouldBe a[Success[_]]
      controller.exists("MSFT") shouldBe true
    }

    "return Failure when adding duplicate" in {
      val repo       = new StockRepository(List(sA))
      val io         = new TestFileIO
      val controller = new StockController(repo, io)

      val res = controller.addStockFromInput("AAPL", "10", "1", "100")
      res.isFailure shouldBe true
    }

    "return Failure for invalid input data" in {
      val repo       = new StockRepository(Nil)
      val io         = new TestFileIO
      val controller = new StockController(repo, io)

      val res = controller.addStockFromInput("BAD", "25.5", "1.2", "nan")
      res.isFailure shouldBe true
    }

    // --- UNDO COMMAND TESTS ---

    "undo adding a stock" in {
      val repo       = new StockRepository(Nil)
      val io         = new TestFileIO
      val controller = new StockController(repo, io)

      // Add
      controller.addStockFromInput("MSFT", "25", "1", "300")
      controller.exists("MSFT") shouldBe true

      // Undo
      controller.undoLastAction() shouldBe true
      controller.exists("MSFT") shouldBe false
    }

    "undo deleting a stock" in {
      val repo       = new StockRepository(List(sA))
      val io         = new TestFileIO
      val controller = new StockController(repo, io)

      // Delete
      controller.deleteStock("AAPL") shouldBe true
      controller.exists("AAPL") shouldBe false

      // Undo
      controller.undoLastAction() shouldBe true
      controller.exists("AAPL") shouldBe true
    }

    "handle undo when history is empty" in {
      val repo       = new StockRepository(Nil)
      val io         = new TestFileIO
      val controller = new StockController(repo, io)

      controller.undoLastAction() shouldBe false
    }

    // --- STRATEGY & LEGACY TESTS ---

    "switch sort strategies correctly" in {
      val repo       = new StockRepository(List(sA, sB)) // AAPL(100), GOOG(10)
      val io         = new TestFileIO
      val controller = new StockController(repo, io)

      // Sort by Price Ascending -> GOOG, AAPL
      controller.setSortStrategy(SortByPriceAsc)
      controller.allStocks.map(_.ticker) shouldBe List("GOOG", "AAPL")
    }

    "check if stock exists and get it" in {
      val repo       = new StockRepository(List(sA))
      val io         = new TestFileIO
      val controller = new StockController(repo, io)

      controller.exists("AAPL") shouldBe true
      controller.getStock("AAPL") shouldBe Some(sA)
    }

    "filter by price" in {
      val repo       = new StockRepository(List(sA, sB))
      val io         = new TestFileIO
      val controller = new StockController(repo, io)

      // Filter range 0..50 should return only GOOG (price 10)
      val res = controller.filterByPrice(0, 50)
      res.map(_.ticker) shouldBe List("GOOG")
    }

    // --- MEMENTO / PERSISTENCE TESTS ---

    "save and load via FileIO" in {
      val repo       = new StockRepository(List(sA))
      val io         = new TestFileIO
      val controller = new StockController(repo, io)

      // 1. Save current state (AAPL)
      controller.save()
      io.saved.isDefined shouldBe true
      io.saved.get.stocks should contain(sA)

      // 2. Change state (Add MSFT)
      controller.addStockFromInput("MSFT", "1", "1", "1")
      repo.exists("MSFT") shouldBe true

      // 3. Load old state (Should revert to only AAPL)
      controller.load()
      repo.exists("MSFT") shouldBe false
      repo.exists("AAPL") shouldBe true
    }
  }
}
