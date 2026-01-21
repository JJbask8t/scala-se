package stockpilot.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import _root_.stockpilot.model._

class ComponentIsolationSpec extends AnyWordSpec with Matchers {

  /** * STUB: A fake repository that doesn't use a Map, just returns hardcoded values for testing.
    */
  class RepositoryStub extends IStockRepository {
    override def all: List[Stock] = List(Stock("STUB", 1, 1, 1))
    override def exists(t: String): Boolean = t == "STUB"
    override def get(t: String): Option[Stock] = if (t == "STUB") Some(all.head) else None
    override def add(s: Stock): Boolean = true
    override def delete(t: String): Boolean = true
    override def createMemento(): StockMemento = StockMemento(Nil)
    override def setMemento(m: StockMemento): Unit = {}
    override def iterator: Iterator[Stock] = all.iterator
  }

  class FileIOStub extends FileIO {
    override def save(m: StockMemento): Unit = {}
    override def load: StockMemento = StockMemento(Nil)
  }

  "A StockController (as a Component)" should {
    "be testable in isolation using a Repository Stub" in {
      val stub = new RepositoryStub()
      val controller = new StockController(stub, new FileIOStub())

      controller.allStocks.head.ticker shouldBe "STUB"
      controller.exists("STUB") shouldBe true
    }
  }
}
