case class Stock(ticker: String, pe: Double, eps: Double) {

  def toLines: List[String] = List(s"Stock ${ticker}", f"P/E = ${pe}%.2f", f"EPS = ${eps}%.4f")
}

def drawStockRow(stocks: List[Stock], cellWidth: Int): String = {

  if (stocks.isEmpty) return "No stocks to print"

  val eol     = sys.props("line.separator")
  val builder = new StringBuilder

  val topAndBottomBar = ("+" + "-" * cellWidth) * stocks.length + "+" + eol
  builder.append(topAndBottomBar)

  // Line 1:
  builder.append("|")
  for (stock <- stocks) {
    val line = stock.toLines(0)
    builder.append(line.padTo(cellWidth, ' ').mkString + "|")
  }
  builder.append(eol)

  // Line 2:
  builder.append("|")
  for (stock <- stocks) {
    val line = stock.toLines(1)
    builder.append(line.padTo(cellWidth, ' ').mkString + "|")
  }
  builder.append(eol)

  // Line 3:
  builder.append("|")
  for (stock <- stocks) {
    val line = stock.toLines(2)
    builder.append(line.padTo(cellWidth, ' ').mkString + "|")
  }
  builder.append(eol)

  builder.append(topAndBottomBar)

  builder.toString()
}

@main
def startSimpleStockPilot(): Unit = {
  val eol = sys.props("line.separator")
  println(eol + "Hey, I'm your SIMPLE StockPilot!")

  val cellWidth = 20

  val myStocks = List(
    Stock("RR.L", 16.64, 0.6852),
    Stock("AAPL", 28.5, 5.51),
    Stock("GOOGL", 26.8, 110.23),
    Stock("GOOG", 12.01, 0.3)
  )

  val grid = drawStockRow(myStocks, cellWidth)
  println(grid)
}
