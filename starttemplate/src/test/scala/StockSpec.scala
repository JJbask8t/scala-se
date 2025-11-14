import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

class StockSpec extends AnyWordSpec {

  private val eol = sys.props("line.separator")

  private def pad(s: String, w: Int): String = s.toList.padTo(w, ' ').mkString

  private def bar(columns: Int, cellWidth: Int): String =
    ("+" + "-" * cellWidth) * columns + "+" + eol

  "Stock.toLines" should {
    "format three lines with proper number formatting" in {
      val s = Stock("AAPL", 28.5, 5.51)
      val lines = s.toLines

      lines(0) shouldBe "Stock AAPL"
      lines(1) shouldBe f"P/E = ${28.5}%.2f" // "P/E = 28.50"
      lines(2) shouldBe f"EPS = ${5.51}%.4f" // "EPS = 5.5100"
    }
  }

  "drawStockRow" should {

    "return a message when list is empty" in {
      val out = drawStockRow(Nil, cellWidth = 10)
      out shouldBe "No stocks to print"
    }

    "render a 1-column grid for a single stock with padding" in {
      val w = 12
      val st = Stock("AAPL", 28.5, 5.51)
      val grid = drawStockRow(List(st), w)

      val lines = st.toLines
      val expected = bar(1, w) + "|" + pad(lines(0), w) + "|" + eol + "|" + pad(lines(1), w) + "|" +
        eol + "|" + pad(lines(2), w) + "|" + eol + bar(1, w)

      grid shouldBe expected
    }

    "render multiple stocks side-by-side and pad each cell to width" in {
      val w = 14
      val s1 = Stock("RR.L", 16.64, 0.6852)
      val s2 = Stock("AAPL", 28.5, 5.51)
      val grid = drawStockRow(List(s1, s2), w)

      val l1 = s1.toLines
      val l2 = s2.toLines
      val expected = bar(2, w) + "|" + pad(l1(0), w) + "|" + pad(l2(0), w) + "|" + eol + "|" +
        pad(l1(1), w) + "|" + pad(l2(1), w) + "|" + eol + "|" + pad(l1(2), w) + "|" +
        pad(l2(2), w) + "|" + eol + bar(2, w)

      grid shouldBe expected
    }
  }
}
