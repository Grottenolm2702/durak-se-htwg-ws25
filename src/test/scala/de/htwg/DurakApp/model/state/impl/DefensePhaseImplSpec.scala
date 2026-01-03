package de.htwg.DurakApp.model.state.impl

import de.htwg.DurakApp.testutil.TestHelpers._
import de.htwg.DurakApp.testutil.TestGamePhases

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Suit, Rank, GameState, Player}
import de.htwg.DurakApp.model.state.{GameEvent}

class DefensePhaseImplSpec extends AnyWordSpec with Matchers {

  "DefensePhaseImpl" should {
    "have correct string representation" in {
      DefensePhaseImpl.toString shouldBe "TestGamePhases.defensePhase"
    }
    
    "handle returns same state" in {
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
        gamePhase = TestGamePhases.setupPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      
      val result = DefensePhaseImpl.handle(gameState)
      result shouldBe gameState
    }
    
    "defend successfully with higher same suit card" in {
      val attackCard = Card(Suit.Hearts, Rank.Six)
      val defenseCard = Card(Suit.Hearts, Rank.Seven)
      val player1 = Player("Alice", List.empty)
      val player2 = Player("Bob", List(defenseCard))
      val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val table = Map(attackCard -> None)
      
      val gameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = table,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = TestGamePhases.setupPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = Some(0)
      )
      
      val result = DefensePhaseImpl.playCard(defenseCard, 1, gameState)
      result.table.get(attackCard) shouldBe Some(Some(defenseCard))
      result.lastEvent shouldBe Some(GameEvent.Defend(defenseCard))
      result.gamePhase shouldBe AttackPhaseImpl
    }
    
    "defend successfully with trump against non-trump" in {
      val attackCard = Card(Suit.Hearts, Rank.Ace)
      val defenseCard = Card(Suit.Clubs, Rank.Six, isTrump = true)
      val player1 = Player("Alice", List.empty)
      val player2 = Player("Bob", List(defenseCard))
      val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val table = Map(attackCard -> None)
      
      val gameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = table,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = TestGamePhases.setupPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = Some(0)
      )
      
      val result = DefensePhaseImpl.playCard(defenseCard, 1, gameState)
      result.table.get(attackCard) shouldBe Some(Some(defenseCard))
      result.lastEvent shouldBe Some(GameEvent.Defend(defenseCard))
    }
    
    "return NotYourTurn when attacker tries to play" in {
      val attackCard = Card(Suit.Hearts, Rank.Six)
      val defenseCard = Card(Suit.Hearts, Rank.Seven)
      val player1 = Player("Alice", List(defenseCard))
      val player2 = Player("Bob", List(Card(Suit.Diamonds, Rank.Seven)))
      val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val table = Map(attackCard -> None)
      
      val gameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = table,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = TestGamePhases.setupPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      
      val result = DefensePhaseImpl.playCard(defenseCard, 0, gameState)
      result.lastEvent shouldBe Some(GameEvent.NotYourTurn)
    }
    
    "return InvalidMove when card not in hand" in {
      val attackCard = Card(Suit.Hearts, Rank.Six)
      val defenseCard = Card(Suit.Hearts, Rank.Seven)
      val player1 = Player("Alice", List.empty)
      val player2 = Player("Bob", List(Card(Suit.Diamonds, Rank.Seven)))
      val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val table = Map(attackCard -> None)
      
      val gameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = table,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = TestGamePhases.setupPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      
      val result = DefensePhaseImpl.playCard(defenseCard, 1, gameState)
      result.lastEvent shouldBe Some(GameEvent.InvalidMove)
    }
    
    "return InvalidMove when all attacks defended" in {
      val attackCard = Card(Suit.Hearts, Rank.Six)
      val defenseCard = Card(Suit.Hearts, Rank.Seven)
      val anotherCard = Card(Suit.Hearts, Rank.Eight)
      val player1 = Player("Alice", List.empty)
      val player2 = Player("Bob", List(anotherCard))
      val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val table = Map(attackCard -> Some(defenseCard))
      
      val gameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = table,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = TestGamePhases.setupPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      
      val result = DefensePhaseImpl.playCard(anotherCard, 1, gameState)
      result.lastEvent shouldBe Some(GameEvent.InvalidMove)
    }
    
    "return InvalidMove when defense card too weak" in {
      val attackCard = Card(Suit.Hearts, Rank.Seven)
      val defenseCard = Card(Suit.Hearts, Rank.Six)
      val player1 = Player("Alice", List.empty)
      val player2 = Player("Bob", List(defenseCard))
      val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val table = Map(attackCard -> None)
      
      val gameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = table,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = TestGamePhases.setupPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      
      val result = DefensePhaseImpl.playCard(defenseCard, 1, gameState)
      result.lastEvent shouldBe Some(GameEvent.InvalidMove)
    }
    
    "take cards successfully" in {
      val attackCard = Card(Suit.Hearts, Rank.Six)
      val defenseCard = Card(Suit.Hearts, Rank.Seven)
      val player1 = Player("Alice", List.empty)
      val player2 = Player("Bob", List(Card(Suit.Diamonds, Rank.Nine)))
      val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val table = Map(attackCard -> Some(defenseCard))
      
      val gameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = table,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = TestGamePhases.setupPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      
      val result = DefensePhaseImpl.takeCards(1, gameState)
      result.table shouldBe empty
      result.lastEvent shouldBe Some(GameEvent.Take)
      result.gamePhase shouldBe DrawPhaseImpl
      result.players(1).hand.size shouldBe 3
    }
    
    "return NotYourTurn when attacker tries to take cards" in {
      val attackCard = Card(Suit.Hearts, Rank.Six)
      val player1 = Player("Alice", List.empty)
      val player2 = Player("Bob", List(Card(Suit.Diamonds, Rank.Seven)))
      val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val table = Map(attackCard -> None)
      
      val gameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = table,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = TestGamePhases.setupPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      
      val result = DefensePhaseImpl.takeCards(0, gameState)
      result.lastEvent shouldBe Some(GameEvent.NotYourTurn)
    }
  }
}
