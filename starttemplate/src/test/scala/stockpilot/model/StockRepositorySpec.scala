package stockpilot.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class StockRepositorySpec extends AnyWordSpec with Matchers {

  "StockRepository" should {
    val s1 = Stock("A", 1, 1, 1)

    "add stock if not present" in {
      val repo = new StockRepository(Nil)
      repo.add(s1) shouldBe true
      repo.all.length shouldBe 1
    }

    "NOT add stock if already present (Branch Coverage)" in {
      val repo = new StockRepository(List(s1))
      repo.add(s1) shouldBe false // This hits the 'else' or 'if exists' branch
      repo.all.length shouldBe 1
    }

    "delete stock if present" in {
      val repo = new StockRepository(List(s1))
      repo.delete("A") shouldBe true
      repo.all shouldBe empty
    }

    "NOT delete stock if missing (Branch Coverage)" in {
      val repo = new StockRepository(Nil)
      repo.delete("A") shouldBe false // Hits the 'else' branch
    }

    "handle case-insensitive keys" in {
      val repo = new StockRepository(List(s1))
      repo.exists("a") shouldBe true
      repo.get("a") shouldBe Some(s1)
    }

  }

}
