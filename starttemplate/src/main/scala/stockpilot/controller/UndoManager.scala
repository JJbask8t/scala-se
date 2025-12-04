package stockpilot.controller

import stockpilot.model.{Stock, IStockRepository}

// Interface for commands that can be executed and undone
trait UndoableCommand {
  def execute(): Unit
  def undo(): Unit // ! new
}

//! The Invoker that manages the history stack
class UndoManager {
  private var history: List[UndoableCommand] = Nil

  def execute(cmd: UndoableCommand): Unit = {
    cmd.execute()
    history = cmd :: history // Push to stack
  }

  def undo(): Boolean = history match {
    case head :: tail =>
      head.undo()
      history = tail // Pop from stack
      true
    case Nil          => false // Nothing to undo
  }
}

// --- Concrete Logic Commands ---

//! Action to add a stock. Undo deletes it.
class AddStockAction(stock: Stock, repo: IStockRepository) extends UndoableCommand {
  override def execute(): Unit = repo.add(stock)
  override def undo(): Unit    = repo.delete(stock.ticker)
}

// Action to delete a stock. Undo adds it back.
//! have to store the deleted stock to be able to restore it!
class DeleteStockAction(stock: Stock, repo: IStockRepository) extends UndoableCommand {
  override def execute(): Unit = repo.delete(stock.ticker)
  override def undo(): Unit    = repo.add(stock)
}
