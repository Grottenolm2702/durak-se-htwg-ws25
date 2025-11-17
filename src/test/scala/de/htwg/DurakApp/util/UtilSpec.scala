package de.htwg.DurakApp.util

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class UtilSpec extends AnyWordSpec with Matchers {

  "An Observable" should {
    "add and notify an observer" in {
      val observable = new Observable {}
      val observer = new TestObserver
      observable.add(observer)
      observer.updated shouldBe false
      observable.notifyObservers
      observer.updated shouldBe true
    }

    "remove an observer" in {
      val observable = new Observable {}
      val observer1 = new TestObserver
      val observer2 = new TestObserver
      observable.add(observer1)
      observable.add(observer2)
      
      observable.notifyObservers
      observer1.updated shouldBe true
      observer2.updated shouldBe true
      
      // Reset observers
      observer1.reset()
      observer2.reset()
      
      observable.remove(observer1)
      observable.notifyObservers
      
      observer1.updated shouldBe false
      observer2.updated shouldBe true
    }

    "not fail when notifying with no observers" in {
      val observable = new Observable {}
      noException should be thrownBy observable.notifyObservers
    }
  }
}

class TestObserver extends Observer {
  var updated = false
  def update: Unit = updated = true
  def reset(): Unit = updated = false
}
