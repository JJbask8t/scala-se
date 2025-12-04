package stockpilot.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class DecoratorSpec extends AnyWordSpec with Matchers {
  "LoggingRepository (Decorator)" should {

    val s = Stock("AAPL", 10.0, 1.0, 100.0)

    "log additions and delegate to real repo" in {
      val baseRepo  = new StockRepository(Nil)
      val decorator = new LoggingRepository(baseRepo)

      decorator.add(s) shouldBe true
      baseRepo.exists("AAPL") shouldBe true
    }

    "log deletions and delegate to real repo" in {
      val baseRepo  = new StockRepository(List(s))
      val decorator = new LoggingRepository(baseRepo)

      decorator.delete("AAPL") shouldBe true
      baseRepo.exists("AAPL") shouldBe false
    }

    // --- Extended test all methods ---
    "delegate ALL read operations correctly" in {
      val baseRepo  = new StockRepository(List(s))
      val decorator = new LoggingRepository(baseRepo)

      // 1. test get
      decorator.get("AAPL") shouldBe Some(s)

      // 2. test exists
      decorator.exists("AAPL") shouldBe true

      // 3. test all
      decorator.all.length shouldBe 1
      decorator.all.head.ticker shouldBe "AAPL"

      // 4. test iterator
      decorator.iterator.hasNext shouldBe true
      decorator.iterator.next().ticker shouldBe "AAPL"

      // 5. test for-loop
      val listFromLoop = for (stock <- decorator) yield stock
      listFromLoop.toList.length shouldBe 1
    }
  }
}
