package de.htwg.DurakApp.aview.tui.handler

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Suit, Rank, GameState, Player}
import de.htwg.DurakApp.model.state._
import de.htwg.DurakApp.controller._

class GamePhaseInputHandlerSpec extends AnyWordSpec with Matchers {

  val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)

  "A GamePhaseInputHandler" should {
    "handle player count input in AskPlayerCountPhase" in {
      val gameState = GameState(
        players = List.empty,
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 0,
        gamePhase = AskPlayerCountPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      
      val handler = GamePhaseInputHandler(None)
      val result = handler.handleRequest("3", gameState)
      
      result shouldBe SetPlayerCountAction(3)
    }
    
    "handle player count input in SetupPhase" in {
      val gameState = GameState(
        players = List.empty,
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 0,
        gamePhase = SetupPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      
      val handler = GamePhaseInputHandler(None)
      val result = handler.handleRequest("2", gameState)
      
      result shouldBe SetPlayerCountAction(2)
    }
    
    "return InvalidAction for non-numeric input in AskPlayerCountPhase" in {
      val gameState = GameState(
        players = List.empty,
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 0,
        gamePhase = AskPlayerCountPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      
      val handler = GamePhaseInputHandler(None)
      val result = handler.handleRequest("abc", gameState)
      
      result shouldBe InvalidAction
    }
    
    "handle player name input in AskPlayerNamesPhase" in {
      val gameState = GameState(
        players = List.empty,
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 0,
        gamePhase = AskPlayerNamesPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = Some(2),
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      
      val handler = GamePhaseInputHandler(None)
      val result = handler.handleRequest("Alice", gameState)
      
      result shouldBe AddPlayerNameAction("Alice")
    }
    
    "handle deck size input in AskDeckSizePhase" in {
      val gameState = GameState(
        players = List.empty,
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 0,
        gamePhase = AskDeckSizePhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = Some(2),
        setupPlayerNames = List("Alice", "Bob"),
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      
      val handler = GamePhaseInputHandler(None)
      val result = handler.handleRequest("36", gameState)
      
      result shouldBe SetDeckSizeAction(36)
    }
    
    "return InvalidAction for non-numeric input in AskDeckSizePhase" in {
      val gameState = GameState(
        players = List.empty,
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 0,
        gamePhase = AskDeckSizePhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = Some(2),
        setupPlayerNames = List("Alice", "Bob"),
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      
      val handler = GamePhaseInputHandler(None)
      val result = handler.handleRequest("large", gameState)
      
      result shouldBe InvalidAction
    }
    
    "handle yes input in AskPlayAgainPhase" in {
      val player1 = Player("Alice", List.empty)
      val player2 = Player("Bob", List.empty)
      
      val gameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = AskPlayAgainPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = Some(2),
        setupPlayerNames = List("Alice", "Bob"),
        setupDeckSize = Some(36),
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      
      val handler = GamePhaseInputHandler(None)
      val result = handler.handleRequest("yes", gameState)
      
      result shouldBe PlayAgainAction
    }
    
    "handle no input in AskPlayAgainPhase" in {
      val player1 = Player("Alice", List.empty)
      val player2 = Player("Bob", List.empty)
      
      val gameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = AskPlayAgainPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = Some(2),
        setupPlayerNames = List("Alice", "Bob"),
        setupDeckSize = Some(36),
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      
      val handler = GamePhaseInputHandler(None)
      val result = handler.handleRequest("no", gameState)
      
      result shouldBe ExitGameAction
    }
    
    "return InvalidAction for invalid input in AskPlayAgainPhase" in {
      val player1 = Player("Alice", List.empty)
      val player2 = Player("Bob", List.empty)
      
      val gameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = AskPlayAgainPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = Some(2),
        setupPlayerNames = List("Alice", "Bob"),
        setupDeckSize = Some(36),
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      
      val handler = GamePhaseInputHandler(None)
      val result = handler.handleRequest("maybe", gameState)
      
      result shouldBe InvalidAction
    }
    
    "delegate to next handler in other phases" in {
      val player1 = Player("Alice", List(Card(Suit.Hearts, Rank.Six)))
      val player2 = Player("Bob", List(Card(Suit.Diamonds, Rank.Seven)))
      
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
      
      val nextHandler = InvalidInputHandler()
      val handler = GamePhaseInputHandler(Some(nextHandler))
      val result = handler.handleRequest("test", gameState)
      
      result shouldBe InvalidAction
    }
  }
}
