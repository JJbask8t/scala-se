case class Stock(ticker: String, pe: Double, eps: Double, price: Double) {

  def toLines: List[String] =
    List(s"Stock ${ticker}", f"P/E = ${pe}%.2f", f"EPS = ${eps}%.4f", f"Price = ${price}%.4f")
}

def drawStockRow(stocks: List[Stock], cellWidth: Int): String = {

  if (stocks.isEmpty) return "No stocks to print"

  val eol = sys.props("line.separator")
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

  // Line 4:
  builder.append("|")
  for (stock <- stocks) {
    val line = stock.toLines(3)
    builder.append(line.padTo(cellWidth, ' ').mkString + "|")
  }
  builder.append(eol)

  builder.append(topAndBottomBar)

  builder.toString()
}

//old Main without CLI-UI
/* @main
def startSimpleStockPilot(): Unit = {
  val eol = sys.props("line.separator")
  println(eol + "Hey, I'm your SIMPLE StockPilot!")

  val cellWidth = 20

  val myStocks = List(
    Stock("RR.L", 16.64, 0.6852, 798.99),
    Stock("AAPL", 28.5, 5.51, 420.33),
    Stock("GOOGL", 26.8, 110.23, 120.01),
    Stock("GOOG", 12.01, 0.3, 98.01)
  )

  val grid = drawStockRow(myStocks, cellWidth)
  println(grid)
} */
