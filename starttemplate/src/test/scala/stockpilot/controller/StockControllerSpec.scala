package stockpilot.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
//to avoid name conflicts within the controller package:
import _root_.stockpilot.model._
import scala.util.Success

class StockControllerSpec extends AnyWordSpec with Matchers {

  val sA = Stock("AAPL", 10.0, 1.0, 100.0)
  val sB = Stock("GOOG", 10.0, 1.0, 10.0)

  "StockController" should {

    "add valid stocks returning Success" in {
      val repo       = new StockRepository(Nil)
      val controller = new StockController(repo)
      val res        = controller.addStockFromInput("MSFT", "25.5", "1.2", "300.50")
      res shouldBe a[Success[_]]
      controller.exists("MSFT") shouldBe true
    }

    "return Failure when adding duplicate" in {
      val repo       = new StockRepository(List(sA))
      val controller = new StockController(repo)
      val res        = controller.addStockFromInput("AAPL", "10", "1", "100")
      res.isFailure shouldBe true
    }

    "return Failure for invalid input data" in {
      val repo       = new StockRepository(Nil)
      val controller = new StockController(repo)
      val res        = controller.addStockFromInput("BAD", "25.5", "1.2", "nan")
      res.isFailure shouldBe true
    }

    "undo adding a stock" in {
      val repo       = new StockRepository(Nil)
      val controller = new StockController(repo)

      // Add
      controller.addStockFromInput("MSFT", "25", "1", "300")
      controller.exists("MSFT") shouldBe true

      // Undo
      controller.undoLastAction() shouldBe true
      controller.exists("MSFT") shouldBe false
    }

    "undo deleting a stock" in {
      val repo       = new StockRepository(List(sA))
      val controller = new StockController(repo)

      // Delete
      controller.deleteStock("AAPL") shouldBe true
      controller.exists("AAPL") shouldBe false

      // Undo
      controller.undoLastAction() shouldBe true
      controller.exists("AAPL") shouldBe true
    }

    "handle undo when history is empty" in {
      val repo       = new StockRepository(Nil)
      val controller = new StockController(repo)
      controller.undoLastAction() shouldBe false
    }

    // --- Legacy Tests (Strategy, etc) ---
    "switch sort strategies correctly" in {
      val repo       = new StockRepository(List(sA, sB))
      val controller = new StockController(repo)
      controller.setSortStrategy(SortByPriceAsc)
      controller.allStocks.map(_.ticker) shouldBe List("GOOG", "AAPL")
    }

    "check if stock exists and get it" in {
      val repo       = new StockRepository(List(sA))
      val controller = new StockController(repo)
      controller.exists("AAPL") shouldBe true
      controller.getStock("AAPL") shouldBe Some(sA)
    }

    "filter by price" in {
      val repo       = new StockRepository(List(sA, sB))
      val controller = new StockController(repo)
      val res        = controller.filterByPrice(0, 50)
      res.map(_.ticker) shouldBe List("GOOG")
    }
  }
}
