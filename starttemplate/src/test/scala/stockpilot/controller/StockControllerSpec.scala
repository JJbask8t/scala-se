package stockpilot.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
//to avoid name conflicts within the controller package:
import _root_.stockpilot.model._

class StockControllerSpec extends AnyWordSpec with Matchers {

  // Вспомогательные данные
  val sA = Stock("AAPL", 10.0, 1.0, 100.0) // Price 100
  val sB = Stock("GOOG", 10.0, 1.0, 10.0) // Prise 10
  val sC = Stock("MSFT", 15.0, 2.0, 50.0) // price 50

  "StockController" should {

    // --- TESTS OF NEW PATTERNS (FACTORY) ---
    "add valid stocks via Factory method (String input)" in {
      val repo = new StockRepository(Nil)
      val controller = new StockController(repo)
      val res = controller.addStockFromInput("MSFT", "25.5", "1.2", "300.50")
      res shouldBe true
      controller.exists("MSFT") shouldBe true
    }

    "reject invalid stocks via Factory method" in {
      val repo = new StockRepository(Nil)
      val controller = new StockController(repo)
      val res = controller.addStockFromInput("BAD", "25.5", "1.2", "not-a-number")
      res shouldBe false
      controller.exists("BAD") shouldBe false
    }

    // --- TESTS OF NEW PATTERNS (STRATEGY) ---
    "switch sort strategies correctly" in {
      val repo = new StockRepository(List(sA, sB))
      val controller = new StockController(repo)

      // 1. By default (SortByTicker)
      controller.setSortStrategy(SortByTicker)
      controller.allStocks.map(_.ticker) shouldBe List("AAPL", "GOOG")

      // 2. By price (Asc)
      controller.setSortStrategy(SortByPriceAsc)
      controller.allStocks.map(_.ticker) shouldBe List("GOOG", "AAPL")

      // 3. By pric (Desc)
      controller.setSortStrategy(SortByPriceDesc)
      controller.allStocks.map(_.ticker) shouldBe List("AAPL", "GOOG")
    }

    "notify observers when strategy changes" in {
      val repo = new StockRepository(Nil)
      val controller = new StockController(repo)
      var notified = false
      val observer = new Observer {
        def update(): Unit = notified = true
      }
      controller.addObserver(observer)
      controller.setSortStrategy(SortByPriceAsc)
      notified shouldBe true
    }

    // --- BASIC FUNCTION TESTS (CRUD) ---

    "check if stock exists" in {
      val repo = new StockRepository(List(sA))
      val controller = new StockController(repo)
      controller.exists("AAPL") shouldBe true
      controller.exists("NONEXISTENT") shouldBe false
    }

    "get stock by ticker" in {
      val repo = new StockRepository(List(sA))
      val controller = new StockController(repo)
      controller.getStock("AAPL") shouldBe Some(sA)
      controller.getStock("UNKNOWN") shouldBe None
    }

    "delete stock and notify observers" in {
      val repo = new StockRepository(List(sA))
      val controller = new StockController(repo)

      var notifications = 0
      controller.addObserver(new Observer {
        def update(): Unit = notifications += 1
      })

      // Delete existing
      controller.deleteStock("AAPL") shouldBe true
      repo.exists("AAPL") shouldBe false
      notifications shouldBe 1

      // Delete non-existent (should not notify)
      controller.deleteStock("AAPL") shouldBe false
      notifications shouldBe 1
    }

    "filter by price and respect current sort strategy" in {
      // List: GOOG(10), MSFT(50), AAPL(100)
      val repo = new StockRepository(List(sA, sB, sC))
      val controller = new StockController(repo)

      // Filter 0-60 should return GOOG and MSFT
      // Default strategy (Ticker): GOOG, MSFT
      val res1 = controller.filterByPrice(0, 60)
      res1.map(_.ticker) shouldBe List("GOOG", "MSFT")

      // Change strategy to PriceDesc (expensive at the top)
      // Filter 0-60. Should return MSFT(50), GOOG(10)
      controller.setSortStrategy(SortByPriceDesc)
      val res2 = controller.filterByPrice(0, 60)
      res2.map(_.ticker) shouldBe List("MSFT", "GOOG")
    }
  }
}
