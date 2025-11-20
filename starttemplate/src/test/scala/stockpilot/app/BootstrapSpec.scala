package stockpilot.app

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import stockpilot.model.Stock

class BootstrapSpec extends AnyWordSpec with Matchers {

  "Bootstrap.build" should {
    "create repo, controller and view from initial stocks" in {
      val initial = List(
        Stock("RR.L", 16.64, 0.6852, 798.99),
        Stock("AAPL", 28.5, 5.51, 420.33)
      )
      val (repo, controller, view) = Bootstrap.build(initial)
      repo.all.map(_.ticker).sorted should contain allElementsOf List("AAPL", "RR.L")
      controller.exists("AAPL") shouldBe true
      view should not be null
    }
  }
}
