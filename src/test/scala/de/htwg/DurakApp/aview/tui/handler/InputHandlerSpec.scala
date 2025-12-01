package de.htwg.DurakApp.aview.tui.handler

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class InputHandlerSpec extends AnyWordSpec with Matchers {

  "The InputHandler trait" should {
    "set the next handler" in {
      val handler2 = new TakeCardsHandler()
      val handler1 = new PassHandler(Some(handler2))
      handler1.next should be(Some(handler2))
    }
  }
}
