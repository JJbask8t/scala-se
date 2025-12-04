package stockpilot.controller

package stockpilot.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class ObservableSpec extends AnyWordSpec with Matchers {

  // class for testing the Observable trait
  class TestSubject extends Observable {
    def trigger(): Unit = notifyObservers()
    def getObserversCount: Int = {
      // reflection or just a behavior test,
      // but for coverage, it is enough to check that remove works
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
      callCount shouldBe 1 // It should not increase, as it has been deleted.
    }
  }
}
