package stockpilot.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*
import _root_.stockpilot.model.{Stock, StockRepository}

class DeleteStockActionSpec extends AnyWordSpec {

  "DeleteStockAction" should {
    "delete a stock on execute and restore it on undo" in {

      val repo = new StockRepository()
      val s    = Stock("AAPL", 10.0, 1.0, 100.0, 0)
      repo.add(s) shouldBe true

      val action = new DeleteStockAction(s, repo)

      action.execute()
      repo.exists("AAPL") shouldBe false

      action.undo()
      repo.exists("AAPL") shouldBe true
    }

  }

}
