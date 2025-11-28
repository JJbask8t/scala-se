package stockpilot.controller

package stockpilot.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class ObservableSpec extends AnyWordSpec with Matchers {

  // Простой класс для тестирования трейта Observable
  class TestSubject extends Observable {
    def trigger(): Unit = notifyObservers()
    def getObserversCount: Int = {
      // reflection или просто тест поведения,
      // но для coverage достаточно проверить, что remove работает
      0
    }
  }

  "Observable" should {
    "allow adding and removing observers" in {
      val subject = new TestSubject
      var callCount = 0
      val obs = new Observer {
        def update(): Unit = callCount += 1
      }

      subject.addObserver(obs)
      subject.trigger()
      callCount shouldBe 1

      subject.removeObserver(obs)
      subject.trigger()
      callCount shouldBe 1 // Не должно увеличиться, так как удалили
    }
  }
}
