package stockpilot.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import java.io.File

class FileIOSpec extends AnyWordSpec with Matchers {

  val s1      = Stock("TEST1", 10.0, 1.0, 100.0)
  val memento = StockMemento(List(s1))

  "FileIOJson" should {
    val io       = new FileIOJson
    val filename = "stock_data.json"

    "save and load correctly" in {
      new File(filename).delete()
      io.save(memento)
      new File(filename).exists() shouldBe true

      val loaded = io.load
      loaded.stocks should contain(s1)
      new File(filename).delete()
    }

    "handle missing file gracefully" in {
      new File(filename).delete()
      io.load.stocks shouldBe empty
    }
  }

  "FileIOXml" should {
    val io       = new FileIOXml
    val filename = "stock_data.xml"

    "save and load correctly" in {
      new File(filename).delete()
      io.save(memento)
      new File(filename).exists() shouldBe true

      val loaded = io.load
      loaded.stocks should contain(s1)
      new File(filename).delete()
    }
  }
}
