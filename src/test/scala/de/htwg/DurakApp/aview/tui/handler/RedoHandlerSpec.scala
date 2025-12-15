package de.htwg.DurakApp.aview.tui.handler

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.controller.ControllerInterface._
import de.htwg.DurakApp.model.ModelInterface._
import de.htwg.DurakApp.model.ModelInterface.StateInterface._
import de.htwg.DurakApp.util.UndoRedoManager

class RedoHandlerSpec extends AnyWordSpec with Matchers {
  "RedoHandler" should {
    "handle redo input" in {
      val controller = Controller(
        GameStateBuilder().withGamePhase(SetupPhase).build(),
        UndoRedoManager()
      )
      val handler = new RedoHandler(controller, None)

      val action = handler.handleRequest("redo", controller.gameState)
      action shouldBe RedoAction
    }
  }
}
