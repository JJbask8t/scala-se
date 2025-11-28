package stockpilot.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class StockRepositorySpec extends AnyWordSpec with Matchers {

  val s1 = Stock("AAPL", 28.5, 5.51, 420.33)
  val s2 = Stock("GOOG", 12.01, 0.3, 98.01)
  val s3 = Stock("MSFT", 30.0, 8.0, 300.00)

  "A Stock" should {
    "produce formatted lines with correct numeric formatting" in {
      val lines = s1.toLines
      lines.length shouldBe 4
      lines(0) shouldBe "Stock AAPL"
      // P/E formatted to 2 decimals
      lines(1) should include("P/E = 28.50")
      // EPS formatted to 4 decimals
      lines(2) should include("EPS = 5.5100")
      // Price formatted to 4 decimals
      lines(3) should include("Price = 420.3300")
    }
  }

  "StockRepository" when {
    "constructed with initial stocks" should {
      "return all initial stocks via all()" in {
        val repo = new StockRepository(List(s1, s2))
        val all = repo.all.sortBy(_.ticker)
        all.map(_.ticker) shouldBe List("AAPL", "GOOG")
      }

      "normalize tickers to uppercase and allow case-insensitive lookup" in {
        val repo = new StockRepository(List(s1))
        repo.exists("aapl") shouldBe true
        repo.exists("AAPL") shouldBe true
        val opt = repo.get("aapl")
        opt.isDefined shouldBe true
        opt.get.ticker shouldBe "AAPL"
      }
    }

    "adding stocks" should {
      "return false when adding a duplicate ticker (case-insensitive)" in {
        val repo = new StockRepository(List(s1))
        val dup = Stock("aapl", 1.0, 0.1, 1.0)
        repo.add(dup) shouldBe false
      }

      "return true when adding a new stock and make it findable" in {
        val repo = new StockRepository(List(s1))
        repo.exists("MSFT") shouldBe false
        repo.add(s3) shouldBe true
        repo.exists("MSFT") shouldBe true
      }
    }

    "deleting stocks" should {
      "return true when deleting existing stock and false for non-existing" in {
        val repo = new StockRepository(List(s1, s2))
        repo.delete("AAPL") shouldBe true
        repo.exists("AAPL") shouldBe false
        repo.delete("NONEXISTENT") shouldBe false
      }
    }

    "findByPrice" should {
      "return inclusive results for [min,max]" in {
        val repo = new StockRepository(
          List(
            Stock("LOW", 1.0, 0.1, 10.0),
            Stock("MID", 2.0, 0.2, 50.0),
            Stock("HIGH", 3.0, 0.3, 100.0)
          )
        )
        // [10,50] should include LOW(10.0) and MID(50.0)
        val got = repo.findByPrice(10.0, 50.0).map(_.ticker).sorted
        got should contain allOf ("LOW", "MID")
        got should not contain ("HIGH")
        // [50,100] should include MID and HIGH
        val got2 = repo.findByPrice(50.0, 100.0).map(_.ticker).sorted
        got2.toSet shouldBe Set("MID", "HIGH")
      }

      "return empty list when no stocks match" in {
        val repo = new StockRepository(List(s1, s2))
        repo.findByPrice(1000.0, 2000.0) shouldBe empty
      }

      // ----------> NEW PATTERN TEST (ITERATOR) ---
      "used as an Iterator" should {
        "allow iterating over stocks directly" in {
          val repo = new StockRepository(List(s1, s2))
          // Verify that the repository behaves as a collection
          repo.toList.length shouldBe 2
          repo.map(_.ticker).toList.sorted shouldBe List("AAPL", "GOOG")
        }
      }

    }
  }
}
