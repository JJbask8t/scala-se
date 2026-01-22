package stockpilot.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import java.io.{File, PrintWriter}

class FileIOErrorSpec extends AnyWordSpec with Matchers {

  "FileIOJson" should {
    "throw exception on corrupted JSON file" in {
      val io   = new FileIOJson
      val file = new File("stock_data.json")

      // Write garbage to file
      val pw = new PrintWriter(file)
      pw.write("{ invalid json }")
      pw.close()

      // FIXED: Expect an exception instead of suppressing it
      an[Exception] should be thrownBy io.load

      file.delete()
    }
  }

  "FileIOXml" should {
    "throw exception on corrupted XML file" in {
      val io   = new FileIOXml
      val file = new File("stock_data.xml")

      // Write garbage
      val pw = new PrintWriter(file)
      pw.write("<not closed xml")
      pw.close()

      // FIXED: Expect an exception
      an[Exception] should be thrownBy io.load

      file.delete()
    }

  }

}
