package stockpilot.app

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import stockpilot.model.{Stock, FileIO, StockMemento}

class BootstrapSpec extends AnyWordSpec with Matchers {

  // Mock implementation needed for the test
  class MockIO extends FileIO {
    def load                  = StockMemento(Nil)
    def save(m: StockMemento) = {}
  }

  val mockIO = new MockIO

  "Bootstrap.build" should {
    "create repo, controller and view from initial stocks" in {
      val initial                  = List(Stock("RR.L", 16.64, 0.6852, 798.99), Stock("AAPL", 28.5, 5.51, 420.33))
      // FIX: Pass mockIO to the build method
      val (repo, controller, view) = Bootstrap.build(initial, mockIO)

      repo.all.map(_.ticker).sorted should contain allElementsOf List("AAPL", "RR.L")
      controller.exists("AAPL") shouldBe true
      view should not be null
    }
  }
}
