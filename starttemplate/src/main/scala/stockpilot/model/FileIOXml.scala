package stockpilot.model

import java.io.{File, PrintWriter}
import scala.xml.{Node, PrettyPrinter, XML}

/** Implementation of FileIO using XML format.
  */
class FileIOXml extends FileIO {

  private val fileName = "stock_data.xml"

  override def save(memento: StockMemento): Unit = {
    // Convert each stock to an XML node
    val stocksXml = memento.stocks.map { stock =>
      <stock>
        <ticker>{stock.ticker}</ticker>
        <pe>{stock.pe}</pe>
        <eps>{stock.eps}</eps>
        <price>{stock.price}</price>
      </stock>
    }

    // Wrap in a root element
    val rootXml = <stocks>{stocksXml}</stocks>

    // Save to file with pretty formatting
    val printer   = new PrettyPrinter(120, 4)
    val xmlString = printer.format(rootXml)

    val pw = new PrintWriter(new File(fileName))
    try pw.write(xmlString)
    finally pw.close()
  }

  override def load: StockMemento = {
    val file = new File(fileName)
    if (file.exists()) {
      val xml    = XML.loadFile(file)
      // Map XML nodes back to Stock objects
      val stocks = (xml \ "stock").map { node =>
        Stock(
          ticker = (node \ "ticker").text.trim,
          pe = (node \ "pe").text.toDouble,
          eps = (node \ "eps").text.toDouble,
          price = (node \ "price").text.toDouble
        )
      }.toList
      StockMemento(stocks)
    } else {
      // Return empty if file not found
      StockMemento(Nil)
    }
  }
}
