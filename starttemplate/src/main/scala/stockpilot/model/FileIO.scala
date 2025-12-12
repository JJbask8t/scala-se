package stockpilot.model

/** Interface for File Input/Output (Caretaker in the Memento Pattern)
  *
  * Strategy Pattern (switching XML <-> JSON implementations
  */
trait FileIO {
  def load: StockMemento
  def save(memento: StockMemento): Unit
}
