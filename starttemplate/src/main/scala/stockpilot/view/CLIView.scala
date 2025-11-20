package stockpilot.view

import scala.io.StdIn.readLine
import scala.util.Try
import scala.math.BigDecimal
import scala.math.BigDecimal.RoundingMode

import stockpilot.model.Stock
import stockpilot.controller.{StockController, Observer}

/** Helper functions extracted so they can be tested without running interactive UI. */
object CLIViewHelpers {

  /** Require a safe conversion to Double, returning Option[Double]. */
  def toDouble(s: String): Option[Double] =
    Try(s.toDouble).toOption

  /** Round to 2 decimal places using HALF_UP. */
  def round2(d: Double): Double =
    BigDecimal(d).setScale(2, RoundingMode.HALF_UP).toDouble

  /** Render a list of stocks as a horizontal grid (extracted from CLIView). */
  def drawStockRow(stocks: List[Stock], cellWidth: Int): String = {
    if (stocks.isEmpty) return "No stocks to print"

    val eol = sys.props("line.separator")
    val builder = new StringBuilder

    val topAndBottomBar = ("+" + "-" * cellWidth) * stocks.length + "+" + eol
    builder.append(topAndBottomBar)

    // Lines 0..3
    for (lineIdx <- 0 until 4) {
      builder.append("|")
      for (stock <- stocks) {
        val line = stock.toLines(lineIdx)
        builder.append(line.padTo(cellWidth, ' ').mkString + "|")
      }
      builder.append(eol)
    }

    builder.append(topAndBottomBar)
    builder.toString()
  }
}

/** Text-based user interface (View layer). Handles all input/output and delegates logic to
  * StockController.
  *
  * NOTE: interactive methods still use readLine/println — we did not change UI behaviour. We only
  * extracted deterministic helpers into CLIViewHelpers to allow unit testing.
  */
class CLIView(controller: StockController) extends Observer {

  private val cellWidth = 20

  /** Start the main interaction loop. */
  def run(): Unit =
    mainLoop()

  /** Called by controller when model changes. For now we don't auto-refresh anything in CLI, so
    * leave it empty.
    */
  override def update(): Unit = {
    // No-op for simple CLI
  }

  // ----- Main Loop -----

  private def mainLoop(): Unit = {
    var continue = true
    while (continue) {
      printMenu()
      readLine("Choose [1-5]: ").trim match {
        case "1" => addStockFlow()
        case "2" => showAll()
        case "3" => filterByPriceFlow()
        case "4" => deleteStockFlow()
        case "5" => continue = false
        case _   => println("Unknown command. Please choose 1..5.")
      }
    }
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

  // ----- Flows for each menu command -----

  // 1) Add stock
  private def addStockFlow(): Unit = {
    val rawTicker = readNonEmpty("Enter ticker (required, will be uppercased): ")
    val ticker = rawTicker.toUpperCase

    if (controller.exists(ticker)) {
      println(s"Ticker '$ticker' already exists. Nothing added.")
      return
    }

    val pe = readRequiredDoubleDot("Enter P/E (use '.' as decimal): ")
    val eps = readRequiredDoubleDot("Enter EPS (use '.' as decimal): ")
    val pr = readRequiredDoubleDot("Enter Price (use '.' as decimal): ")

    val added = controller.addStock(ticker, pe, eps, pr)
    if (added) println(s"Added: $ticker")
    else println(s"Could not add '$ticker' (unknown error).")
  }

  // 2) Show all stocks
  private def showAll(): Unit = {
    val all = controller.allStocks
    if (all.isEmpty) {
      println("No stocks to print")
    } else {
      val grid = CLIViewHelpers.drawStockRow(all, cellWidth)
      println(grid)
    }
  }

  // 3) Filter by price
  private def filterByPriceFlow(): Unit = {
    var stay = true
    while (stay) {
      val raw = readNonEmpty("Enter price range as min-max (use '.' as decimal): ")

      if (raw.contains(",")) {
        println("Use '.' as decimal separator. Try again.")
      } else {
        val parts = raw.split("-").map(_.trim)
        if (parts.length != 2) {
          println("Format must be: min-max (example: 23.60-45.03). Try again.")
        } else {
          val maybeMin = CLIViewHelpers.toDouble(parts(0))
          val maybeMax = CLIViewHelpers.toDouble(parts(1))
          (maybeMin, maybeMax) match {
            case (Some(min0), Some(max0)) =>
              val min = CLIViewHelpers.round2(min0)
              val max = CLIViewHelpers.round2(max0)
              if (min > max) {
                println(s"min > max ($min > $max). Try again.")
              } else {
                val found = controller.filterByPrice(min, max)
                if (found.isEmpty) {
                  println(s"No results in [$min, $max].")
                  stay = askYesNo("Change range and try again? (y/n): ")
                } else {
                  val grid = CLIViewHelpers.drawStockRow(found, cellWidth)
                  println(grid)
                  stay = askYesNo("Filter again with a different range? (y/n): ")
                }
              }
            case _ =>
              println("Both min and max must be valid numbers. Try again.")
          }
        }
      }
    }
  }

  // 4) Delete stock
  private def deleteStockFlow(): Unit = {
    val ticker = readNonEmpty("Enter ticker to delete: ").toUpperCase
    controller.getStock(ticker) match {
      case None =>
        println(s"Ticker '$ticker' not found.")
      case Some(st) =>
        val preview = CLIViewHelpers.drawStockRow(List(st), cellWidth)
        println(preview)
        if (askYesNo(s"Delete '$ticker'? (y/n): ")) {
          val deleted = controller.deleteStock(ticker)
          if (deleted) println(s"Deleted: $ticker")
          else println(s"Could not delete '$ticker'.")
        } else {
          println("Canceled.")
        }
    }
  }

  // ----- Input helpers -----

  /** Require non-empty text input. */
  private def readNonEmpty(prompt: String): String = {
    var s = ""
    var first = true
    while (first || s.isEmpty) {
      s = readLine(prompt).trim
      if (s.isEmpty) println("Value is required.")
      first = false
    }
    s
  }

  /** Require a valid Double with '.' as decimal separator. */
  private def readRequiredDoubleDot(prompt: String): Double = {
    var ok = false
    var out = 0.0
    while (!ok) {
      val s = readLine(prompt).trim
      if (s.isEmpty) {
        println("Value is required.")
      } else if (s.contains(",")) {
        println("Use '.' as decimal separator.")
      } else {
        CLIViewHelpers.toDouble(s) match {
          case Some(d) =>
            out = d
            ok = true
          case None =>
            println("Invalid number. Try again.")
        }
      }
    }
    out
  }

  private def askYesNo(prompt: String): Boolean = {
    val ans = readLine(prompt).trim.toLowerCase
    ans == "y" || ans == "yes"
  }
}
