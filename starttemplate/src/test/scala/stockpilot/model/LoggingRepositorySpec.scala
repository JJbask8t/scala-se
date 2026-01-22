package stockpilot.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*
import java.io.{ByteArrayOutputStream, PrintStream}

class LoggingRepositorySpec extends AnyWordSpec {

  "LoggingRepository" should {

    "log successful and failed add" in {
      val baseRepo = new StockRepository()
      val repo     = new LoggingRepository(baseRepo)

      val out = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(out)) {
        repo.add(Stock("AAPL", 10.0, 1.0, 100.0, 0)) shouldBe true
        repo.add(Stock("AAPL", 10.0, 1.0, 100.0, 0)) shouldBe false // duplicate -> другая ветка
      }

      val text = out.toString
      text should include("[LOG] Adding stock: AAPL")
      text should include("[LOG] Add success")
      text should include("[LOG] Add failed")
    }

    "log successful and failed delete" in {
      val baseRepo = new StockRepository()
      baseRepo.add(Stock("AAPL", 10.0, 1.0, 100.0, 0)) shouldBe true

      val repo = new LoggingRepository(baseRepo)

      val out = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(out)) {
        repo.delete("AAPL") shouldBe true  // success ветка
        repo.delete("AAPL") shouldBe false // not found ветка
      }

      val text = out.toString
      text should include("[LOG] Attempting to delete stock: AAPL")
      text should include("[LOG] Successfully deleted: AAPL")
      text should include("[LOG] Failed to delete")
    }
  }
}
