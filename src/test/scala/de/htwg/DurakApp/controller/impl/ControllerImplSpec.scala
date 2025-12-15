package de.htwg.DurakApp.controller.impl

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.controller.ControllerInterface._
import de.htwg.DurakApp.model.ModelInterface._
import de.htwg.DurakApp.model.ModelInterface.StateInterface._
import de.htwg.DurakApp.util.UndoRedoManager

class ControllerImplSpec extends AnyWordSpec with Matchers {
  "ControllerImpl" should {
    "be created through Controller factory" in {
      val gameState = GameStateBuilder().withGamePhase(SetupPhase).build()
      val controller = Controller(gameState, UndoRedoManager())
      
      controller.gameState shouldBe gameState
    }
  }
}
