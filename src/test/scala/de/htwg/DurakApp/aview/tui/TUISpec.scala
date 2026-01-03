package de.htwg.DurakApp.aview.tui

import de.htwg.DurakApp.testutil.TestHelpers._

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Player, Suit, Rank, GameState}
import de.htwg.DurakApp.model.state._
import de.htwg.DurakApp.testutil.{TestHelper, StubGameSetup, StubUndoRedoManager, SpyController}
import de.htwg.DurakApp.controller.Controller
import com.google.inject.Guice

class TUISpec extends AnyWordSpec with Matchers {

  "A TUI" should {

    "be created with a controller" in {
      val initialState = TestHelper.createTestGameState()
      val controller = new SpyController(
        initialState,
        new StubUndoRedoManager(),
        new StubGameSetup()
      )
      val tui = new TUI(controller)
      
      tui should not be null
    }

    "build status string for attack phase" in {
      val attacker = Player("Angreifer", List.empty)
      val defender = Player("Verteidiger", List.empty)
      val game = TestHelper.createTestGameState(
        players = List(attacker, defender),
        gamePhase = AttackPhase,
        lastEvent = Some(GameEvent.Attack(Card(Suit.Spades, Rank.Six)))
      )
      
      val controller = new SpyController(game, new StubUndoRedoManager(), new StubGameSetup())
      val tui = new TUI(controller)
      val statusString = tui.buildStatusString(game)
      
      statusString should not be empty
      statusString should include("Angriff")
    }

    "respond to update notification" in {
      val initialState = TestHelper.createTestGameState()
      val controller = new SpyController(
        initialState,
        new StubUndoRedoManager(),
        new StubGameSetup()
      )
      
      val tui = new TUI(controller)
      noException should be thrownBy tui.update
    }
    
    "show correct description for SetupPhase" in {
      val gameState = TestHelper.createTestGameState(gamePhase = SetupPhase)
      val controller = new SpyController(gameState, new StubUndoRedoManager(), new StubGameSetup())
      val tui = new TUI(controller)
      
      val desc = tui.description(gameState)
      
      desc should include("Spieleranzahl")
    }
    
    "show correct description for AskPlayerCountPhase" in {
      val gameState = TestHelper.createTestGameState(gamePhase = AskPlayerCountPhase)
      val controller = new SpyController(gameState, new StubUndoRedoManager(), new StubGameSetup())
      val tui = new TUI(controller)
      
      val desc = tui.description(gameState)
      
      desc should include("Spieleranzahl")
    }
    
    "show correct description for AskPlayerNamesPhase" in {
      val gameState = TestHelper.createTestGameState(
        gamePhase = AskPlayerNamesPhase,
        setupPlayerNames = List("Alice")
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager(), new StubGameSetup())
      val tui = new TUI(controller)
      
      val desc = tui.description(gameState)
      
      desc should include("Spielername")
      desc should include("2")
    }
    
    "show correct description for AskDeckSizePhase" in {
      val gameState = TestHelper.createTestGameState(
        gamePhase = AskDeckSizePhase,
        setupPlayerNames = List("Alice", "Bob")
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager(), new StubGameSetup())
      val tui = new TUI(controller)
      
      val desc = tui.description(gameState)
      
      desc should include("Deckgröße")
      desc should include("2")
    }
    
    "show correct description for AskPlayAgainPhase" in {
      val gameState = TestHelper.createTestGameState(gamePhase = AskPlayAgainPhase)
      val controller = new SpyController(gameState, new StubUndoRedoManager(), new StubGameSetup())
      val tui = new TUI(controller)
      
      val desc = tui.description(gameState)
      
      desc should include("neue Runde")
    }
    
    "show correct description for AttackPhase" in {
      val gameState = TestHelper.createTestGameState(gamePhase = AttackPhase)
      val controller = new SpyController(gameState, new StubUndoRedoManager(), new StubGameSetup())
      val tui = new TUI(controller)
      
      val desc = tui.description(gameState)
      
      desc should include("AttackPhase")
    }
    
    "build status string contains important information" in {
      val player1 = Player("Alice", List(Card(Suit.Hearts, Rank.Six)))
      val player2 = Player("Bob", List(Card(Suit.Diamonds, Rank.Seven)))
      val gameState = TestHelper.createTestGameState(
        players = List(player1, player2),
        gamePhase = AttackPhase
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager(), new StubGameSetup())
      val tui = new TUI(controller)
      
      val statusString = tui.buildStatusString(gameState)
      
      statusString should not be empty
      statusString.length should be > 0
    }
    
    "build status string for DefensePhase" in {
      val player1 = Player("Alice", List(Card(Suit.Hearts, Rank.Six)))
      val player2 = Player("Bob", List(Card(Suit.Diamonds, Rank.Seven)))
      val gameState = TestHelper.createTestGameState(
        players = List(player1, player2),
        gamePhase = DefensePhase,
        lastEvent = Some(GameEvent.Defend(Card(Suit.Hearts, Rank.Seven)))
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager(), new StubGameSetup())
      val tui = new TUI(controller)
      
      val statusString = tui.buildStatusString(gameState)
      
      statusString should not be empty
      statusString should include("Verteidigung")
    }
    
    "build status string for GameOver event contains end message" in {
      val player1 = Player("Alice", List.empty)
      val player2 = Player("Bob", List(Card(Suit.Hearts, Rank.Six)))
      val gameState = TestHelper.createTestGameState(
        players = List(player1, player2),
        gamePhase = AskPlayAgainPhase,
        lastEvent = Some(GameEvent.GameOver(player1, Some(player2)))
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager(), new StubGameSetup())
      val tui = new TUI(controller)
      
      val statusString = tui.buildStatusString(gameState)
      
      statusString should include("Spiel")
    }
  }
}
