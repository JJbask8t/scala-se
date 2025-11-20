package stockpilot.model

// data model: stock model with all fields
case class Stock(ticker: String, pe: Double, eps: Double, price: Double) {

  // Lines used by the table renderer; formatting kept as in your original code
  def toLines: List[String] = List(
    s"Stock ${ticker}",
    f"P/E = ${pe}%.2f",
    f"EPS = ${eps}%.4f",
    f"Price = ${price}%.4f"
  )
}
