package de.htwg.DurakApp.aview.tui.handler

import de.htwg.DurakApp.testutil.TestHelpers._
import de.htwg.DurakApp.testutil.{TestGamePhases, TestGamePhasesInstance}

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Suit, Rank, GameState, Player}

import de.htwg.DurakApp.controller.{PlayCardAction, InvalidAction}

class PlayCardHandlerSpec extends AnyWordSpec with Matchers {

  "A PlayCardHandler" should {
    "handle play command with valid index in TestGamePhases.attackPhase" in {
      val card1 = Card(Suit.Hearts, Rank.Six)
      val card2 = Card(Suit.Hearts, Rank.Seven)
      val player1 = Player("Alice", List(card1, card2))
      val player2 = Player("Bob", List(Card(Suit.Diamonds, Rank.Eight)))
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
        currentAttackerIndex = Some(0),
        lastAttackerIndex = None
      )
      
      val handler = PlayCardHandler(None, TestGamePhasesInstance)
      val result = handler.handleRequest("play 0", gameState)
      
      result shouldBe PlayCardAction(card1)
    }
    
    "handle play command with valid index in TestGamePhases.defensePhase" in {
      val card1 = Card(Suit.Hearts, Rank.Six)
      val card2 = Card(Suit.Hearts, Rank.Seven)
      val player1 = Player("Alice", List(Card(Suit.Diamonds, Rank.Eight)))
      val player2 = Player("Bob", List(card1, card2))
      val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)
      
      val gameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map(Card(Suit.Hearts, Rank.Six) -> None),
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = TestGamePhases.defensePhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      
      val handler = PlayCardHandler(None, TestGamePhasesInstance)
      val result = handler.handleRequest("play 1", gameState)
      
      result shouldBe PlayCardAction(card2)
    }
    
    "return InvalidAction for invalid card index" in {
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
        currentAttackerIndex = Some(0),
        lastAttackerIndex = None
      )
      
      val handler = PlayCardHandler(None, TestGamePhasesInstance)
      val result = handler.handleRequest("play 5", gameState)
      
      result shouldBe InvalidAction
    }
    
    "return InvalidAction for non-numeric index" in {
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
        currentAttackerIndex = Some(0),
        lastAttackerIndex = None
      )
      
      val handler = PlayCardHandler(None, TestGamePhasesInstance)
      val result = handler.handleRequest("play abc", gameState)
      
      result shouldBe InvalidAction
    }
    
    "delegate to next handler for non-play command" in {
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
        currentAttackerIndex = Some(0),
        lastAttackerIndex = None
      )
      
      val nextHandler = InvalidInputHandler()
      val handler = PlayCardHandler(Some(nextHandler), TestGamePhasesInstance)
      val result = handler.handleRequest("pass", gameState)
      
      result shouldBe InvalidAction
    }
    
    "handle uppercase PLAY command" in {
      val card1 = Card(Suit.Hearts, Rank.Six)
      val player1 = Player("Alice", List(card1))
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
        currentAttackerIndex = Some(0),
        lastAttackerIndex = None
      )
      
      val handler = PlayCardHandler(None, TestGamePhasesInstance)
      val result = handler.handleRequest("PLAY 0", gameState)
      
      result shouldBe PlayCardAction(card1)
    }
  }
}
