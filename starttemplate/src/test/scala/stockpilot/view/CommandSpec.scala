package stockpilot.controller.stockpilot.view

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import stockpilot.controller.StockController
import stockpilot.model.StockRepository
import stockpilot.view.{
  AddStockCommand,
  ShowAllCommand,
  DeleteStockCommand,
  FilterStockCommand,
  UndoCommand,
  ChangeStrategyCommand
}

class CommandSpec extends AnyWordSpec with Matchers {
  "Commands" should {
    "have correct descriptions" in {
      val repo = new StockRepository(Nil)
      val ctrl = new StockController(repo)

      new AddStockCommand(ctrl).description shouldBe "Add stock"
      new ShowAllCommand(ctrl).description shouldBe "Show all stocks"
      new FilterStockCommand(ctrl).description shouldBe "Filter by price [min-max]"
      new DeleteStockCommand(ctrl).description shouldBe "Delete stock by ticker"
      new ChangeStrategyCommand(ctrl).description shouldBe "Change Sort Strategy"
      new UndoCommand(ctrl).description shouldBe "Undo last add/delete"
    }
  }
}
