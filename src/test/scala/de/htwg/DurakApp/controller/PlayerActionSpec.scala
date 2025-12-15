package de.htwg.DurakApp.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.controller.ControllerInterface._
import de.htwg.DurakApp.model.ModelInterface._

class PlayerActionSpec extends AnyWordSpec with Matchers {
  "PlayerAction" should {
    "have PlayCardAction subtype" in {
      val action: PlayerAction = PlayCardAction(Card(Suit.Hearts, Rank.Ace))
      action shouldBe a[PlayCardAction]
    }
    
    "have PassAction subtype" in {
      val action: PlayerAction = PassAction
      action shouldBe PassAction
    }
    
    "have TakeCardsAction subtype" in {
      val action: PlayerAction = TakeCardsAction
      action shouldBe TakeCardsAction
    }
  }
}
