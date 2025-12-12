package stockpilot.model

import java.io.{File, PrintWriter}
import scala.io.Source
import play.api.libs.json._

/** Implementation of FileIO using JSON format (Play-JSON).
  */
class FileIOJson extends FileIO {

  private val fileName = "stock_data.json"

  // Define implicit converters for JSON serialization/deserialization
  // This magic allows Play-JSON to automatically convert Stock <-> JSON
  implicit val stockFormat: OFormat[Stock] = Json.format[Stock]

  // We also need a format for the list wrapper or the Memento
  implicit val mementoFormat: OFormat[StockMemento] = Json.format[StockMemento]

  override def save(memento: StockMemento): Unit = {
    val json       = Json.toJson(memento)
    val prettyJson = Json.prettyPrint(json)

    val pw = new PrintWriter(new File(fileName))
    try pw.write(prettyJson)
    finally pw.close()
  }

  override def load: StockMemento = {
    val file = new File(fileName)
    if (file.exists()) {
      val source = Source.fromFile(file)
      try {
        val jsonString = source.getLines().mkString
        val json       = Json.parse(jsonString)
        // Convert JSON back to Scala object
        json.as[StockMemento]
      } finally source.close()
    } else { StockMemento(Nil) }
  }
}
