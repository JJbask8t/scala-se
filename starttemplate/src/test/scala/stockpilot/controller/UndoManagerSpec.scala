package stockpilot.controller.stockpilot.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import stockpilot.controller.{UndoManager, UndoableCommand}

class UndoManagerSpec extends AnyWordSpec with Matchers {

  // Fake command for testing
  class TestCommand(var state: Int) extends UndoableCommand {
    def execute(): Unit = state += 1
    def undo(): Unit    = state -= 1
  }

  "UndoManager" should {
    "execute commands and store them in history" in {
      val manager = new UndoManager
      val cmd     = new TestCommand(0)

      manager.execute(cmd)
      cmd.state shouldBe 1

      manager.undo() shouldBe true
      cmd.state shouldBe 0
    }

    "handle empty history gracefully" in {
      val manager = new UndoManager
      manager.undo() shouldBe false
    }

    "handle multiple undo steps" in {
      val manager = new UndoManager
      val cmd1    = new TestCommand(0)
      val cmd2    = new TestCommand(10)

      manager.execute(cmd1) // cmd1=1
      manager.execute(cmd2) // cmd2=11

      manager.undo() // undo cmd2
      cmd2.state shouldBe 10
      cmd1.state shouldBe 1

      manager.undo() // undo cmd1
      cmd1.state shouldBe 0
    }
  }
}
