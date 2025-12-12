package stockpilot.app

import stockpilot.model.{Stock, StockRepository, FileIO}
import stockpilot.controller.StockController
import stockpilot.view.CLIView
import javax.imageio.stream.FileImageOutputStream

/** Small bootstrap helper to build repository/controller/view with given initial data. This is
  * test-friendly: we can assert that the returned objects are properly wired.
  */
object Bootstrap {
  def build(initial: List[Stock], fileIO: FileIO): (StockRepository, StockController, CLIView) = {
    val repo       = new StockRepository(initial)
    // val fileIO     = new MockFileIO()
    val controller = new StockController(repo, fileIO)
    val view       = new CLIView(controller)
    (repo, controller, view)
  }
}
