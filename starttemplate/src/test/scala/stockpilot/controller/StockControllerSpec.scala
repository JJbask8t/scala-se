package stockpilot.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import stockpilot.model.{Stock, StockRepository}

class StockControllerSpec extends AnyWordSpec with Matchers {

  val s1 = Stock("AAPL", 28.5, 5.51, 420.33)
  val s2 = Stock("GOOG", 12.01, 0.3, 98.01)

  "StockController" should {
    "return all stocks sorted by ticker" in {
      val repo = new StockRepository(List(s2, s1))
      val controller = new StockController(repo)
      controller.allStocks.map(_.ticker) shouldBe List("AAPL", "GOOG")
    }

    "delegate addStock to repository and return false for duplicates" in {
      val repo = new StockRepository(List(s1))
      val controller = new StockController(repo)
      controller.addStock("aapl", 1.0, 0.1, 1.0) shouldBe false
      controller.addStock("MSFT", 10.0, 1.0, 50.0) shouldBe true
      controller.allStocks.map(_.ticker) should contain("MSFT")
    }

    "getStock and exists behave as expected" in {
      val repo = new StockRepository(List(s1))
      val controller = new StockController(repo)
      controller.exists("AAPL") shouldBe true
      controller.getStock("aapl").isDefined shouldBe true
      controller.getStock("nope").isEmpty shouldBe true
    }

    "deleteStock removes stocks and returns correct booleans" in {
      val repo = new StockRepository(List(s1, s2))
      val controller = new StockController(repo)
      controller.deleteStock("AAPL") shouldBe true
      controller.exists("AAPL") shouldBe false
      controller.deleteStock("AAPL") shouldBe false
    }

    "filterByPrice returns inclusive range and sorts by ticker" in {
      val repo = new StockRepository(
        List(
          Stock("LOW", 1.0, 0.1, 10.0),
          Stock("MID", 2.0, 0.2, 50.0),
          Stock("HIGH", 3.0, 0.3, 100.0)
        )
      )
      val controller = new StockController(repo)
      val res = controller.filterByPrice(10.0, 50.0)
      res.map(_.ticker).sorted shouldBe List("LOW", "MID")
    }

    "notify registered observers on add and delete" in {
      val repo = new StockRepository(List(s1))
      val controller = new StockController(repo)

      // simple test observer to check notification
      class TestObserver extends Observer {
        var called = 0
        def update(): Unit = { called += 1 }
      }

      val obs = new TestObserver
      controller.addObserver(obs)

      // add new stock -> should notify once
      controller.addStock("MSFT", 10.0, 1.0, 50.0) shouldBe true
      obs.called shouldBe 1

      // delete existing -> should notify again
      controller.deleteStock("MSFT") shouldBe true
      obs.called shouldBe 2

      // delete non-existing -> no notification (delete returns false)
      controller.deleteStock("NOPE") shouldBe false
      obs.called shouldBe 2
    }
  }
}
