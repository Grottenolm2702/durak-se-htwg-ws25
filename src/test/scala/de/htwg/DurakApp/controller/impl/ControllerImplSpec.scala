package de.htwg.DurakApp.controller.impl

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Suit, Rank, GameState, Player}
import de.htwg.DurakApp.model.state._
import de.htwg.DurakApp.controller._
import de.htwg.DurakApp.controller.command.CommandFactory
import de.htwg.DurakApp.util.impl.{UndoRedoManagerFactoryImpl, UndoRedoManagerImpl}
import de.htwg.DurakApp.model.builder.GameStateBuilderFactory

class ControllerImplSpec extends AnyWordSpec with Matchers {

  val builderFactory = GameStateBuilderFactory()
  val gameSetup = GameSetupImpl(builderFactory)
  val undoRedoManagerFactory = UndoRedoManagerFactoryImpl()

  "ControllerImpl with SetPlayerCountAction" should {
    "accept valid player count of 2" in {
      val initialGameState = builderFactory.create()
        .withGamePhase(AskPlayerCountPhase)
        .build()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, CommandFactory, gameSetup, undoRedoManagerFactory)
      
      controller.processPlayerAction(SetPlayerCountAction(2))
      
      controller.gameState.setupPlayerCount shouldBe Some(2)
      controller.gameState.gamePhase shouldBe AskPlayerNamesPhase
    }
    
    "accept valid player count of 6" in {
      val initialGameState = builderFactory.create()
        .withGamePhase(AskPlayerCountPhase)
        .build()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, CommandFactory, gameSetup, undoRedoManagerFactory)
      
      controller.processPlayerAction(SetPlayerCountAction(6))
      
