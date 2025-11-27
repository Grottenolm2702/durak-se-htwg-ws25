package de.htwg.DurakApp.controller.command

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model._
import de.htwg.DurakApp.model.state._

class PassCommandSpec extends AnyWordSpec with Matchers {
  "A PassCommand" should {
    val player1 = Player("P1", List(Card(Suit.Clubs, Rank.Six)))
    val player2 = Player("P2", List(Card(Suit.Hearts, Rank.Ace)))
    val initialGameState = GameState(
      players = List(player1, player2),
      deck = List.empty,
      table = Map(Card(Suit.Spades, Rank.Seven) -> None),
      discardPile = List.empty,
      trumpCard = Card(Suit.Diamonds, Rank.Ace),
      attackerIndex = 0,
      defenderIndex = 1,
      gamePhase = AttackPhase
    )

    "execute correctly when attacker passes" in {
      val command = PassCommand()
      val resultState = command.execute(initialGameState)

      resultState.gamePhase shouldBe AttackPhase
      resultState.roundWinner.isDefined shouldBe false
      resultState.lastEvent.get shouldBe GameEvent.RoundEnd(cleared = true)
    }

    "not execute when passing is invalid (e.g., empty table)" in {
      val invalidGameState = initialGameState.copy(table = Map.empty)
      val command = PassCommand()
      val resultState = command.execute(invalidGameState)

      resultState shouldBe invalidGameState.copy(lastEvent =
        Some(GameEvent.InvalidMove)
      )
    }

    "execute correctly when defender passes (DefensePhase branch)" in {
      val defenseGameState = initialGameState.copy(gamePhase = DefensePhase)

      val command = PassCommand()
      val resultState = command.execute(defenseGameState)

      resultState.lastEvent shouldBe Some(GameEvent.InvalidMove)
    }
  }
}
