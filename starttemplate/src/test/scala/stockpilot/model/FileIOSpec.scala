package stockpilot.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import java.io.File

class FileIOSpec extends AnyWordSpec with Matchers {

  val s1      = Stock("TEST1", 10.0, 1.0, 100.0)
  val s2      = Stock("TEST2", 20.0, 2.0, 200.0)
  val memento = StockMemento(List(s1, s2))

  "FileIOXml" should {
    "save and load stocks correctly" in {
      val io = new FileIOXml
      // Clean up before test
      new File("stock_data.xml").delete()

      // Save
      io.save(memento)
      new File("stock_data.xml").exists() shouldBe true

      // Load
      val loaded = io.load
      loaded.stocks should contain allElementsOf List(s1, s2)

      // Clean up
      new File("stock_data.xml").delete()
    }

    "handle missing file gracefully" in {
      val io = new FileIOXml
      new File("stock_data.xml").delete()
      io.load.stocks shouldBe empty
    }
  }

  "FileIOJson" should {
    "save and load stocks correctly" in {
      val io = new FileIOJson
      new File("stock_data.json").delete()

      io.save(memento)
      new File("stock_data.json").exists() shouldBe true

      val loaded = io.load
      loaded.stocks should contain allElementsOf List(s1, s2)

      new File("stock_data.json").delete()
    }
  }
}
