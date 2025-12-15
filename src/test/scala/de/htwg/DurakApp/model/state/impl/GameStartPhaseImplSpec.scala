package de.htwg.DurakApp.model.state.impl

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.ModelInterface.*
import de.htwg.DurakApp.model.ModelInterface.StateInterface.*

class GameStartPhaseImplSpec extends AnyWordSpec with Matchers {
  "A GameStartPhase" should {
    "set GameSetupComplete event when handling state" in {
      val initialGameState = GameState(
        players = List(Player("P1"), Player("P2")),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = Card(Suit.Hearts, Rank.Six),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = GameStartPhase
      )

      val resultState = GameStartPhase.handle(initialGameState)

      resultState.lastEvent shouldBe Some(GameEvent.GameSetupComplete)
    }

    "have a string representation" in {
      GameStartPhase.toString should not be empty
    }
  }
}
