package de.htwg.DurakApp.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.controller.ControllerInterface._
import de.htwg.DurakApp.model.ModelInterface._
import de.htwg.DurakApp.model.ModelInterface.StateInterface._
import de.htwg.DurakApp.util.UndoRedoManager

class ControllerInterfaceSpec extends AnyWordSpec with Matchers {
  "ControllerInterface Controller factory" should {
    "create Controller with initial GameState" in {
      val gameState = GameStateBuilder()
        .withGamePhase(SetupPhase)
        .build()
      val controller = Controller(gameState, UndoRedoManager())

      controller.gameState shouldBe gameState
    }

    "create Controller that can process actions" in {
      val gameState = GameStateBuilder()
        .withGamePhase(SetupPhase)
        .build()
      val controller = Controller(gameState, UndoRedoManager())

      noException should be thrownBy controller.processPlayerAction(
        SetPlayerCountAction(2)
      )
    }

    "create Controller with UndoRedoManager" in {
      val gameState = GameStateBuilder().withGamePhase(SetupPhase).build()
      val undoManager = UndoRedoManager()
      val controller = Controller(gameState, undoManager)

      controller.undo() shouldBe None
      controller.redo() shouldBe None
    }
  }

  "ControllerInterface PlayerAction types" should {
    "support PlayCardAction with card parameter" in {
      val card = Card(Suit.Hearts, Rank.Ace)
      val action = PlayCardAction(card)

      action shouldBe a[PlayCardAction]
      action shouldBe a[PlayerAction]
    }

    "support PassAction as singleton" in {
      val action: PlayerAction = PassAction

      action shouldBe PassAction
    }

    "support TakeCardsAction as singleton" in {
      val action: PlayerAction = TakeCardsAction

      action shouldBe TakeCardsAction
    }

    "support SetPlayerCountAction with count parameter" in {
      val action = SetPlayerCountAction(3)

      action shouldBe a[SetPlayerCountAction]
      action shouldBe a[PlayerAction]
    }

    "support AddPlayerNameAction with name parameter" in {
      val action = AddPlayerNameAction("Alice")

      action shouldBe a[AddPlayerNameAction]
      action shouldBe a[PlayerAction]
    }

    "support UndoAction and RedoAction" in {
      UndoAction shouldBe a[PlayerAction]
      RedoAction shouldBe a[PlayerAction]
    }
  }
}
