package de.htwg.DurakApp.controller.command

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model._
import de.htwg.DurakApp.model.state._

class PlayCardCommandSpec extends AnyWordSpec with Matchers {
  "A PlayCardCommand" should {
    val player1Card1 = Card(Suit.Clubs, Rank.Six)
    val player1Card2 = Card(Suit.Clubs, Rank.Seven)
    val player2Card = Card(Suit.Hearts, Rank.Ace)

    val player1ForAttack = Player("P1", List(player1Card1)) // Simplify hand for this test
    val player2ForAttack = Player("P2", List(player2Card))

    val initialGameStateAttack = GameState(
      players = List(player1ForAttack, player2ForAttack),
      deck = List.empty,
      table = Map.empty,
      discardPile = List.empty,
      trumpCard = Card(Suit.Diamonds, Rank.Ace),
      attackerIndex = 0,
      defenderIndex = 1,
      gamePhase = AttackPhase
    )

    val attackCardOnTable = Card(Suit.Spades, Rank.Eight)
    val defendingCard = Card(Suit.Spades, Rank.Nine) // Card that can beat attackCardOnTable
    val player1ForDefense = Player("P1", List.empty)
    val player2ForDefense = Player("P2", List(defendingCard))
    val initialGameStateDefense = GameState(
      players = List(player1ForDefense, player2ForDefense),
      deck = List.empty,
      table = Map(attackCardOnTable -> None), // A card is on table to defend against
      discardPile = List.empty,
      trumpCard = Card(Suit.Diamonds, Rank.Ace),
      attackerIndex = 0,
      defenderIndex = 1,
      gamePhase = DefensePhase
    )

    "execute correctly when playing a card in AttackPhase" in {
      val command = PlayCardCommand("0") // Play first card
      val resultState = command.execute(initialGameStateAttack)

      resultState.players(0).hand.shouldNot(contain(player1Card1))
      resultState.table.keys.should(contain(player1Card1))
      resultState.gamePhase shouldBe DefensePhase
      resultState.lastEvent.get shouldBe a[GameEvent.Attack]
    }

    "execute correctly when playing a card in DefensePhase" in {
      val command = PlayCardCommand("0") // Play first card (player1Card1) to defend
      val resultState = command.execute(initialGameStateDefense)

      resultState.players(0).hand.shouldNot(contain(player1Card1))
      resultState.table.get(attackCardOnTable).flatten.should(contain(defendingCard))
      resultState.gamePhase shouldBe AttackPhase // All defended, switches back to AttackPhase
      resultState.lastEvent.get shouldBe a[GameEvent.Defend]
    }

    "not execute with an invalid card index" in {
      val command = PlayCardCommand("99") // Invalid index
      val resultState = command.execute(initialGameStateAttack)
      resultState.lastEvent.get shouldBe GameEvent.InvalidMove
      resultState shouldBe initialGameStateAttack.copy(lastEvent = Some(GameEvent.InvalidMove))
    }

    "not execute with non-numeric input" in {
      val command = PlayCardCommand("foo") // Non-numeric input
      val resultState = command.execute(initialGameStateAttack)
      resultState.lastEvent.get shouldBe GameEvent.InvalidMove
      resultState shouldBe initialGameStateAttack.copy(lastEvent = Some(GameEvent.InvalidMove))
    }
  }
}
