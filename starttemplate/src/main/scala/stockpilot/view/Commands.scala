package stockpilot.view

import scala.io.StdIn.readLine
import scala.util.{Success, Failure}
import stockpilot.model.{SortByTicker, SortByPriceAsc, SortByPriceDesc}
import stockpilot.controller.StockController

// Command interface
trait Command {
  def execute(): Unit
  def description: String // to print in munu
}

// Command: add stock
class AddStockCommand(controller: StockController) extends Command {
  def description: String = "Add stock"
  def execute(): Unit     = {
    val t  = readLine("Ticker: ")
    val p  = readLine("P/E: ")
    val e  = readLine("EPS: ")
    val pr = readLine("Price: ")

    /*if (controller.addStockFromInput(t, p, e, pr)) println(s"Added $t.")
    else println(s"Error adding $t.")*/
    // ! Handling the Try Monad from controller
    controller.addStockFromInput(t, p, e, pr) match {
      case Success(_)  => println(s"Successfully added $t.")
      case Failure(ex) => println(s"Error: ${ex.getMessage}")
    }
  }
}
// Command: Show all
class ShowAllCommand(controller: StockController)  extends Command {
  def description: String = "Show all stocks"
  def execute(): Unit     = {
    val grid = CLIViewHelpers.drawStockRow(controller.allStocks, 20)
    println(grid)
  }
}

// Command: Filter
class FilterStockCommand(controller: StockController) extends Command {
  def description: String = "Filter by price [min-max]"
  def execute(): Unit     = {
    val raw   = readLine("Enter price range (min-max): ")
    val parts = raw.split("-").map(_.trim)
    if (parts.length == 2) {
      (CLIViewHelpers.toDouble(parts(0)), CLIViewHelpers.toDouble(parts(1))) match {
        case (Some(min), Some(max)) =>
          //
          val found = controller.filterByPrice(min, max)
          println(CLIViewHelpers.drawStockRow(found, 20))
        case _                      => println("Invalid numbers.")
      }
    } else { println("Invalid format. Use: 10-100") }
  }
}

// Command: Delete
class DeleteStockCommand(controller: StockController) extends Command {
  def description: String = "Delete stock by ticker"
  def execute(): Unit     = {
    val t = readLine("Ticker to delete: ")
    if (controller.deleteStock(t)) println("Deleted.") else println("Not found.")
  }
}

// Command: Change strategy (nested mini-menu)
class ChangeStrategyCommand(controller: StockController) extends Command {
  def description: String = "Change Sort Strategy"
  def execute(): Unit     = {
    println("a) Ticker, b) PriceAsc, c) PriceDesc")
    readLine("Choice: ") match {
      case "a" => controller.setSortStrategy(SortByTicker)
      case "b" => controller.setSortStrategy(SortByPriceAsc)
      case "c" => controller.setSortStrategy(SortByPriceDesc)
      case _   => println("Unknown.")
    }
  }
}

//! --- NEW UI COMMAND FOR UNDO ---
class UndoCommand(controller: StockController) extends Command {
  def description: String = "Undo last add/delete"
  def execute(): Unit     =
    if (controller.undoLastAction()) println("Undo successful.") else println("Nothing to undo.")
}
