package de.htwg.DurakApp.model.state

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.ModelInterface.*
import de.htwg.DurakApp.model.ModelInterface.StateInterface.*

class AskPlayAgainPhaseSpec extends AnyWordSpec with Matchers {
  "An AskPlayAgainPhase" should {
    "not modify the game state, including lastEvent" in {
      val player1 = Player("P1", List.empty, isDone = true)
      val player2 = Player("P2", List.empty, isDone = true)
      val initialGameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = Card(Suit.Diamonds, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = AskPlayAgainPhase
      )

      val resultState = AskPlayAgainPhase.handle(initialGameState)

      resultState.lastEvent.shouldBe(None)
      resultState.gamePhase.shouldBe(AskPlayAgainPhase)
    }
    
    "have a string representation" in {
      AskPlayAgainPhase.toString should not be empty
    }
  }
}
