package de.htwg.DurakApp.model.impl

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.ModelInterface._
import de.htwg.DurakApp.model.ModelInterface.StateInterface._

class GameStateImplSpec extends AnyWordSpec with Matchers {
  "GameStateImpl" should {
    "be created through GameState factory" in {
      val card = Card(Suit.Hearts, Rank.Ace)
      val player = Player("Alice", List(card))
      val gameState = GameState(
        players = List(player),
        deck = List(),
        table = Map(),
        discardPile = List(),
        trumpCard = card,
        attackerIndex = 0,
        defenderIndex = 0,
        gamePhase = AttackPhase
      )
      
      gameState.players should have size 1
      gameState.deck shouldBe empty
    }
  }
}
