package de.htwg.DurakApp.util

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class ObserverSpec extends AnyWordSpec with Matchers {

  "An Observable" should {
    "add and notify an observer" in {
      val observable = new Observable {}
      val observer = new TestObserver
      observable.add(observer)
      observer.wasUpdated.shouldBe(false)
      observable.notifyObservers
      observer.wasUpdated.shouldBe(true)
    }

    "remove an observer" in {
      val observable = new Observable {}
      val observer1 = new TestObserver
      val observer2 = new TestObserver
      observable.add(observer1)
      observable.add(observer2)
      
      observable.notifyObservers
      observer1.wasUpdated.shouldBe(true)
      observer2.wasUpdated.shouldBe(true)
      
      observer1.reset()
      observer2.reset()
      
      observable.remove(observer1)
      observable.notifyObservers
      
      observer1.wasUpdated.shouldBe(false)
      observer2.wasUpdated.shouldBe(true)
    }

    "not fail when notifying with no observers" in {
      val observable = new Observable {}
      noException should be thrownBy observable.notifyObservers
    }
  }
}

class TestObserver extends Observer {
  private var _updateCount: Int = 0
  def update: Unit = _updateCount += 1
  def wasUpdated: Boolean = _updateCount > 0
  def reset(): Unit = _updateCount = 0
}
