package de.htwg.DurakApp.aview.tui.handler

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.ModelInterface.*
import de.htwg.DurakApp.model.ModelInterface.StateInterface.*
import de.htwg.DurakApp.controller.ControllerInterface.*
import de.htwg.DurakApp.util.UndoRedoManager

class InputHandlerSpec extends AnyWordSpec with Matchers {

  "The InputHandler trait" should {
    "set the next handler" in {
      val handler2 = new TakeCardsHandler()
      val handler1 = new PassHandler(Some(handler2))
      handler1.next should be(Some(handler2))
    }
  }

  "RedoHandler" should {
    "have next set to None by default" in {
      val controller = Controller(
        GameStateBuilder().build(),
        UndoRedoManager()
      )
      val redoHandler = new RedoHandler(controller)
      redoHandler.next should be(None)
    }
  }

  "UndoHandler" should {
    "have next set to None by default" in {
      val controller = Controller(
        GameStateBuilder().build(),
        UndoRedoManager()
      )
      val undoHandler = new UndoHandler(controller)
      undoHandler.next should be(None)
    }
  }
}
