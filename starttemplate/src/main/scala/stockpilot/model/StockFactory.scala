package stockpilot.model

import scala.util.Try

// Pattern Factory Method via Object (Singleton)\

object StockFactory {

  /** Creates Stock from raw string data. Returns Option[Stock], hiding the complexity of parsing
    * numbers.
    */
  def createStock(
      ticker: String,
      peStr: String,
      epsStr: String,
      priceStr: String
  ): Option[Stock] = {
    // try to parse the numbers; if there is an error, we return None.
    val stockOrNone = for {
      pe <- Try(peStr.toDouble).toOption
      eps <- Try(epsStr.toDouble).toOption
      price <- Try(priceStr.toDouble).toOption
      if ticker.nonEmpty // Validation: the ticker must not be empty
    } yield Stock(ticker.toUpperCase, pe, eps, price)

    stockOrNone
  }
}
