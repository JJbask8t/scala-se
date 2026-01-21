package stockpilot.view

import scala.swing._
import scala.swing.event._
import stockpilot.controller.{StockController, Observer, IStockController}
import stockpilot.model.{Stock, SortByPriceAsc, SortByPriceDesc, SortByTicker}
import java.awt.Dimension
import java.awt.Color
import javax.swing.WindowConstants

/** Full-featured Graphical User Interface. Mirrors all TUI functionality: Add, Delete, Sort,
  * Filter, Undo.
  */
class GUIView(controller: IStockController) extends MainFrame with Observer {

  title = "StockPilot"
  minimumSize = new Dimension(800, 600)

  // Handle window closing gracefully (Auto-save & Exit)
  peer.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE)
  override def closeOperation(): Unit = {
    println("GUI Closing... saving data.")
    controller.save()
    sys.exit(0)
  }

  // --- Components ---

  // 1. Table
  var tableData    = Array.ofDim[Any](0, 4)
  val tableHeaders = Seq("Ticker", "P/E", "EPS", "Price")
  val stockTable   = new Table(tableData, tableHeaders) {
    showGrid = true
    gridColor = Color.LIGHT_GRAY
    rowHeight = 25
  }

  // 2. Sorting & Filtering (Top Panel)
  val comboSort      = new ComboBox(Seq("Ticker (A-Z)", "Price (Asc)", "Price (Desc)"))
  val txtFilterMin   = new TextField { columns = 5; text = "0" }
  val txtFilterMax   = new TextField { columns = 5; text = "1000" }
  val btnFilter      = new Button("Filter")
  val btnResetFilter = new Button("Reset")

  // 3. Adding Stocks (Bottom Panel)
  val txtTicker = new TextField { columns = 5; text = "TICKER" }
  val txtPe     = new TextField { columns = 5; text = "0.0" }
  val txtEps    = new TextField { columns = 5; text = "0.0" }
  val txtPrice  = new TextField { columns = 5; text = "0.0" }
  val btnAdd    = new Button("Add Stock")

  // 4. Actions
  val btnDelete = new Button("Delete Selected")
  val btnUndo   = new Button("Undo")

  // --- Layout ---

  // Top Bar: Sort | Filter
  val topPanel = new FlowPanel {
    contents += new Label("Sort by:")
    contents += comboSort
    contents += new Label("  |  Filter Price:")
    contents += txtFilterMin
    contents += new Label("-")
    contents += txtFilterMax
    contents += btnFilter
    contents += btnResetFilter
  }

  // Bottom Bar: Inputs | Add | Delete | Undo
  val bottomPanel = new FlowPanel {
    border = Swing.TitledBorder(Swing.EtchedBorder, "Manage Stocks")
    contents += new Label("Ticker:")
    contents += txtTicker
    contents += new Label("P/E:")
    contents += txtPe
    contents += new Label("EPS:")
    contents += txtEps
    contents += new Label("Price:")
    contents += txtPrice
    contents += btnAdd
    contents += new Label("   ") // Spacer
    contents += btnDelete
    contents += btnUndo
  }

  contents = new BorderPanel {
    layout(topPanel) = BorderPanel.Position.North
    layout(new ScrollPane(stockTable)) = BorderPanel.Position.Center
    layout(bottomPanel) = BorderPanel.Position.South
  }

  // --- Event Handling ---

  listenTo(btnAdd, btnDelete, btnUndo, btnFilter, btnResetFilter, comboSort.selection)

  reactions += {
    // 1. ADD STOCK
    case ButtonClicked(`btnAdd`)    =>
      controller.addStockFromInput(txtTicker.text, txtPe.text, txtEps.text, txtPrice.text)
      // Clear inputs slightly for UX
      txtTicker.text = ""

    // 2. DELETE STOCK
    case ButtonClicked(`btnDelete`) =>
      val row = stockTable.selection.rows.leadIndex
      if (row >= 0) {
        val ticker = stockTable.model.getValueAt(row, 0).toString
        controller.deleteStock(ticker)
      }

    // 3. UNDO
    case ButtonClicked(`btnUndo`)   => controller.undoLastAction()

    // 4. SORTING
    case SelectionChanged(`comboSort`) => comboSort.selection.index match {
        case 0 => controller.setSortStrategy(SortByTicker)
        case 1 => controller.setSortStrategy(SortByPriceAsc)
        case 2 => controller.setSortStrategy(SortByPriceDesc)
      }

    // 5. FILTERING
    case ButtonClicked(`btnFilter`)    =>
      val minOpt = scala.util.Try(txtFilterMin.text.toDouble).toOption
      val maxOpt = scala.util.Try(txtFilterMax.text.toDouble).toOption
      (minOpt, maxOpt) match {
        case (Some(min), Some(max)) =>
          val filtered = controller.filterByPrice(min, max)
          updateTable(filtered) // Manually update table with filtered list
        case _                      => Dialog.showMessage(this, "Invalid numbers for filter", "Error")
      }

    case ButtonClicked(`btnResetFilter`) => update() // Re-fetch all data from controller
  }

  // --- Observer Implementation ---

  override def update(): Unit =
    // When controller notifies change, we show ALL stocks by default
    // (Filtering is a temporary view state)
    updateTable(controller.allStocks)

  private def updateTable(stocks: List[Stock]): Unit = {
    val newData = stocks.map(s => Array[Any](s.ticker, s.pe, s.eps, s.price)).toArray

    stockTable.model = new javax.swing.table.AbstractTableModel {
      override def getRowCount: Int                   = newData.length
      override def getColumnCount: Int                = tableHeaders.length
      override def getValueAt(r: Int, c: Int): AnyRef = newData(r)(c).asInstanceOf[AnyRef]
      override def getColumnName(c: Int): String      = tableHeaders(c)
    }
  }

  // Initial draw
  visible = true
  update()
}