      controller.gameState.setupPlayerCount shouldBe Some(6)
      controller.gameState.gamePhase shouldBe AskPlayerNamesPhase
    }
    
    "reject player count less than 2" in {
      val initialGameState = builderFactory.create()
        .withGamePhase(AskPlayerCountPhase)
        .build()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, CommandFactory, gameSetup, undoRedoManagerFactory)
      
      controller.processPlayerAction(SetPlayerCountAction(1))
      
      controller.gameState.lastEvent shouldBe Some(GameEvent.SetupError)
    }
    
    "reject player count greater than 6" in {
      val initialGameState = builderFactory.create()
        .withGamePhase(AskPlayerCountPhase)
        .build()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, CommandFactory, gameSetup, undoRedoManagerFactory)
      
      controller.processPlayerAction(SetPlayerCountAction(7))
      
      controller.gameState.lastEvent shouldBe Some(GameEvent.SetupError)
    }
  }

  "ControllerImpl with AddPlayerNameAction" should {
    "add first player name" in {
      val initialGameState = builderFactory.create()
        .withGamePhase(AskPlayerNamesPhase)
        .withSetupPlayerCount(Some(2))
        .build()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, CommandFactory, gameSetup, undoRedoManagerFactory)
      
      controller.processPlayerAction(AddPlayerNameAction("Alice"))
      
      controller.gameState.setupPlayerNames shouldBe List("Alice")
      controller.gameState.gamePhase shouldBe AskPlayerNamesPhase
    }
    
    "add all player names and transition to AskDeckSize" in {
      val initialGameState = builderFactory.create()
        .withGamePhase(AskPlayerNamesPhase)
        .withSetupPlayerCount(Some(2))
        .withSetupPlayerNames(List("Alice"))
        .build()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, CommandFactory, gameSetup, undoRedoManagerFactory)
      
      controller.processPlayerAction(AddPlayerNameAction("Bob"))
      
      controller.gameState.setupPlayerNames shouldBe List("Alice", "Bob")
      controller.gameState.gamePhase shouldBe AskDeckSizePhase
    }
    
    "reject empty player name" in {
      val initialGameState = builderFactory.create()
        .withGamePhase(AskPlayerNamesPhase)
        .withSetupPlayerCount(Some(2))
        .build()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, CommandFactory, gameSetup, undoRedoManagerFactory)
      
      controller.processPlayerAction(AddPlayerNameAction(""))
      
      controller.gameState.lastEvent shouldBe Some(GameEvent.SetupError)
    }
    
    "reject whitespace-only player name" in {
      val initialGameState = builderFactory.create()
        .withGamePhase(AskPlayerNamesPhase)
        .withSetupPlayerCount(Some(2))
        .build()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, CommandFactory, gameSetup, undoRedoManagerFactory)
      
      controller.processPlayerAction(AddPlayerNameAction("   "))
      
      controller.gameState.lastEvent shouldBe Some(GameEvent.SetupError)
    }
    
    "trim player name" in {
      val initialGameState = builderFactory.create()
        .withGamePhase(AskPlayerNamesPhase)
        .withSetupPlayerCount(Some(2))
        .build()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, CommandFactory, gameSetup, undoRedoManagerFactory)
      
      controller.processPlayerAction(AddPlayerNameAction("  Alice  "))
      
      controller.gameState.setupPlayerNames shouldBe List("Alice")
    }
  }

  "ControllerImpl with SetDeckSizeAction" should {
    "accept valid deck size" in {
      val initialGameState = builderFactory.create()
        .withGamePhase(AskDeckSizePhase)
        .withSetupPlayerCount(Some(2))
        .withSetupPlayerNames(List("Alice", "Bob"))
        .build()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, CommandFactory, gameSetup, undoRedoManagerFactory)
      
      controller.processPlayerAction(SetDeckSizeAction(36))
      
      controller.gameState.setupDeckSize shouldBe Some(36)
      controller.gameState.lastEvent shouldBe Some(GameEvent.GameSetupComplete)
    }
    
    "accept minimum deck size equal to player count" in {
      val initialGameState = builderFactory.create()
        .withGamePhase(AskDeckSizePhase)
        .withSetupPlayerCount(Some(3))
        .withSetupPlayerNames(List("Alice", "Bob", "Charlie"))
        .build()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, CommandFactory, gameSetup, undoRedoManagerFactory)
      
      controller.processPlayerAction(SetDeckSizeAction(20))
      
      controller.gameState.setupDeckSize shouldBe Some(20)
    }
    
    "reject deck size less than player count" in {
      val initialGameState = builderFactory.create()
        .withGamePhase(AskDeckSizePhase)
        .withSetupPlayerCount(Some(3))
        .withSetupPlayerNames(List("Alice", "Bob", "Charlie"))
        .build()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, CommandFactory, gameSetup, undoRedoManagerFactory)
      
      controller.processPlayerAction(SetDeckSizeAction(2))
      
      controller.gameState.lastEvent shouldBe Some(GameEvent.SetupError)
    }
    
    "reject deck size greater than 36" in {
      val initialGameState = builderFactory.create()
        .withGamePhase(AskDeckSizePhase)
        .withSetupPlayerCount(Some(2))
        .withSetupPlayerNames(List("Alice", "Bob"))
        .build()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, CommandFactory, gameSetup, undoRedoManagerFactory)
      
      controller.processPlayerAction(SetDeckSizeAction(50))
      
      controller.gameState.lastEvent shouldBe Some(GameEvent.SetupError)
    }
  }

  "ControllerImpl with PlayAgainAction" should {
    "restart game with same players" in {
      val initialGameState = builderFactory.create()
        .withGamePhase(AskPlayAgainPhase)
        .withSetupPlayerCount(Some(2))
        .withSetupPlayerNames(List("Alice", "Bob"))
        .withSetupDeckSize(Some(36))
        .build()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, CommandFactory, gameSetup, undoRedoManagerFactory)
      
      controller.processPlayerAction(PlayAgainAction)
      
      controller.gameState.setupPlayerNames shouldBe List("Alice", "Bob")
      controller.gameState.lastEvent shouldBe Some(GameEvent.GameSetupComplete)
    }
    
    "reset undo/redo manager on restart" in {
      val initialGameState = builderFactory.create()
        .withGamePhase(AskPlayAgainPhase)
        .withSetupPlayerCount(Some(2))
        .withSetupPlayerNames(List("Alice", "Bob"))
        .withSetupDeckSize(Some(36))
        .build()
      val undoRedoManager = undoRedoManagerFactory.create()
      val testCommand = CommandFactory.phaseChange()
      val managerWithHistory = undoRedoManager.save(testCommand, initialGameState)
      val controller = ControllerImpl(initialGameState, managerWithHistory, CommandFactory, gameSetup, undoRedoManagerFactory)
      
      controller.processPlayerAction(PlayAgainAction)
      
      controller.undoRedoManager.undoStack shouldBe empty
    }
    
    "handle ExitGameAction" in {
      val initialGameState = builderFactory.create()
        .withGamePhase(AskPlayAgainPhase)
        .withSetupPlayerCount(Some(2))
        .withSetupPlayerNames(List("Alice", "Bob"))
        .withSetupDeckSize(Some(36))
        .build()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, CommandFactory, gameSetup, undoRedoManagerFactory)
      
      controller.processPlayerAction(ExitGameAction)
      
      controller.gameState.lastEvent shouldBe Some(GameEvent.ExitApplication)
    }
    
    "reject invalid action in AskPlayAgainPhase" in {
      val initialGameState = builderFactory.create()
        .withGamePhase(AskPlayAgainPhase)
        .withSetupPlayerCount(Some(2))
        .withSetupPlayerNames(List("Alice", "Bob"))
        .withSetupDeckSize(Some(36))
        .build()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, CommandFactory, gameSetup, undoRedoManagerFactory)
      
      controller.processPlayerAction(InvalidAction)
      
      controller.gameState.lastEvent shouldBe Some(GameEvent.InvalidMove)
    }
  }

  "ControllerImpl undo functionality" should {
    "return CannotUndo when nothing to undo" in {
      val player1 = Player("Alice", List(Card(Suit.Hearts, Rank.Six)))
      val player2 = Player("Bob", List(Card(Suit.Diamonds, Rank.Seven)))
      val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)
      
      val initialGameState = GameState(
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
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, CommandFactory, gameSetup, undoRedoManagerFactory)
      
      val result = controller.undo()
      
      result shouldBe None
      controller.gameState.lastEvent shouldBe Some(GameEvent.CannotUndo)
    }
  }

  "ControllerImpl redo functionality" should {
    "return CannotRedo when nothing to redo" in {
      val player1 = Player("Alice", List(Card(Suit.Hearts, Rank.Six)))
      val player2 = Player("Bob", List(Card(Suit.Diamonds, Rank.Seven)))
      val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)
      
      val initialGameState = GameState(
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
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, CommandFactory, gameSetup, undoRedoManagerFactory)
      
      val result = controller.redo()
      
      result shouldBe None
      controller.gameState.lastEvent shouldBe Some(GameEvent.CannotRedo)
    }
  }

  "ControllerImpl with invalid actions in setup phase" should {
    "reject invalid action in SetupPhase" in {
      val initialGameState = builderFactory.create()
        .withGamePhase(SetupPhase)
        .build()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, CommandFactory, gameSetup, undoRedoManagerFactory)
      
      controller.processPlayerAction(PlayCardAction(Card(Suit.Hearts, Rank.Six)))
      
      controller.gameState.lastEvent shouldBe Some(GameEvent.SetupError)
    }
  }

  "ControllerImpl getStatusString" should {
    "return current game phase as string" in {
      val initialGameState = builderFactory.create()
        .withGamePhase(AttackPhase)
        .build()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, CommandFactory, gameSetup, undoRedoManagerFactory)
      
      val status = controller.getStatusString()
      
      status shouldBe "AttackPhase"
    }
  }
}
