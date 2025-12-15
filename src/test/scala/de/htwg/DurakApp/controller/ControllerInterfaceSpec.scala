package de.htwg.DurakApp.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.controller.ControllerInterface._
import de.htwg.DurakApp.model.ModelInterface._
import de.htwg.DurakApp.model.ModelInterface.StateInterface._
import de.htwg.DurakApp.util.UndoRedoManager

class ControllerInterfaceSpec extends AnyWordSpec with Matchers {
  "ControllerInterface" should {
    "provide access to Controller factory" in {
      val gameState = GameStateBuilder().withGamePhase(SetupPhase).build()
      val controller = Controller(gameState, UndoRedoManager())
      
      controller shouldBe a[Controller]
    }
    
    "provide access to PlayerAction types" in {
      val playAction: PlayerAction = PlayCardAction(Card(Suit.Hearts, Rank.Ace))
      val passAction: PlayerAction = PassAction
      val takeAction: PlayerAction = TakeCardsAction
      
      playAction shouldBe a[PlayCardAction]
      passAction shouldBe PassAction
      takeAction shouldBe TakeCardsAction
    }
  }
}
