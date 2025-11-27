package stockpilot.app

import stockpilot.model.{Stock, StockRepository}
import stockpilot.controller.StockController
import stockpilot.view.CLIView

@main def main(): Unit = {
  // Initial stocks – same data as before
  val initialStocks = List(
    Stock("RR.L", 16.64, 0.6852, 798.99),
    Stock("AAPL", 28.50, 5.5100, 420.33),
    Stock("GOOGL", 26.80, 110.23, 120.01),
    Stock("GOOG", 12.01, 0.3000, 98.01)
  )

  // Build MVC
  val repo = new StockRepository(initialStocks)
  val controller = new StockController(repo)
  val view = new CLIView(controller)

  // Register view as observer of controller
  controller.addObserver(view)

  val eol = sys.props("line.separator")
  println(eol + "Hey, I'm your SIMPLE StockPilot (CLI mode)!")

  // Start text UI
  view.run()
}
