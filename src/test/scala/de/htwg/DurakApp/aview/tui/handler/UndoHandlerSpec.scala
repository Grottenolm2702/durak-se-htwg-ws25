package de.htwg.DurakApp.aview.tui.handler

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.controller.ControllerInterface._
import de.htwg.DurakApp.model.ModelInterface._
import de.htwg.DurakApp.model.ModelInterface.StateInterface._
import de.htwg.DurakApp.util.UndoRedoManager

class UndoHandlerSpec extends AnyWordSpec with Matchers {
  "UndoHandler" should {
    "handle undo input" in {
      val controller = Controller(
        GameStateBuilder().withGamePhase(SetupPhase).build(),
        UndoRedoManager()
      )
      val handler = new UndoHandler(controller, None)
      
      val action = handler.handleRequest("undo", controller.gameState)
      action shouldBe UndoAction
    }
  }
}
