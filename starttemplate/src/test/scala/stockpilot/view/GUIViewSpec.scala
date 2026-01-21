package stockpilot.view

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import stockpilot.controller.StockController
import stockpilot.model.{StockRepository, FileIO, StockMemento}

class GUIViewSpec extends AnyWordSpec with Matchers {

  // Mock FileIO needed for Controller
  class MockIO extends FileIO {
    def load                  = StockMemento(Nil)
    def save(m: StockMemento) = {}
  }

  "GUIView" should {
    "be instantiable and register as observer" in {
      // Prepare MVC parts
      val repo = new StockRepository(Nil)
      val ctrl = new StockController(repo, new MockIO)

      // Initialize GUI (headless mode check)
      // Note: In some CI environments, this might fail if no display is available.
      // Usually sbt handles this, or we skip if needed.
      noException should be thrownBy {
        val gui = new GUIView(ctrl)
        ctrl.addObserver(gui)

        // Trigger update to see if GUI crashes on redraw
        ctrl.addStockFromInput("TEST", "1", "1", "1", "0")
      }
    }
  }
}
