package stockpilot.model

// Pattern Strategy

// Defining the strategy interface
trait StockSortStrategy {
  def sort(stocks: List[Stock]): List[Stock]
}

// Specific strategy: Sort by Ticker (A-Z)
object SortByTicker extends StockSortStrategy {
  override def sort(stocks: List[Stock]): List[Stock] =
    stocks.sortBy(_.ticker)
}

// Specific strategy: Sort by price (from low to high)
object SortByPriceAsc extends StockSortStrategy {
  override def sort(stocks: List[Stock]): List[Stock] =
    stocks.sortBy(_.price)
}

// Specific strategy: Sort by price (from expensive to cheap)
object SortByPriceDesc extends StockSortStrategy {
  override def sort(stocks: List[Stock]): List[Stock] =
    stocks.sortBy(_.price)(Ordering[Double].reverse)
}
