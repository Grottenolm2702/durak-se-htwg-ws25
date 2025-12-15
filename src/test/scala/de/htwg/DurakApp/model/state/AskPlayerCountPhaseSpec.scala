package de.htwg.DurakApp.model.state

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.ModelInterface.*
import de.htwg.DurakApp.model.ModelInterface.StateInterface.*

class AskPlayerCountPhaseSpec extends AnyWordSpec with Matchers {
  "An AskPlayerCountPhase" should {
    "handle the game state by returning it unchanged" in {
      val initialGameState = GameState(
        players = List.empty,
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = Card(Suit.Hearts, Rank.Six),
        attackerIndex = 0,
        defenderIndex = 0,
        gamePhase = AskPlayerCountPhase
      )
      val resultState = AskPlayerCountPhase.handle(initialGameState)
      resultState shouldBe initialGameState
    }
    
    "have a string representation" in {
      AskPlayerCountPhase.toString should not be empty
    }
  }
}
