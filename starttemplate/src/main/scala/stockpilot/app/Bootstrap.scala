package stockpilot.app

import stockpilot.model.{Stock, StockRepository, FileIO}
import stockpilot.controller.StockController
import stockpilot.view.CLIView

/** Small bootstrap helper to build repository/controller/view with given initial data. Adapted for
  * the zero-argument repository constructor.
  */
object Bootstrap {
  def build(initial: List[Stock], fileIO: FileIO): (StockRepository, StockController, CLIView) = {
    // 1. Create empty repository (constructor has changed for DI)
    val repo = new StockRepository()

    // 2. Populate it manually with initial data
    initial.foreach(repo.add)

    // 3. Wire the rest
    val controller = new StockController(repo, fileIO)
    val view       = new CLIView(controller)

    (repo, controller, view)
  }
}
