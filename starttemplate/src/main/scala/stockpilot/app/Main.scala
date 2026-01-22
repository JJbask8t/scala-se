package stockpilot.app

import stockpilot.StockModule
import stockpilot.view.{CLIView, GUIView}
import stockpilot.model.Stock

@main
def main(): Unit = {

  // Default data definition moved here (Clean Code)
  val defaultStocks = List(
    Stock("RR.L", 16.64, 0.6852, 798.99, 0),
    Stock("AAPL", 28.50, 5.5100, 420.33, 10),
    Stock("GOOGL", 26.80, 110.23, 120.01, 5),
    Stock("GOOG", 12.01, 0.3000, 98.01, 0)
  )

  // 1. Setup via Guice Module
  val controller = StockModule.setupController(defaultStocks)

  // 2. Initialize Views
  val tui = new CLIView(controller)
  val gui = new GUIView(controller)

  controller.addObserver(tui)
  controller.addObserver(gui)

  val eol = sys.props("line.separator")
  println(s"$eol StockPilot initialized via Google Guice DI.")

  // 3. Start
  tui.run()

  println("Auto-saving...")
  controller.save()
  sys.exit(0)
}
