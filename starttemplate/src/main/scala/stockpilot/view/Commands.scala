package stockpilot.view

import scala.io.StdIn.readLine
import scala.util.{Success, Failure, Try}
import stockpilot.controller.IStockController
import stockpilot.model.{Stock, Verdict, SortByTicker, SortByPriceAsc, SortByPriceDesc}

trait Command {
  def execute(): Unit
  def description: String
}

// --- COMMANDS IMPLEMENTATION ---

class AddStockCommand(controller: IStockController) extends Command {
  def description: String = "Add stock (Unified Flow)"

  def execute(): Unit = {
    println("\n--- Add New Stock ---")
    val t     = readLine("Ticker: ")
    val pStr  = readLine("P/E: ")
    val eStr  = readLine("EPS: ")
    val prStr = readLine("Price: ")

    // Preview Verdict (UX Logic)
    val epsOpt   = Try(eStr.toDouble).toOption
    val priceOpt = Try(prStr.toDouble).toOption

    if (epsOpt.isDefined && priceOpt.isDefined) {
      // Create a temporary stock just to calculate verdict
      val tempStock = Stock(t, 0, epsOpt.get, priceOpt.get)
      println(s">>> ANALYST VERDICT: ${tempStock.verdict} <<<")
    }

    println("How many shares to buy? (Enter 0 for Watchlist)")
    val qStr = readLine("Quantity: ")

    // Call controller with 5 arguments
    controller.addStockFromInput(t, pStr, eStr, prStr, qStr) match {
      case Success(_)  => println(s"Successfully added $t.")
      case Failure(ex) => println(s"Error: ${ex.getMessage}")
    }
  }
}

class ShowAllCommand(controller: IStockController) extends Command {
  def description: String = "Show all stocks"

  def execute(): Unit = {
    val stocks = controller.allStocks
    printTable(stocks)
  }

  // Helper method to print stocks nicely without external dependencies
  private def printTable(stocks: List[Stock]): Unit =
    if (stocks.isEmpty) { println("List is empty.") }
    else {
      println(f"${"Ticker"}%-10s ${"Price"}%10s ${"Qty"}%8s ${"Total"}%12s ${"Verdict"}%10s")
      println("-" * 55)
      stocks.foreach { s =>
        println(
          f"${s.ticker}%-10s ${s.price}%10.2f ${s.quantity}%8.1f ${s.totalValue}%12.2f ${s.verdict}%10s"
        )
      }
      println("-" * 55)
    }
}

class FilterStockCommand(controller: IStockController) extends Command {
  def description: String = "Filter by price [min-max]"

  def execute(): Unit = {
    println("Enter price range (e.g. '10 100'):")
    val input = readLine()
    val parts = input.split(" ")

    if (parts.length == 2) {
      val minTry = Try(parts(0).toDouble)
      val maxTry = Try(parts(1).toDouble)

      (minTry, maxTry) match {
        case (Success(min), Success(max)) =>
          val found = controller.filterByPrice(min, max)
          println(s"Found ${found.size} stocks:")
          // Reuse simple printing logic
          found.foreach(s => println(f"${s.ticker}: ${s.price}"))

        case _ => println("Invalid numbers.")
      }
    } else { println("Invalid format. Please enter two numbers separated by space.") }
  }
}

class DeleteStockCommand(controller: IStockController) extends Command {
  def description: String = "Delete stock by ticker"
  def execute(): Unit     = {
    val ticker = readLine("Enter ticker to delete: ")
    if (controller.deleteStock(ticker)) println(s"Deleted $ticker.")
    else println(s"Stock $ticker not found.")
  }
}

class ChangeStrategyCommand(controller: IStockController) extends Command {
  def description: String = "Change Sort Strategy"
  def execute(): Unit     = {
    println("Choose strategy: 1) Ticker  2) Price Asc  3) Price Desc")
    readLine() match {
      case "1" => controller.setSortStrategy(SortByTicker)
      case "2" => controller.setSortStrategy(SortByPriceAsc)
      case "3" => controller.setSortStrategy(SortByPriceDesc)
      case _   => println("Invalid selection")
    }
  }
}

class UndoCommand(controller: IStockController) extends Command {
  def description: String = "Undo last add/delete"
  def execute(): Unit     =
    if (controller.undoLastAction()) println("Undo successful.") else println("Nothing to undo.")
}

// NEW COMMAND
class GenerateReportCommand(controller: IStockController) extends Command {
  def description: String = "Generate Portfolio Report (CSV)"
  def execute(): Unit     = controller.generateReport() match {
    case Success(msg) => println(msg)
    case Failure(ex)  => println(s"Failed to generate report: ${ex.getMessage}")
  }
}
