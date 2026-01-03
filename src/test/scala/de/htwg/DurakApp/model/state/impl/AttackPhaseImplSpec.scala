package de.htwg.DurakApp.model.state.impl

import de.htwg.DurakApp.testutil.TestHelpers._

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Suit, Rank, GameState, Player}
import de.htwg.DurakApp.model.state.{AttackPhase, DefensePhase, GameEvent}

class AttackPhaseImplSpec extends AnyWordSpec with Matchers {

  "AttackPhaseImpl" should {
    "have correct string representation" in {
      AttackPhaseImpl.toString shouldBe "AttackPhase"
    }
    
    "set currentAttackerIndex when empty" in {
      val player1 = Player("Alice", List(Card(Suit.Hearts, Rank.Six)))
      val player2 = Player("Bob", List(Card(Suit.Diamonds, Rank.Seven)))
      val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)
      
      val gameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = AttackPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      
      val result = AttackPhaseImpl.handle(gameState)
      result.currentAttackerIndex shouldBe Some(0)
    }
    
    "not modify currentAttackerIndex when already set" in {
      val player1 = Player("Alice", List(Card(Suit.Hearts, Rank.Six)))
      val player2 = Player("Bob", List(Card(Suit.Diamonds, Rank.Seven)))
      val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)
      
      val gameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = AttackPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = Some(0),
        lastAttackerIndex = None
      )
      
      val result = AttackPhaseImpl.handle(gameState)
      result shouldBe gameState
    }
    
    "play card successfully when valid" in {
      val card = Card(Suit.Hearts, Rank.Six)
      val player1 = Player("Alice", List(card))
      val player2 = Player("Bob", List(Card(Suit.Diamonds, Rank.Seven)))
      val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)
      
      val gameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = AttackPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = Some(0),
        lastAttackerIndex = None
      )
      
      val result = AttackPhaseImpl.playCard(card, 0, gameState)
      result.table.contains(card) shouldBe true
      result.gamePhase shouldBe DefensePhaseImpl
      result.lastEvent shouldBe Some(GameEvent.Attack(card))
    }
    
    "return InvalidMove when playerIndex out of bounds" in {
      val card = Card(Suit.Hearts, Rank.Six)
      val player1 = Player("Alice", List(card))
      val player2 = Player("Bob", List(Card(Suit.Diamonds, Rank.Seven)))
      val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)
      
      val gameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = AttackPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = Some(0),
        lastAttackerIndex = None
      )
      
      val result = AttackPhaseImpl.playCard(card, 5, gameState)
      result.lastEvent shouldBe Some(GameEvent.InvalidMove)
    }
    
    "return NotYourTurn when defender tries to play" in {
      val card = Card(Suit.Hearts, Rank.Six)
      val player1 = Player("Alice", List(card))
      val player2 = Player("Bob", List(Card(Suit.Diamonds, Rank.Seven)))
      val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)
      
      val gameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = AttackPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = Some(0),
        lastAttackerIndex = None
      )
      
      val result = AttackPhaseImpl.playCard(card, 1, gameState)
      result.lastEvent shouldBe Some(GameEvent.NotYourTurn)
    }
    
    "return NotYourTurn when wrong attacker plays" in {
      val card = Card(Suit.Hearts, Rank.Six)
      val player1 = Player("Alice", List(card))
      val player2 = Player("Bob", List(Card(Suit.Diamonds, Rank.Seven)))
      val player3 = Player("Charlie", List(Card(Suit.Spades, Rank.Eight)))
      val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)
      
      val gameState = GameState(
        players = List(player1, player2, player3),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = AttackPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = Some(0),
        lastAttackerIndex = None
      )
      
      val result = AttackPhaseImpl.playCard(card, 2, gameState)
      result.lastEvent shouldBe Some(GameEvent.NotYourTurn)
    }
    
    "return InvalidMove when card not in hand" in {
      val card = Card(Suit.Hearts, Rank.Six)
      val wrongCard = Card(Suit.Hearts, Rank.Seven)
      val player1 = Player("Alice", List(card))
      val player2 = Player("Bob", List(Card(Suit.Diamonds, Rank.Seven)))
      val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)
      
      val gameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = AttackPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = Some(0),
        lastAttackerIndex = None
      )
      
      val result = AttackPhaseImpl.playCard(wrongCard, 0, gameState)
      result.lastEvent shouldBe Some(GameEvent.InvalidMove)
    }
    
    "return InvalidMove when card rank not on table" in {
      val card = Card(Suit.Hearts, Rank.Six)
      val wrongCard = Card(Suit.Hearts, Rank.Nine)
      val player1 = Player("Alice", List(card, wrongCard))
      val player2 = Player("Bob", List(Card(Suit.Diamonds, Rank.Seven)))
      val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val table = Map(Card(Suit.Hearts, Rank.Six) -> None)
      
      val gameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = table,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = AttackPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = Some(0),
        lastAttackerIndex = None
      )
      
      val result = AttackPhaseImpl.playCard(wrongCard, 0, gameState)
      result.lastEvent shouldBe Some(GameEvent.InvalidMove)
    }
    
    "pass successfully and move to next attacker" in {
      val card1 = Card(Suit.Hearts, Rank.Six)
      val player1 = Player("Alice", List(card1))
      val player2 = Player("Bob", List(Card(Suit.Diamonds, Rank.Seven)))
      val player3 = Player("Charlie", List(Card(Suit.Spades, Rank.Eight)))
      val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val table = Map(card1 -> None)
      
      val gameState = GameState(
        players = List(player1, player2, player3),
        deck = List.empty,
        table = table,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = AttackPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = Some(0),
        lastAttackerIndex = None
      )
      
      val result = AttackPhaseImpl.pass(0, gameState)
      result.lastEvent shouldBe Some(GameEvent.Pass)
      result.passedPlayers.contains(0) shouldBe true
    }
    
    "return InvalidMove when passing with empty table" in {
      val player1 = Player("Alice", List(Card(Suit.Hearts, Rank.Six)))
      val player2 = Player("Bob", List(Card(Suit.Diamonds, Rank.Seven)))
      val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)
      
      val gameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = AttackPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = Some(0),
        lastAttackerIndex = None
      )
      
      val result = AttackPhaseImpl.pass(0, gameState)
      result.lastEvent shouldBe Some(GameEvent.InvalidMove)
    }
  }
}
