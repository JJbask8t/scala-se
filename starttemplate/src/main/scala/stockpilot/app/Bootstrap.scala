package stockpilot.app

import stockpilot.model.{Stock, StockRepository}
import stockpilot.controller.StockController
import stockpilot.view.CLIView

/** Small bootstrap helper to build repository/controller/view with given initial data. This is
  * test-friendly: we can assert that the returned objects are properly wired.
  */
object Bootstrap {
  def build(initial: List[Stock]): (StockRepository, StockController, CLIView) = {
    val repo = new StockRepository(initial)
    val controller = new StockController(repo)
    val view = new CLIView(controller)
    (repo, controller, view)
  }
}
