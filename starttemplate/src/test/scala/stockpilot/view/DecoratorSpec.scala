package stockpilot.view

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import stockpilot.model.{Stock, StockRepository, LoggingRepository}
import java.io.ByteArrayOutputStream

class DecoratorSpec extends AnyWordSpec with Matchers {

  "LoggingRepository" should {
    "log message when adding a stock" in {
      // 1. Setup
      val baseRepo  = new StockRepository()
      val decorator = new LoggingRepository(baseRepo)
      val s         = Stock("LOG", 1, 1, 1, 0)

      // 2. Capture Console Output
      val out = new ByteArrayOutputStream()
      Console.withOut(out)(decorator.add(s))

      // check if the output *contains* the key phrase
      out.toString should include("[LOG] Adding stock: LOG")
    }

    "log message when deleting a stock" in {
      // 1. Setup
      val s        = Stock("DEL", 1, 1, 1, 0)
      val baseRepo = new StockRepository()
      baseRepo.add(s)

      val decorator = new LoggingRepository(baseRepo)

      // 2. Capture Console Output
      val out = new ByteArrayOutputStream()
      Console.withOut(out)(decorator.delete("DEL"))

      out.toString should include("Attempting to delete stock: DEL")
    }

    "forward other calls to wrapped repository" in {
      val s        = Stock("FWD", 1, 1, 1, 0)
      val baseRepo = new StockRepository()
      baseRepo.add(s)

      val decorator = new LoggingRepository(baseRepo)

      decorator.exists("FWD") shouldBe true
      decorator.all should contain(s)
    }

  }

}
