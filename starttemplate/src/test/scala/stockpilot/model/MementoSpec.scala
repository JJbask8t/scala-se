package stockpilot.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class MementoSpec extends AnyWordSpec with Matchers {

  "StockRepository (as Originator)" should {
    "create and restore state using Memento" in {
      val s1 = Stock("A", 1.0, 1.0, 1.0)
      val s2 = Stock("B", 2.0, 2.0, 2.0)

      val repo = new StockRepository(List(s1))

      // 1. Initial State
      repo.all.length shouldBe 1
      repo.exists("A") shouldBe true

      // 2. Create Snapshot (Memento)
      val memento = repo.createMemento()
      memento.stocks should contain(s1)

      // 3. Change State
      repo.add(s2)
      repo.exists("B") shouldBe true
      repo.all.length shouldBe 2

      // 4. Restore State
      repo.setMemento(memento)
      repo.all.length shouldBe 1
      repo.exists("B") shouldBe false
      repo.exists("A") shouldBe true
    }
  }
}
