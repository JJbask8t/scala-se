package stockpilot.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class UndoManagerSpec extends AnyWordSpec with Matchers {

  // FIXED: Renamed methods to 'execute' and 'undo' to match UndoableCommand trait
  class TestCommand extends UndoableCommand {
    var executed = false
    var undone   = false

    override def execute(): Unit = executed = true

    override def undo(): Unit = undone = true
  }

  "UndoManager" should {
    "execute command and add to history" in {
      val manager = new UndoManager
      val cmd     = new TestCommand

      manager.execute(cmd)

      cmd.executed shouldBe true
    }

    "undo last command" in {
      val manager = new UndoManager
      val cmd     = new TestCommand
      manager.execute(cmd)

      val res = manager.undo()
      res shouldBe true
      cmd.undone shouldBe true
    }

    "handle undo on empty history" in {
      val manager = new UndoManager
      // Stack is empty
      manager.undo() shouldBe false
    }

  }

}
