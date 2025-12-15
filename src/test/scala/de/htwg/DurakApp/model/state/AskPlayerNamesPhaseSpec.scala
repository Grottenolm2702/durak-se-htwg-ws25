package de.htwg.DurakApp.model.state

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.ModelInterface.*

class AskPlayerNamesPhaseSpec extends AnyWordSpec with Matchers {
  "An AskPlayerNamesPhase" should {
    "handle the game state by returning it unchanged" in {
      val initialGameState = GameState(
        players = List.empty,
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = Card(Suit.Hearts, Rank.Six),
        attackerIndex = 0,
        defenderIndex = 0,
        gamePhase = AskPlayerNamesPhase
      )
      val resultState = AskPlayerNamesPhase.handle(initialGameState)
      resultState shouldBe initialGameState
    }
  }
}
