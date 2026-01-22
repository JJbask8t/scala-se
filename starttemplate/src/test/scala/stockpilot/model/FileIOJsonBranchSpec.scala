package stockpilot.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*
import java.io.File

class FileIOJsonBranchSpec extends AnyWordSpec {

  private val file = new File("stock_data.json")

  private def cleanup(): Unit = if (file.exists()) file.delete()

  "FileIOJson" should {

    "return empty memento when file does not exist" in {
      cleanup()
      val io = new FileIOJson
      io.load.stocks shouldBe Nil
    }

    "save and load memento when file exists" in {
      cleanup()
      val io = new FileIOJson

      val m =
        StockMemento(List(Stock("AAPL", 10.0, 1.0, 100.0, 0), Stock("GOOG", 12.0, 2.0, 200.0, 5)))

      io.save(m)
      val loaded = io.load

      loaded.stocks.map(_.ticker) should contain allOf ("AAPL", "GOOG")

      cleanup()
    }

  }

}
