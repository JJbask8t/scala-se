package stockpilot.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*
import java.io.File

class FileIOXmlBranchSpec extends AnyWordSpec {

  private val file = new File("stock_data.xml")

  private def cleanup(): Unit = if (file.exists()) file.delete()

  "FileIOXml" should {

    "return empty memento when file does not exist" in {
      cleanup()
      val io = new FileIOXml
      io.load.stocks shouldBe Nil
    }

    "save and load memento when file exists" in {
      cleanup()
      val io = new FileIOXml

      val m = StockMemento(List(Stock("AAPL", 10.0, 1.0, 100.0, 0)))

      io.save(m)
      val loaded = io.load

      loaded.stocks.map(_.ticker) shouldBe List("AAPL")

      cleanup()
    }

  }

}
