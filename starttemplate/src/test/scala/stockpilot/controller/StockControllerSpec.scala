package stockpilot.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import _root_.stockpilot.model._
import scala.util.{Success, Failure}

class StockControllerSpec extends AnyWordSpec with Matchers {

  // --- Test Doubles (Stubs) ---
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

  // --- Tests ---

  "StockController" should {
    val s1 = Stock("AAPL", 10.0, 1.0, 100.0)

    "add valid stock via input" in {
      val repo = new MockRepo
      val io   = new MockFileIO
      val ctrl = new StockController(repo, io)

      val result = ctrl.addStockFromInput("AAPL", "10", "1", "100")
      result shouldBe a[Success[_]]
      repo.exists("AAPL") shouldBe true
    }

    "fail to add invalid stock data" in {
      val repo = new MockRepo
      val ctrl = new StockController(repo, new MockFileIO)

      val result = ctrl.addStockFromInput("AAPL", "invalid", "1", "100")
      result shouldBe a[Failure[_]]
      repo.all shouldBe empty
    }

    "fail to add duplicate stock" in {
      val repo = new MockRepo
      repo.add(s1)
      val ctrl = new StockController(repo, new MockFileIO)

      val result = ctrl.addStockFromInput("AAPL", "20", "2", "200")
      result shouldBe a[Failure[_]]
    }

    "delete existing stock" in {
      val repo = new MockRepo
      repo.add(s1)
      val ctrl = new StockController(repo, new MockFileIO)

      ctrl.deleteStock("AAPL") shouldBe true
      repo.exists("AAPL") shouldBe false
    }

    "handle delete of non-existent stock" in {
      val ctrl = new StockController(new MockRepo, new MockFileIO)
      ctrl.deleteStock("UNKNOWN") shouldBe false
    }

    "undo last action (Add -> Undo)" in {
      val repo = new MockRepo
      val ctrl = new StockController(repo, new MockFileIO)

      ctrl.addStockFromInput("AAPL", "10", "1", "100")
      repo.exists("AAPL") shouldBe true

      ctrl.undoLastAction() shouldBe true
      repo.exists("AAPL") shouldBe false
    }

    "undo last action (Delete -> Undo)" in {
      val repo = new MockRepo
      repo.add(s1)
      val ctrl = new StockController(repo, new MockFileIO)

      ctrl.deleteStock("AAPL")
      repo.exists("AAPL") shouldBe false

      ctrl.undoLastAction() shouldBe true
      repo.exists("AAPL") shouldBe true
    }

    "filter stocks by price" in {
      val repo = new MockRepo
      repo.add(Stock("LOW", 1, 1, 10))
      repo.add(Stock("HIGH", 1, 1, 100))
      val ctrl = new StockController(repo, new MockFileIO)

      val result = ctrl.filterByPrice(0, 50)
      result.map(_.ticker) shouldBe List("LOW")
    }

    "save and load data" in {
      val repo = new MockRepo
      val io   = new MockFileIO
      val ctrl = new StockController(repo, io)

      // Save
      repo.add(s1)
      ctrl.save()
      io.saved.get.stocks should contain(s1)

      // Load
      repo.delete("AAPL")
      ctrl.load()
      repo.exists("AAPL") shouldBe true
    }

    "notify observers on changes" in {
      var notified = false
      val observer = new Observer {
        override def update(): Unit = notified = true
      }
      val ctrl     = new StockController(new MockRepo, new MockFileIO)
      ctrl.addObserver(observer)

      ctrl.addStockFromInput("TEST", "1", "1", "1")
      notified shouldBe true
    }
  }
}
