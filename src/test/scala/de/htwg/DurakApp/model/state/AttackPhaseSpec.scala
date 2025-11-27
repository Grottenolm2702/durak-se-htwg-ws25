package de.htwg.DurakApp.model.state

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model._

class AttackPhaseSpec extends AnyWordSpec with Matchers {
  "An AttackPhase" should {
    "handle the game state without changing it by default" in {
      val player1 = Player("P1", List(Card(Suit.Clubs, Rank.Six)))
      val player2 = Player("P2", List.empty)
      val initialGameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = Card(Suit.Diamonds, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = AttackPhase
      )
      val resultState = AttackPhase.handle(initialGameState)
      resultState shouldBe initialGameState
    }

    "allow an attacker to play a card" in {
      val attackerCard = Card(Suit.Clubs, Rank.Seven)
      val player1 = Player("P1", List(attackerCard, Card(Suit.Clubs, Rank.Eight)))
      val player2 = Player("P2", List(Card(Suit.Hearts, Rank.Ace)))
      val initialGameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = Card(Suit.Diamonds, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = AttackPhase
      )

      val resultState = AttackPhase.playCard(attackerCard, 0, initialGameState)

      resultState.players(0).hand.shouldNot(contain(attackerCard))
      resultState.table.keys.should(contain(attackerCard))
      resultState.gamePhase shouldBe DefensePhase
      resultState.lastEvent.get shouldBe a[GameEvent.Attack]
    }

    "not allow attacker to play a card not in hand" in {
      val attackerCard = Card(Suit.Clubs, Rank.Seven)
      val player1 = Player("P1", List(Card(Suit.Clubs, Rank.Eight)))
      val player2 = Player("P2", List(Card(Suit.Hearts, Rank.Ace)))
      val initialGameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = Card(Suit.Diamonds, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = AttackPhase
      )

      val resultState = AttackPhase.playCard(attackerCard, 0, initialGameState)
      resultState shouldBe initialGameState.copy(lastEvent = Some(GameEvent.InvalidMove))
    }

    "not allow playing card if rank does not match table cards" in {
      val player1 = Player("P1", List(Card(Suit.Clubs, Rank.Six)))
      val player2 = Player("P2", List(Card(Suit.Hearts, Rank.Ace)))
      val initialGameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map(Card(Suit.Spades, Rank.Seven) -> None), // Table has a 7
        discardPile = List.empty,
        trumpCard = Card(Suit.Diamonds, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = AttackPhase
      )

      val resultState = AttackPhase.playCard(Card(Suit.Clubs, Rank.Six), 0, initialGameState)
      resultState shouldBe initialGameState.copy(lastEvent = Some(GameEvent.InvalidMove))
    }

    "allow attacker to pass" in {
      val player1 = Player("P1", List(Card(Suit.Clubs, Rank.Six)))
      val player2 = Player("P2", List(Card(Suit.Hearts, Rank.Ace)))
      val initialGameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map(Card(Suit.Spades, Rank.Seven) -> None), // Cards on table
        discardPile = List.empty,
        trumpCard = Card(Suit.Diamonds, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = AttackPhase
      )

      val resultState = AttackPhase.pass(0, initialGameState) // Attacker passes
      resultState.gamePhase shouldBe AttackPhase // RoundPhase -> AttackPhase after handle
      resultState.roundWinner.isDefined shouldBe false // roundWinner is reset after round
      resultState.lastEvent.get shouldBe GameEvent.RoundEnd(cleared = true)
    }

    "not allow passing if table is empty" in {
      val player1 = Player("P1", List(Card(Suit.Clubs, Rank.Six)))
      val player2 = Player("P2", List(Card(Suit.Hearts, Rank.Ace)))
      val initialGameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map.empty, // No cards on table
        discardPile = List.empty,
        trumpCard = Card(Suit.Diamonds, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = AttackPhase
      )

      val resultState = AttackPhase.pass(0, initialGameState) // Attacker tries to pass
      resultState shouldBe initialGameState.copy(lastEvent = Some(GameEvent.InvalidMove))
    }
  }
}
