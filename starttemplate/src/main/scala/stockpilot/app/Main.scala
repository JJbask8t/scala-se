package stockpilot.app

import stockpilot.model._
import stockpilot.controller.StockController
import stockpilot.view.{CLIView, GUIView}

/* //! Mock Implementation for Stage 1 (To keep code compilable)
class MockFileIO extends FileIO {
  override def load: StockMemento          = {
    println("Mock load called")
    StockMemento(Nil)
  }
  override def save(m: StockMemento): Unit =
    println(s"Mock save called with ${m.stocks.size} stocks")
} */

@main
def main(): Unit = {
  // ! Try to load appsettings from file, otherwise use default list

  // ! 1. Option 1: XML
  // val fileIO = new FileIOXml()

  // ! 1. Option 2: JSON (Selected)
  val fileIO = new FileIOJson()

  // 2. Load state
  val loadedMemento = fileIO.load
  val initialStocks =
    if (loadedMemento.stocks.nonEmpty) { loadedMemento.stocks }
    else {
      // Default data if file is empty or missing
      List(
        Stock("RR.L", 16.64, 0.6852, 798.99),
        Stock("AAPL", 28.50, 5.5100, 420.33),
        Stock("GOOGL", 26.80, 110.23, 120.01),
        Stock("GOOG", 12.01, 0.3000, 98.01)
      )
    }

  // 3. Wiring with Dependency Injection
  val repo       = new LoggingRepository(new StockRepository(initialStocks)) // Wrap in Decorator
  // val fileIO     = new MockFileIO()        // replaced by FileIO: JSON +- XML
  val controller = new StockController(repo, fileIO)

  // ! 4. Create Views
  val tui = new CLIView(controller)
  val gui = new GUIView(controller)

  // 5. Register view observers
  controller.addObserver(tui)
  controller.addObserver(gui)

  val eol = sys.props("line.separator")
  println(eol + "Hey, I'm your SIMPLE StockPilot (CLI mode)!")
  println("GUI launched. TUI running in console.")

  // 6. Start text UI
  tui.run()
  println("Auto-saving...")
  controller.save()
  sys.exit(0) // GUI close
}
