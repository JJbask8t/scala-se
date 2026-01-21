package stockpilot.model

import scala.util.{Try, Success, Failure}

object StockFactory {

  /** Creates a Stock from raw string inputs safely using the Try Monad. Returns Success(Stock) if
    * all inputs are valid, or Failure(exception) otherwise.
    */
  def createStock(ticker: String, peStr: String, epsStr: String, priceStr: String): Try[Stock] =
    // Try monad allows to chain operations. If any step fails, the result is Failure.
    for {
      _ <- validateTicker(ticker)
      pe <- parseDouble(peStr, "P/E")
      eps <- parseDouble(epsStr, "EPS")
      price <- parseDouble(priceStr, "Price")
    } yield Stock(ticker.toUpperCase, pe, eps, price)

  // Helper to validate ticker
  private def validateTicker(t: String): Try[String] =
    if (t != null && t.nonEmpty) Success(t)
    else Failure(new IllegalArgumentException("Ticker cannot be empty"))

  // Helper to parse double with a clear error message
  private def parseDouble(value: String, fieldName: String): Try[Double] = Try(value.toDouble)
    .recoverWith { case _ =>
      Failure(new IllegalArgumentException(s"$fieldName must be a valid number, got '$value'"))
    }
}
