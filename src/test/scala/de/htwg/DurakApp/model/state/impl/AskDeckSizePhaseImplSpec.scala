package de.htwg.DurakApp.model.state.impl

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.ModelInterface.*
import de.htwg.DurakApp.model.ModelInterface.StateInterface.*

class AskDeckSizePhaseImplSpec extends AnyWordSpec with Matchers {
  "An AskDeckSizePhase" should {
    "handle the game state by returning it unchanged" in {
      val initialGameState = GameState(
        players = List.empty,
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = Card(Suit.Hearts, Rank.Six),
        attackerIndex = 0,
        defenderIndex = 0,
        gamePhase = AskDeckSizePhase
      )
      val resultState = AskDeckSizePhase.handle(initialGameState)
      resultState shouldBe initialGameState
    }
    
    "have a string representation" in {
      AskDeckSizePhase.toString should not be empty
    }
  }
}
