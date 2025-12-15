package de.htwg.DurakApp.controller.command.impl

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.ModelInterface.*
import de.htwg.DurakApp.model.ModelInterface.StateInterface.*
import de.htwg.DurakApp.controller.ControllerInterface.*

class PhaseChangeCommandSpec extends AnyWordSpec with Matchers {
  "A PhaseChangeCommand" should {
    val dummyPlayer = Player("TestPlayer", List.empty)
    val dummyGameState = GameState(
      players = List(dummyPlayer),
      deck = List.empty,
      table = Map.empty,
      discardPile = List.empty,
      trumpCard = Card(Suit.Clubs, Rank.Ace),
      attackerIndex = 0,
      defenderIndex = 0,
      gamePhase = SetupPhase
    )

    "execute by returning the same GameState instance" in {
      val command = PhaseChangeCommand()
      val resultState = command.execute(dummyGameState)
      resultState should be theSameInstanceAs dummyGameState
      resultState.lastEvent shouldBe None
    }

    "undo by returning the previous GameState instance" in {
      val command = PhaseChangeCommand()
      val newGameState = dummyGameState.copy(gamePhase = AttackPhase)
      val undoneState = command.undo(newGameState, dummyGameState)
      undoneState should be theSameInstanceAs dummyGameState
    }
  }
}
