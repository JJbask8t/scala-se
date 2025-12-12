package stockpilot.model

/** Memento Pattern - Stores the internal state of the Originator (StockRepository) HERE: the state
  * is just the list of stocks
  */
case class StockMemento(stocks: List[Stock])
