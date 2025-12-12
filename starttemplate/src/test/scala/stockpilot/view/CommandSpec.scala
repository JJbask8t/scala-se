package stockpilot.view

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import stockpilot.controller.StockController
import stockpilot.model.{StockRepository, FileIO, StockMemento}

class CommandSpec extends AnyWordSpec with Matchers {

//! Simple Mock for testing
  class TestFileIO extends FileIO {
    var saved: Option[StockMemento]          = None
    override def save(m: StockMemento): Unit = saved = Some(m)
    override def load: StockMemento          = saved.getOrElse(StockMemento(Nil))
  }

  "Commands" should {
    "have correct descriptions" in {
      val repo = new StockRepository(Nil)
      val ctrl = new StockController(repo, new TestFileIO)

      new AddStockCommand(ctrl).description shouldBe "Add stock"
      new ShowAllCommand(ctrl).description shouldBe "Show all stocks"
      new FilterStockCommand(ctrl).description shouldBe "Filter by price [min-max]"
      new DeleteStockCommand(ctrl).description shouldBe "Delete stock by ticker"
      new ChangeStrategyCommand(ctrl).description shouldBe "Change Sort Strategy"
      new UndoCommand(ctrl).description shouldBe "Undo last add/delete"
      // new SaveCommand(ctrl).description shouldBe "Save data to file"
    }
  }
}
