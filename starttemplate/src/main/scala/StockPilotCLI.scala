import scala.io.StdIn.readLine
import scala.util.Try
import scala.math.BigDecimal
import scala.math.BigDecimal.RoundingMode

/** Simple CLI for managing in-memory stocks. Requirements covered:
  *   - Mandatory fields: TICKER, P/E, EPS, Price
  *   - No duplicate tickers (case-insensitive, stored as UPPERCASE)
  *   - List all stocks sorted by ticker
  *   - Filter by price with inclusive [min, max], dot (.) as decimal separator; if more than 2
  *     decimals given for bounds -> round to 2
  *   - Delete by ticker
  *   - Keep existing grid formatting by reusing drawStockRow from Main.scala
  *
  * NOTE: This file relies on:
  *   - case class Stock(...) from Main.scala
  *   - def drawStockRow(stocks: List[Stock], cellWidth: Int): String from Main.scala
  */

// Single place to seed initial content (mirrors what you print in startSimpleStockPilot)
private object Seed {
  val initial: List[Stock] = List(
    Stock("RR.L", 16.64, 0.6852, 798.99),
    Stock("AAPL", 28.5, 5.51, 420.33),
    Stock("GOOGL", 26.8, 110.23, 120.01),
    Stock("GOOG", 12.01, 0.3, 98.01)
  )
}

object StockPilotCLI {

  // In-memory state; key is normalized (UPPERCASE) ticker
  private var stocks: Map[String, Stock] =
    Seed.initial.map(s => s.ticker.toUpperCase -> s.copy(ticker = s.ticker.toUpperCase)).toMap

  private val cellWidth = 20
  private val eol = sys.props("line.separator")

  def run(): Unit = {
    println(eol + "Hey, I'm your SIMPLE StockPilot (CLI mode)!")
    mainLoop()
  }

  // ----- Main Loop -----
  private def mainLoop(): Unit = {
    var continue = true
    while continue do
      printMenu()
      readLine("Choose [1-5]: ").trim match
        case "1" => addStock()
        case "2" => listAll()
        case "3" => filterByPrice()
        case "4" => deleteStock()
        case "5" => continue = false
        case _   => println("Unknown command. Please choose 1..5.")
    println("Bye!")
  }

  private def printMenu(): Unit = {
    println()
    println("=== StockPilot Menu ===")
    println("1) Add stock")
    println("2) Show all stocks")
    println("3) Filter by price [min-max], inclusive; use '.' as decimal separator")
    println("4) Delete stock by ticker")
    println("5) Exit")
    println()
  }

  // ----- 1) Add stock -----
  private def addStock(): Unit = {
    val rawTicker = readNonEmpty("Enter ticker (required, will be uppercased): ")
    val ticker = rawTicker.toUpperCase

    if stocks.contains(ticker) then
      println(s"Ticker '$ticker' already exists. Nothing added.")
      return

    val pe = readRequiredDoubleDot("Enter P/E (use '.' as decimal): ")
    val eps = readRequiredDoubleDot("Enter EPS (use '.' as decimal): ")
    val pr = readRequiredDoubleDot("Enter Price (use '.' as decimal): ")

    val st = Stock(ticker, pe, eps, pr)
    stocks = stocks.updated(ticker, st)
    println(s"Added: $ticker")
  }

  // ----- 2) Show all stocks (sorted by ticker) -----
  private def listAll(): Unit = {
    if stocks.isEmpty then
      println("No stocks to print")
      return
    val sorted = stocks.values.toList.sortBy(_.ticker)
    val grid = drawStockRow(sorted, cellWidth) // reuse your existing printer
    println(grid)
  }

  // ----- 3) Filter by price [min-max], inclusive, dot as separator -----
  private def filterByPrice(): Unit = {
    var stay = true
    while stay do
      val raw = readNonEmpty("Enter price range as min-max (use '.' as decimal): ")

      // Enforce dot separator only
      if raw.contains(",") then println("Use '.' as decimal separator. Try again.")
      else
        val parts = raw.split("-").map(_.trim)
        if parts.length != 2 then
          println("Format must be: min-max (example: 23.60-45.03). Try again.")
        else
          val maybeMin = toDouble(parts(0))
          val maybeMax = toDouble(parts(1))
          (maybeMin, maybeMax) match
            case (Some(min0), Some(max0)) =>
              // Round to 2 decimals if more than 2 provided (filter-only requirement)
              val min = round2(min0)
              val max = round2(max0)
              if min > max then println(s"min > max ($min > $max). Try again.")
              else
                val found = stocks.values.toList
                  .filter(s => s.price >= min && s.price <= max)
                  .sortBy(_.ticker)

                if found.isEmpty then
                  println(s"No results in [$min, $max].")
                  stay = askYesNo("Change range and try again? (y/n): ")
                else
                  val grid = drawStockRow(found, cellWidth)
                  println(grid)
                  stay = askYesNo("Filter again with a different range? (y/n): ")
            case _ =>
              println("Both min and max must be valid numbers. Try again.")
  }

  // ----- 4) Delete stock -----
  private def deleteStock(): Unit = {
    val ticker = readNonEmpty("Enter ticker to delete: ").toUpperCase
    stocks.get(ticker) match
      case None =>
        println(s"Ticker '$ticker' not found.")
      case Some(st) =>
        // Show a compact single-row preview using your formatter
        val preview = drawStockRow(List(st), cellWidth)
        println(preview)
        if askYesNo(s"Delete '$ticker'? (y/n): ") then
          stocks -= ticker
          println(s"Deleted: $ticker")
        else println("Canceled.")
  }

  // ----- Utilities -----

  // Require non-empty text (Scala 3: no do-while)
  private def readNonEmpty(prompt: String): String = {
    var s = ""
    var first = true
    while first || s.isEmpty do
      s = readLine(prompt).trim
      if s.isEmpty then println("Value is required.")
      first = false
    s
  }

  // Require valid double; reject comma as decimal separator
  private def readRequiredDoubleDot(prompt: String): Double = {
    var ok = false
    var out = 0.0
    while !ok do
      val s = readLine(prompt).trim
      if s.isEmpty then println("Value is required.")
      else if s.contains(",") then println("Use '.' as decimal separator.")
      else
        toDouble(s) match
          case Some(d) =>
            out = d
            ok = true
          case None =>
            println("Invalid number. Try again.")
    out
  }

  private def toDouble(s: String): Option[Double] =
    Try(s.toDouble).toOption

  private def round2(d: Double): Double =
    BigDecimal(d).setScale(2, RoundingMode.HALF_UP).toDouble

  private def askYesNo(prompt: String): Boolean = {
    val ans = readLine(prompt).trim.toLowerCase
    ans == "y" || ans == "yes"
  }
}

@main def main(): Unit =
  StockPilotCLI.run()
