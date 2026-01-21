package stockpilot.model

import scala.util.{Try, Success, Failure}

object StockFactory {

  /** Creates a Stock from raw string inputs safely using the Try Monad. Returns Success(Stock) if
    * all inputs are valid, or Failure(exception) otherwise.
    */
  def createStock(
      ticker: String,
      peStr: String,
      epsStr: String,
      priceStr: String,
      qtyStr: String
  ): Try[Stock] = for {
    _     <- validateTicker(ticker)
    pe    <- parseDouble(peStr, "P/E")
    eps   <- parseDouble(epsStr, "EPS")
    price <- parseDouble(priceStr, "Price")
    qty   <- parseDouble(qtyStr, "Quantity")
  } yield Stock(ticker.toUpperCase, pe, eps, price, qty)

  private def validateTicker(t: String): Try[String] =
    if (t != null && t.nonEmpty) Success(t)
    else Failure(new IllegalArgumentException("Ticker cannot be empty"))

  private def parseDouble(value: String, fieldName: String): Try[Double] =
    // Treat empty string as 0.0 (useful for quantity)
    if (value.trim.isEmpty) Success(0.0)
    else {
      Try(value.toDouble).recoverWith { case _ =>
        Failure(new IllegalArgumentException(s"$fieldName must be a valid number, got '$value'"))
      }

    }

}
