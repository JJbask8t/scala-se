package stockpilot.view

import scala.io.StdIn.readLine
import stockpilot.controller.{IStockController, Observer} // Добавлен импорт Observer
import stockpilot.model.Stock

class CLIView(controller: IStockController) extends Observer {

  // Команды теперь импортируются автоматически из пакета stockpilot.view (файл Commands.scala)
  private val commands: Map[String, Command] = Map(
    "1" -> new AddStockCommand(controller),
    "2" -> new ShowAllCommand(controller),
    "3" -> new FilterStockCommand(controller),
    "4" -> new DeleteStockCommand(controller),
    "5" -> new ChangeStrategyCommand(controller),
    "6" -> new GenerateReportCommand(controller),
    "7" -> new UndoCommand(controller)
  )

  def run(): Unit = {
    var input = ""
    while (input != "q") {
      printMenu()
      input = scala.io.StdIn.readLine("Select option: ")
      commands.get(input) match {
        case Some(cmd)            => cmd.execute()
        case None if input != "q" => println("Unknown command.")
        case _                    => // Quit
      }
    }
  }

  /*private def printMenu(): Unit = {
    val eol        = sys.props("line.separator")
    println(eol + "--- Commands ---")
    // Динамическая печать меню на основе карты команд
    val sortedKeys = commands.keys.toSeq.sortBy(_.toInt)
    sortedKeys.foreach(key => println(s"$key. ${commands(key).description}"))
    println("q. Quit")
    println(eol)
  }*/

  private def printMenu(): Unit = {
    println("\n----  StockPilot  Menu  ----")
    // Dynamically print descriptions
    commands.toList.sortBy(_._1).foreach { case (key, cmd) => println(s"$key. ${cmd.description}") }
    println("q. Quit")
    println("-" * 28)
  }

  override def update(): Unit = println("\n[Update received from Controller]")
}
