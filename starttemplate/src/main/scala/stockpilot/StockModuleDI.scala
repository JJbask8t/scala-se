package stockpilot

import com.google.inject.{AbstractModule, Provides, Singleton}
import stockpilot.controller.{IStockController, StockController}
import stockpilot.model._

class StockModuleDI extends AbstractModule {

  override def configure(): Unit = {
    // Bind Controller Interface to Implementation
    bind(classOf[IStockController]).to(classOf[StockController])

    // ! Switch between FileIOJson or FileIOXml here!
    bind(classOf[FileIO]).to(classOf[FileIOJson])
    // bind(classOf[FileIO]).to(classOf[FileIOXml])
  }

  // Guice calls this when 'IStockRepository' is requested.
  @Provides @Singleton
  def provideRepository(): IStockRepository = {
    val baseRepo = new StockRepository()
    new LoggingRepository(baseRepo) // Manual Decorator wrapping
  }
}
