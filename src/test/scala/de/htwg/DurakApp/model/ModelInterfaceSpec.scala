package de.htwg.DurakApp.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.ModelInterface._

class ModelInterfaceSpec extends AnyWordSpec with Matchers {
  "ModelInterface" should {
    "provide access to Card factory" in {
      val card = Card(Suit.Hearts, Rank.Ace)
      card shouldBe a[Card]
    }
    
    "provide access to Player factory" in {
      val player = Player("Alice")
      player shouldBe a[Player]
    }
    
    "provide access to GameStateBuilder factory" in {
      val builder = GameStateBuilder()
      val gameState = builder.build()
      gameState shouldBe a[GameState]
    }
  }
}
