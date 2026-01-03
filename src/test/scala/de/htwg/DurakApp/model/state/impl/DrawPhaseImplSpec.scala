package de.htwg.DurakApp.model.state.impl

import de.htwg.DurakApp.testutil.TestHelpers._

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Suit, Rank, GameState, Player}
import de.htwg.DurakApp.model.state.GameEvent

class DrawPhaseImplSpec extends AnyWordSpec with Matchers {

  "DrawPhaseImpl" should {
    "have correct string representation" in {
      DrawPhaseImpl.toString shouldBe "DrawPhase"
    }
    
    "draw cards for attacker first" in {
      val player1 = Player("Alice", List(Card(Suit.Hearts, Rank.Six)))
      val player2 = Player("Bob", List(Card(Suit.Diamonds, Rank.Seven)))
      val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val deck = List.fill(10)(Card(Suit.Spades, Rank.Eight))
      
      val gameState = GameState(
        players = List(player1, player2),
        deck = deck,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = DrawPhaseImpl,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = Some(1),
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      
      val result = DrawPhaseImpl.handle(gameState)
      result.players(0).hand.size shouldBe 6
      result.players(1).hand.size shouldBe 6
      result.lastEvent shouldBe Some(GameEvent.Draw)
      result.gamePhase shouldBe RoundPhaseImpl
    }
    
    "set correct next attacker and defender when round won" in {
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
        gamePhase = DrawPhaseImpl,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = Some(1),
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      
      val result = DrawPhaseImpl.handle(gameState)
      result.attackerIndex shouldBe 1
      result.defenderIndex shouldBe 0
    }
    
    "set correct next attacker and defender when round lost" in {
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
        gamePhase = DrawPhaseImpl,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      
      val result = DrawPhaseImpl.handle(gameState)
      result.attackerIndex shouldBe 0
      result.defenderIndex shouldBe 1
    }
    
    "not draw more cards than needed to reach 6" in {
      val player1 = Player("Alice", List.fill(4)(Card(Suit.Hearts, Rank.Six)))
      val player2 = Player("Bob", List.fill(5)(Card(Suit.Diamonds, Rank.Seven)))
      val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val deck = List.fill(10)(Card(Suit.Spades, Rank.Eight))
      
      val gameState = GameState(
        players = List(player1, player2),
        deck = deck,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = DrawPhaseImpl,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = Some(1),
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      
      val result = DrawPhaseImpl.handle(gameState)
      result.players(0).hand.size shouldBe 6
      result.players(1).hand.size shouldBe 6
      result.deck.size shouldBe 7
    }
    
    "handle empty deck" in {
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
        gamePhase = DrawPhaseImpl,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = Some(1),
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      
      val result = DrawPhaseImpl.handle(gameState)
      result.players(0).hand.size shouldBe 1
      result.players(1).hand.size shouldBe 1
    }
    
    "draw for other attackers before defender" in {
      val player1 = Player("Alice", List(Card(Suit.Hearts, Rank.Six)))
      val player2 = Player("Bob", List(Card(Suit.Diamonds, Rank.Seven)))
      val player3 = Player("Charlie", List(Card(Suit.Spades, Rank.Nine)))
      val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val deck = List.fill(15)(Card(Suit.Hearts, Rank.Ten))
      
      val gameState = GameState(
        players = List(player1, player2, player3),
        deck = deck,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = DrawPhaseImpl,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = Some(1),
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      
      val result = DrawPhaseImpl.handle(gameState)
      result.players(0).hand.size shouldBe 6
      result.players(1).hand.size shouldBe 6
      result.players(2).hand.size shouldBe 6
    }
  }
}
