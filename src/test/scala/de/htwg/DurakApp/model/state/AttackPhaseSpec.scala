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
      val player1 =
        Player("P1", List(attackerCard, Card(Suit.Clubs, Rank.Eight)))
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
      resultState shouldBe initialGameState.copy(lastEvent =
        Some(GameEvent.InvalidMove)
      )
    }

    "not allow playing card if rank does not match table cards" in {
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

      val resultState =
        AttackPhase.playCard(Card(Suit.Clubs, Rank.Six), 0, initialGameState)
      resultState shouldBe initialGameState.copy(lastEvent =
        Some(GameEvent.InvalidMove)
      )
    }

    "allow attacker to pass" in {
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

      val resultState = AttackPhase.pass(0, initialGameState)
      resultState.gamePhase shouldBe DrawPhase
      resultState.roundWinner shouldBe Some(1)
      resultState.lastEvent.get shouldBe GameEvent.Pass
    }

    "not allow passing if table is empty" in {
      val player1 = Player("P1", List(Card(Suit.Clubs, Rank.Six)))
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

      val resultState = AttackPhase.pass(0, initialGameState)
      resultState shouldBe initialGameState.copy(lastEvent =
        Some(GameEvent.InvalidMove)
      )
    }

    "not allow playCard with an invalid player index" in {
      val card = Card(Suit.Clubs, Rank.Seven)
      val player1 = Player("P1", List(card))
      val player2 = Player("P2", List.empty)
      val state = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = Card(Suit.Diamonds, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = AttackPhase
      )

      val result = AttackPhase.playCard(card, -1, state)
      result shouldBe state.copy(lastEvent = Some(GameEvent.InvalidMove))
    }

    "not allow non-attacker to play (NotYourTurn)" in {
      val card = Card(Suit.Clubs, Rank.Seven)
      val attacker = Player("A", List(card))
      val nonAttacker = Player("B", List(card))
      val state = GameState(
        players = List(attacker, nonAttacker),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = Card(Suit.Diamonds, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = AttackPhase
      )

      val result = AttackPhase.playCard(card, 1, state)
      result shouldBe state.copy(lastEvent = Some(GameEvent.NotYourTurn))
    }

    "not allow playCard when openAttacks >= defender.hand.size" in {
      val playCard = Card(Suit.Clubs, Rank.Six)
      val attacker = Player("A", List(playCard))
      val defender = Player("D", List(Card(Suit.Hearts, Rank.Ace)))
      val state = GameState(
        players = List(attacker, defender),
        deck = List.empty,
        table = Map(Card(Suit.Spades, Rank.Six) -> None),
        discardPile = List.empty,
        trumpCard = Card(Suit.Diamonds, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = AttackPhase
      )

      val result = AttackPhase.playCard(playCard, 0, state)
      result shouldBe state.copy(lastEvent = Some(GameEvent.InvalidMove))
    }

    "not allow pass with invalid player index" in {
      val player1 = Player("P1", List(Card(Suit.Clubs, Rank.Six)))
      val player2 = Player("P2", List(Card(Suit.Hearts, Rank.Ace)))
      val state = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map(Card(Suit.Spades, Rank.Seven) -> None),
        discardPile = List.empty,
        trumpCard = Card(Suit.Diamonds, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = AttackPhase
      )

      val result = AttackPhase.pass(-5, state)
      result shouldBe state.copy(lastEvent = Some(GameEvent.InvalidMove))
    }

    "not allow non-attacker to pass when they are not in passedPlayers (NotYourTurn)" in {
      val player1 = Player("P1", List(Card(Suit.Clubs, Rank.Six)))
      val player2 = Player("P2", List(Card(Suit.Hearts, Rank.Ace)))
      val state = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map(Card(Suit.Spades, Rank.Seven) -> None),
        discardPile = List.empty,
        trumpCard = Card(Suit.Diamonds, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = AttackPhase
      )

      val result = AttackPhase.pass(1, state)
      result shouldBe state.copy(lastEvent = Some(GameEvent.NotYourTurn))
    }

    "record a non-attacker in passedPlayers when they are already present (idempotent add)" in {
      val player1 = Player("P1", List(Card(Suit.Clubs, Rank.Six)))
      val player2 = Player("P2", List(Card(Suit.Hearts, Rank.Ace)))
      val baseState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map(Card(Suit.Spades, Rank.Seven) -> None),
        discardPile = List.empty,
        trumpCard = Card(Suit.Diamonds, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = AttackPhase
      )

      val alreadyPassed =
        baseState.copy(passedPlayers = baseState.passedPlayers + 1)

      val result = AttackPhase.pass(1, alreadyPassed)
      result.passedPlayers should contain(1)
      result.passedPlayers shouldBe alreadyPassed.passedPlayers
    }
  }
}
