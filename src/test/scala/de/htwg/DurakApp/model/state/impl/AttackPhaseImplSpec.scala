package de.htwg.DurakApp.model.state.impl

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.ModelInterface.*
import de.htwg.DurakApp.model.ModelInterface.StateInterface.*

class AttackPhaseImplSpec extends AnyWordSpec with Matchers {
  "An AttackPhase" should {
    "set currentAttackerIndex on handle" in {
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
      resultState.currentAttackerIndex shouldBe Some(0)
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

    "allow non-primary attacker to play when it's their turn" in {
      val card = Card(Suit.Clubs, Rank.Seven)
      val attacker = Player("A", List(card))
      val otherAttacker = Player("B", List(card))
      val defender = Player("D", List(Card(Suit.Hearts, Rank.Ace)))
      val state = GameState(
        players = List(attacker, otherAttacker, defender),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = Card(Suit.Diamonds, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 2,
        gamePhase = AttackPhase,
        currentAttackerIndex = Some(1)
      )

      val result = AttackPhase.playCard(card, 1, state)
      result.players(1).hand.shouldNot(contain(card))
      result.table.keys.should(contain(card))
      result.gamePhase shouldBe DefensePhase
    }

    "not allow defender to attack (NotYourTurn)" in {
      val card = Card(Suit.Clubs, Rank.Seven)
      val attacker = Player("A", List(card))
      val defender = Player("D", List(card))
      val state = GameState(
        players = List(attacker, defender),
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

    "not allow defender to pass (NotYourTurn)" in {
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

    "allow non-primary attacker to pass and record in passedPlayers" in {
      val player1 = Player("P1", List(Card(Suit.Clubs, Rank.Six)))
      val player2 = Player("P2", List(Card(Suit.Hearts, Rank.Seven)))
      val player3 = Player("P3", List(Card(Suit.Hearts, Rank.Ace)))
      val baseState = GameState(
        players = List(player1, player2, player3),
        deck = List.empty,
        table = Map(Card(Suit.Spades, Rank.Seven) -> None),
        discardPile = List.empty,
        trumpCard = Card(Suit.Diamonds, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 2,
        gamePhase = AttackPhase,
        currentAttackerIndex = Some(1)
      )

      val result = AttackPhase.pass(1, baseState)
      result.passedPlayers should contain(1)
    }
    
    "not allow playCard when table size >= 6" in {
      val playCard = Card(Suit.Clubs, Rank.Six)
      val attacker = Player("A", List(playCard))
      val defender = Player("D", List(Card(Suit.Hearts, Rank.Ace), Card(Suit.Hearts, Rank.King)))
      val table = Map(
        Card(Suit.Spades, Rank.Six) -> None,
        Card(Suit.Hearts, Rank.Six) -> None,
        Card(Suit.Diamonds, Rank.Six) -> None,
        Card(Suit.Clubs, Rank.Seven) -> None,
        Card(Suit.Spades, Rank.Seven) -> None,
        Card(Suit.Hearts, Rank.Seven) -> None
      )
      val state = GameState(
        players = List(attacker, defender),
        deck = List.empty,
        table = table,
        discardPile = List.empty,
        trumpCard = Card(Suit.Diamonds, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = AttackPhase
      )
      
      val result = AttackPhase.playCard(playCard, 0, state)
      result shouldBe state.copy(lastEvent = Some(GameEvent.InvalidMove))
    }
    
    "allow pass and switch to next attacker when multiple attackers available" in {
      val player1 = Player("P1", List(Card(Suit.Clubs, Rank.Six)))
      val player2 = Player("P2", List(Card(Suit.Hearts, Rank.Seven)))
      val player3 = Player("P3", List(Card(Suit.Spades, Rank.Seven)))
      val player4 = Player("P4", List(Card(Suit.Hearts, Rank.Ace)))
      val state = GameState(
        players = List(player1, player2, player3, player4),
        deck = List.empty,
        table = Map(Card(Suit.Diamonds, Rank.Seven) -> None),
        discardPile = List.empty,
        trumpCard = Card(Suit.Diamonds, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 3,
        gamePhase = AttackPhase,
        currentAttackerIndex = Some(0)
      )
      
      val result = AttackPhase.pass(0, state)
      result.passedPlayers should contain(0)
      result.currentAttackerIndex shouldBe Some(1)
      result.lastEvent shouldBe Some(GameEvent.Pass)
    }
    
    "return to main attacker when other attackers have passed" in {
      val player1 = Player("P1", List(Card(Suit.Clubs, Rank.Six)))
      val player2 = Player("P2", List(Card(Suit.Hearts, Rank.Seven)))
      val player3 = Player("P3", List(Card(Suit.Hearts, Rank.Ace)))
      val state = GameState(
        players = List(player1, player2, player3),
        deck = List.empty,
        table = Map(Card(Suit.Diamonds, Rank.Seven) -> None),
        discardPile = List.empty,
        trumpCard = Card(Suit.Diamonds, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 2,
        gamePhase = AttackPhase,
        currentAttackerIndex = Some(1),
        passedPlayers = Set.empty
      )
      
      val result = AttackPhase.pass(1, state)
      result.passedPlayers should contain(1)
      result.currentAttackerIndex shouldBe Some(0)
    }
    
    "not allow wrong attacker to play when it's not their turn" in {
      val card = Card(Suit.Clubs, Rank.Seven)
      val player1 = Player("P1", List(card))
      val player2 = Player("P2", List(card))
      val player3 = Player("P3", List(Card(Suit.Hearts, Rank.Ace)))
      val state = GameState(
        players = List(player1, player2, player3),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = Card(Suit.Diamonds, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 2,
        gamePhase = AttackPhase,
        currentAttackerIndex = Some(0)
      )
      
      val result = AttackPhase.playCard(card, 1, state)
      result shouldBe state.copy(lastEvent = Some(GameEvent.NotYourTurn))
    }
    
    "not allow wrong attacker to pass when it's not their turn" in {
      val player1 = Player("P1", List(Card(Suit.Clubs, Rank.Six)))
      val player2 = Player("P2", List(Card(Suit.Hearts, Rank.Seven)))
      val player3 = Player("P3", List(Card(Suit.Hearts, Rank.Ace)))
      val state = GameState(
        players = List(player1, player2, player3),
        deck = List.empty,
        table = Map(Card(Suit.Diamonds, Rank.Seven) -> None),
        discardPile = List.empty,
        trumpCard = Card(Suit.Diamonds, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 2,
        gamePhase = AttackPhase,
        currentAttackerIndex = Some(0)
      )
      
      val result = AttackPhase.pass(1, state)
      result shouldBe state.copy(lastEvent = Some(GameEvent.NotYourTurn))
    }
    
    "check ranks from defended cards in table (table.values.flatten.map(_.rank))" in {
      val attackCard = Card(Suit.Clubs, Rank.Seven)
      val defendedCard = Card(Suit.Hearts, Rank.Eight)
      val newAttackCard = Card(Suit.Diamonds, Rank.Eight)
      val player1 = Player("P1", List(newAttackCard))
      val player2 = Player("P2", List(Card(Suit.Spades, Rank.Nine)))
      val state = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map(attackCard -> Some(defendedCard)),
        discardPile = List.empty,
        trumpCard = Card(Suit.Diamonds, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = AttackPhase,
        currentAttackerIndex = Some(0)
      )
      
      val result = AttackPhase.playCard(newAttackCard, 0, state)
      result.players(0).hand.shouldNot(contain(newAttackCard))
      result.table.keys.toList should contain(newAttackCard)
      result.lastEvent.get shouldBe a[GameEvent.Attack]
    }
    
    "not allow card with rank not matching defended cards in table" in {
      val attackCard = Card(Suit.Clubs, Rank.Seven)
      val defendedCard = Card(Suit.Hearts, Rank.Eight)
      val invalidCard = Card(Suit.Diamonds, Rank.Nine)
      val player1 = Player("P1", List(invalidCard))
      val player2 = Player("P2", List(Card(Suit.Spades, Rank.Ten)))
      val state = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map(attackCard -> Some(defendedCard)),
        discardPile = List.empty,
        trumpCard = Card(Suit.Diamonds, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = AttackPhase,
        currentAttackerIndex = Some(0)
      )
      
      val result = AttackPhase.playCard(invalidCard, 0, state)
      result shouldBe state.copy(lastEvent = Some(GameEvent.InvalidMove))
    }
  }
}
