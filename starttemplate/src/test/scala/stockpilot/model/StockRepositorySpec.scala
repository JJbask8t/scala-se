package stockpilot.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class StockRepositorySpec extends AnyWordSpec with Matchers {

  "A StockRepository" when {
    val s1 = Stock("AAPL", 100.0, 5.0, 150.0)
    val s2 = Stock("GOOG", 200.0, 10.0, 250.0)

    "newly created" should {
      val repo = new StockRepository(List(s1))

      "contain the initial stocks" in {
        repo.all should contain(s1)
        repo.exists("AAPL") shouldBe true
      }

      "normalize tickers to uppercase" in {
        repo.exists("aapl") shouldBe true
        repo.get("aapl") shouldBe Some(s1)
      }

      "return None for unknown stocks" in {
        repo.get("UNKNOWN") shouldBe None
        repo.exists("UNKNOWN") shouldBe false
      }
    }

    "managing stocks" should {
      "add a new stock correctly" in {
        val repo = new StockRepository(Nil)
        repo.add(s1) shouldBe true
        repo.exists("AAPL") shouldBe true
      }

      "not add a duplicate stock" in {
        val repo = new StockRepository(List(s1))
        repo.add(s1) shouldBe false
        repo.all.size shouldBe 1
      }

      "delete an existing stock" in {
        val repo = new StockRepository(List(s1))
        repo.delete("AAPL") shouldBe true
        repo.exists("AAPL") shouldBe false
      }

      "return false when deleting a non-existent stock" in {
        val repo = new StockRepository(Nil)
        repo.delete("AAPL") shouldBe false
      }
    }

    "using Memento pattern" should {
      "save and restore state" in {
        val repo = new StockRepository(List(s1))

        // Save state
        val memento = repo.createMemento()

        // Change state
        repo.add(s2)
        repo.exists("GOOG") shouldBe true

        // Restore state
        repo.setMemento(memento)
        repo.exists("GOOG") shouldBe false
        repo.exists("AAPL") shouldBe true
      }
    }

    "iterating" should {
      "allow iteration over stocks" in {
        val repo = new StockRepository(List(s1, s2))
        repo.iterator.toList should contain allElementsOf List(s1, s2)
      }
    }
  }
}
