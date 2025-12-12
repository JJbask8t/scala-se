package stockpilot.view

import scala.io.StdIn.readLine
import stockpilot.controller.{StockController, Observer}
import stockpilot.model.Stock

// remove CLIViewHelpers.toDouble and other parsers,
// now StockFactory handles parsing inside the Controller

object CLIViewHelpers {
  import scala.math.BigDecimal
  import scala.math.BigDecimal.RoundingMode
  import stockpilot.model.Stock

  // for rounding during OUTPUT (filtering)
  def toDouble(s: String): Option[Double] = scala.util.Try(s.toDouble).toOption

  def round2(d: Double): Double = BigDecimal(d).setScale(2, RoundingMode.HALF_UP).toDouble

  def drawStockRow(stocks: List[Stock], cellWidth: Int): String = {
    if (stocks.isEmpty) return "No stocks to print"
    val eol             = sys.props("line.separator")
    val builder         = new StringBuilder
    val topAndBottomBar = ("+" + "-" * cellWidth) * stocks.length + "+" + eol
    builder.append(topAndBottomBar)
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

/*class CLIView(controller: StockController) extends Observer {
  private val cellWidth = 20

  def run(): Unit = mainLoop()

  override def update(): Unit = {
    // MVC: future => display a message (data updat), for the CLI is unnecessary
  }

  private def mainLoop(): Unit = {
    var continue = true
    while (continue) {
      printMenu()
      readLine("Choose [1-6]: ").trim match {
        case "1" => addStockFlow()
        case "2" => showAll()
        case "3" => filterByPriceFlow()
        case "4" => deleteStockFlow()
        case "5" => changeSortStrategyFlow() // NEW MENU ITEM
        case "6" => continue = false
        case _   => println("Unknown command.")
      }
    }
    println("Bye!")
  }

  private def printMenu(): Unit = {
    println("\n=== StockPilot Menu ===")
    println("1) Add stock")
    println("2) Show all stocks")
    println("3) Filter by price [min-max]")
    println("4) Delete stock by ticker")
    println("5) Change Sort Strategy (Price/Ticker)") // Strategy Pattern UI
    println("6) Exit")
  }

  // 1) Add stock - NOW USES FACTORY THROUGH CONTROLLER
  private def addStockFlow(): Unit = {
    val rawTicker = readNonEmpty("Enter ticker: ")
    // no more parse Double here, just read strings!
    val peStr = readNonEmpty("Enter P/E: ")
    val epsStr = readNonEmpty("Enter EPS: ")
    val priceStr = readNonEmpty("Enter Price: ")

    // Send the strings to the controller. Factory cannot create Stock -> return false
    val added = controller.addStockFromInput(rawTicker, peStr, epsStr, priceStr)

    if (added) println(s"Added: $rawTicker")
    else println(s"Could not add '$rawTicker'. Invalid numbers or duplicate.")
  }

  // 2) Show all
  private def showAll(): Unit = {
    val all = controller.allStocks // will return a list sorted by the current Strategy.
    println(CLIViewHelpers.drawStockRow(all, cellWidth))
  }

  // 3) Filter
  private def filterByPriceFlow(): Unit = {
    val raw = readNonEmpty("Enter price range (min-max): ")
    val parts = raw.split("-").map(_.trim)
    if (parts.length == 2) {
      (CLIViewHelpers.toDouble(parts(0)), CLIViewHelpers.toDouble(parts(1))) match {
        case (Some(min), Some(max)) =>
          val found = controller.filterByPrice(min, max)
          println(CLIViewHelpers.drawStockRow(found, cellWidth))
        case _ => println("Invalid numbers.")
      }
    } else println("Invalid format.")
  }

  // 4) Delete
  private def deleteStockFlow(): Unit = {
    val ticker = readNonEmpty("Enter ticker to delete: ")
    if (controller.deleteStock(ticker)) println("Deleted.")
    else println("Not found.")
  }

  // 5) NEW METHOD: Change of strategy
  private def changeSortStrategyFlow(): Unit = {
    println("Choose sorting:")
    println("a) By Ticker (A-Z)")
    println("b) By Price (Cheap first)")
    println("c) By Price (Expensive first)")
    readLine("Choice: ").trim.toLowerCase match {
      case "a" =>
        controller.setSortStrategy(stockpilot.model.SortByTicker)
        println("Sorted by Ticker.")
      case "b" =>
        controller.setSortStrategy(stockpilot.model.SortByPriceAsc)
        println("Sorted by Price (Asc).")
      case "c" =>
        controller.setSortStrategy(stockpilot.model.SortByPriceDesc)
        println("Sorted by Price (Desc).")
      case _ => println("Unknown choice.")
    }
  }

  private def readNonEmpty(prompt: String): String = {
    var s = ""
    while (s.isEmpty) {
      s = readLine(prompt).trim
    }
    s
  }
}
 */

class CLIView(controller: StockController) extends Observer {

  // Command initialization (Command Pattern)
  private val commands: Map[String, Command] = Map(
    "1" -> new AddStockCommand(controller),
    "2" -> new ShowAllCommand(controller),
    "3" -> new FilterStockCommand(controller),
    "4" -> new DeleteStockCommand(controller),
    "5" -> new ChangeStrategyCommand(controller),
    "6" -> new UndoCommand(controller)

    // ! NEW : save/load settings
    // "7" -> new SaveCommand(controller)
    // "8" -> new LoadCommand(controller)
  )

  def run(): Unit = {
    var continue = true
    while (continue) {
      println("\n=== Menu ===")
      // Dynamic menu generation from a list of commands
      commands.toList.sortBy(_._1).foreach { case (key, cmd) =>
        println(s"$key) ${cmd.description}")
      }
      println("0) Exit")

      val choice = readLine("Choice: ").trim
      if (choice == "0") continue = false
      else {
        commands.get(choice) match {
          case Some(cmd) => cmd.execute() // Polymorphic call to execute()
          case None      => println("Unknown command")
        }
      }
    }
    print("Bye!\n")
  }

  override def update(): Unit = {}
}
