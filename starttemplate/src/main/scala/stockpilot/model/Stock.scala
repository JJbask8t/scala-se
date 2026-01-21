package stockpilot.model

import java.awt.Color

/** Represents a Stock with portfolio data and analytic logic.
  * @param quantity
  *   0.0 means "Watchlist", > 0.0 means "Portfolio".
  */
case class Stock(
    ticker: String,
    pe: Double,
    eps: Double,
    price: Double,
    quantity: Double = 0.0 // Default value ensures backward compatibility
) {

  // --- Business Logic ---

  /** Calculates Fair Value based on simplified Graham formula (EPS * 15). */
  def fairValue: Double = eps * 15.0

  /** Calculates the Verdict based on current Price vs Fair Value. */
  def verdict: Verdict =
    if (price < fairValue) Verdict.Buy
    else if (price > fairValue * 1.5) Verdict.Sell
    else Verdict.Hold

  /** Total value of the position. */
  def totalValue: Double = price * quantity

  /** Formatted lines for TUI table (Updated columns) */
  def toLines(lineIdx: Int): String = lineIdx match {
    case 0 => s" ${ticker.padTo(10, ' ')} "
    case 1 => s" Price: ${f"$price%6.2f"} "
    case 2 => s" Qty: ${f"$quantity%6.1f"} "
    case 3 => s" ${verdict.toString.padTo(10, ' ')} " // Show Verdict
    case _ => ""
  }
}

/** Enum-like object for Verdicts to handle logic and colors centrally. */
sealed trait Verdict {
  def color: Color
  override def toString: String
}

object Verdict {
  case object Buy  extends Verdict {
    override def toString = "BUY"
    val color             = new Color(0, 150, 0) // Dark Green
  }
  case object Sell extends Verdict {
    override def toString = "SELL"
    val color             = new Color(200, 0, 0) // Dark Red
  }
  case object Hold extends Verdict {
    override def toString = "HOLD"
    val color             = new Color(200, 160, 0) // Dark Yellow/Orange
  }
}
