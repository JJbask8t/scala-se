package stockpilot.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class DecoratorSpec extends AnyWordSpec with Matchers {
  "LoggingRepository (Decorator)" should {

    val baseRepo  = new StockRepository(Nil)
    val decorator = new LoggingRepository(baseRepo)
    val s         = Stock("AAPL", 10.0, 1.0, 100.0)

    "log additions and delegate to real repo" in {
      // Call decorator method
      decorator.add(s) shouldBe true
      // verify that the object has appeared in the REAL repository
      baseRepo.exists("AAPL") shouldBe true
    }

    "log deletions and delegate to real repo" in {
      decorator.delete("AAPL") shouldBe true
      baseRepo.exists("AAPL") shouldBe false
    }

    "delegate read operations (pass-through)" in {
      // verify that the methods we did NOT override also work.
      baseRepo.add(s)
      // Call via decorator
      decorator.exists("AAPL") shouldBe true
      decorator.get("AAPL") shouldBe Some(s)
      decorator.all.length shouldBe 1
    }
  }
}
