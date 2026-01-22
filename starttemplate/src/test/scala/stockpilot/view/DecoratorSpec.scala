package stockpilot.view

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import stockpilot.model.{Stock, StockRepository, LoggingRepository, StockMemento}
import java.io.ByteArrayOutputStream

class DecoratorSpec extends AnyWordSpec with Matchers {

  "LoggingRepository" should {
    val s = Stock("TEST", 1, 1, 1, 0)

    "log message when adding a stock" in {
      val baseRepo  = new StockRepository()
      val decorator = new LoggingRepository(baseRepo)
      val out       = new ByteArrayOutputStream()

      Console.withOut(out)(decorator.add(s))
      out.toString should include("Adding stock: TEST")
    }

    "log message when deleting a stock" in {
      val baseRepo  = new StockRepository()
      baseRepo.add(s)
      val decorator = new LoggingRepository(baseRepo)
      val out       = new ByteArrayOutputStream()

      Console.withOut(out)(decorator.delete("TEST"))
      out.toString should include("Attempting to delete stock: TEST")
    }

    "forward 'all' call to wrapped repository" in {
      val baseRepo  = new StockRepository()
      baseRepo.add(s)
      val decorator = new LoggingRepository(baseRepo)
      decorator.all should contain(s)
    }

    "forward 'get' call" in {
      val baseRepo  = new StockRepository()
      baseRepo.add(s)
      val decorator = new LoggingRepository(baseRepo)
      decorator.get("TEST") shouldBe Some(s)
    }

    "forward 'exists' call" in {
      val baseRepo  = new StockRepository()
      baseRepo.add(s)
      val decorator = new LoggingRepository(baseRepo)
      decorator.exists("TEST") shouldBe true
    }

    "forward 'iterator' call" in {
      val baseRepo  = new StockRepository()
      baseRepo.add(s)
      val decorator = new LoggingRepository(baseRepo)
      decorator.iterator.toList should contain(s)
    }

    "forward 'createMemento' and 'setMemento' calls" in {
      val baseRepo  = new StockRepository()
      val decorator = new LoggingRepository(baseRepo)

      decorator.add(s)
      val memento = decorator.createMemento()

      decorator.delete("TEST")
      decorator.exists("TEST") shouldBe false

      decorator.setMemento(memento)
      decorator.exists("TEST") shouldBe true
    }

  }

}
