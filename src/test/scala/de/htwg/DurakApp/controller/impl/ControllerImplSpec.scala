package de.htwg.DurakApp.controller.impl

import de.htwg.DurakApp.testutil.TestHelpers._
import de.htwg.DurakApp.testutil.{TestGamePhases, TestGamePhasesInstance}
import de.htwg.DurakApp.testutil.TestFactories

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Suit, Rank, GameState, Player}
import de.htwg.DurakApp.model.state._
import de.htwg.DurakApp.controller._
import de.htwg.DurakApp.controller.command.CommandFactory
import de.htwg.DurakApp.controller.command.impl.CommandFactoryImpl
import de.htwg.DurakApp.util.impl.{UndoRedoManagerFactoryImpl, UndoRedoManagerImpl}
import de.htwg.DurakApp.model.builder.impl.GameStateBuilder

class ControllerImplSpec extends AnyWordSpec with Matchers {

  def createBuilder() = GameStateBuilder(TestFactories.gameStateFactory, TestFactories.cardFactory, TestGamePhasesInstance)
  val gameSetup = new GameSetupImpl(TestFactories.gameStateFactory, TestFactories.playerFactory, TestFactories.cardFactory, TestGamePhasesInstance, TestFactories.gameStateBuilderFactory)
  val undoRedoManagerFactory = new UndoRedoManagerFactoryImpl()
  val commandFactory = new CommandFactoryImpl(TestGamePhasesInstance)

  "ControllerImpl with SetPlayerCountAction" should {
    "accept valid player count of 2" in {
      val initialGameState = createBuilder()
        .withGamePhase(TestGamePhases.askPlayerCountPhase)
        .build()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, commandFactory, gameSetup, undoRedoManagerFactory, de.htwg.DurakApp.testutil.TestGamePhasesInstance)
      
      controller.processPlayerAction(SetPlayerCountAction(2))
      
      controller.gameState.setupPlayerCount shouldBe Some(2)
      controller.gameState.gamePhase shouldBe TestGamePhases.askPlayerNamesPhase
    }
    
    "accept valid player count of 6" in {
      val initialGameState = createBuilder()
        .withGamePhase(TestGamePhases.askPlayerCountPhase)
        .build()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, commandFactory, gameSetup, undoRedoManagerFactory, de.htwg.DurakApp.testutil.TestGamePhasesInstance)
      
      controller.processPlayerAction(SetPlayerCountAction(6))
      
      controller.gameState.setupPlayerCount shouldBe Some(6)
      controller.gameState.gamePhase shouldBe TestGamePhases.askPlayerNamesPhase
    }
    
    "reject player count less than 2" in {
      val initialGameState = createBuilder()
        .withGamePhase(TestGamePhases.askPlayerCountPhase)
        .build()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, commandFactory, gameSetup, undoRedoManagerFactory, de.htwg.DurakApp.testutil.TestGamePhasesInstance)
      
      controller.processPlayerAction(SetPlayerCountAction(1))
      
      controller.gameState.lastEvent shouldBe Some(GameEvent.SetupError)
    }
    
    "reject player count greater than 6" in {
      val initialGameState = createBuilder()
        .withGamePhase(TestGamePhases.askPlayerCountPhase)
        .build()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, commandFactory, gameSetup, undoRedoManagerFactory, de.htwg.DurakApp.testutil.TestGamePhasesInstance)
      
      controller.processPlayerAction(SetPlayerCountAction(7))
      
      controller.gameState.lastEvent shouldBe Some(GameEvent.SetupError)
    }
  }

  "ControllerImpl with AddPlayerNameAction" should {
    "add first player name" in {
      val initialGameState = createBuilder()
        .withGamePhase(TestGamePhases.askPlayerNamesPhase)
        .withSetupPlayerCount(Some(2))
        .build()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, commandFactory, gameSetup, undoRedoManagerFactory, de.htwg.DurakApp.testutil.TestGamePhasesInstance)
      
      controller.processPlayerAction(AddPlayerNameAction("Alice"))
      
      controller.gameState.setupPlayerNames shouldBe List("Alice")
      controller.gameState.gamePhase shouldBe TestGamePhases.askPlayerNamesPhase
    }
    
    "add all player names and transition to AskDeckSize" in {
      val initialGameState = createBuilder()
        .withGamePhase(TestGamePhases.askPlayerNamesPhase)
        .withSetupPlayerCount(Some(2))
        .withSetupPlayerNames(List("Alice"))
        .build()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, commandFactory, gameSetup, undoRedoManagerFactory, de.htwg.DurakApp.testutil.TestGamePhasesInstance)
      
      controller.processPlayerAction(AddPlayerNameAction("Bob"))
      
      controller.gameState.setupPlayerNames shouldBe List("Alice", "Bob")
      controller.gameState.gamePhase shouldBe TestGamePhases.askDeckSizePhase
    }
    
    "reject empty player name" in {
      val initialGameState = createBuilder()
        .withGamePhase(TestGamePhases.askPlayerNamesPhase)
        .withSetupPlayerCount(Some(2))
        .build()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, commandFactory, gameSetup, undoRedoManagerFactory, de.htwg.DurakApp.testutil.TestGamePhasesInstance)
      
      controller.processPlayerAction(AddPlayerNameAction(""))
      
      controller.gameState.lastEvent shouldBe Some(GameEvent.SetupError)
    }
    
    "reject whitespace-only player name" in {
      val initialGameState = createBuilder()
        .withGamePhase(TestGamePhases.askPlayerNamesPhase)
        .withSetupPlayerCount(Some(2))
        .build()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, commandFactory, gameSetup, undoRedoManagerFactory, de.htwg.DurakApp.testutil.TestGamePhasesInstance)
      
      controller.processPlayerAction(AddPlayerNameAction("   "))
      
      controller.gameState.lastEvent shouldBe Some(GameEvent.SetupError)
    }
    
    "trim player name" in {
      val initialGameState = createBuilder()
        .withGamePhase(TestGamePhases.askPlayerNamesPhase)
        .withSetupPlayerCount(Some(2))
        .build()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, commandFactory, gameSetup, undoRedoManagerFactory, de.htwg.DurakApp.testutil.TestGamePhasesInstance)
      
      controller.processPlayerAction(AddPlayerNameAction("  Alice  "))
      
      controller.gameState.setupPlayerNames shouldBe List("Alice")
    }
  }

  "ControllerImpl with SetDeckSizeAction" should {
    "accept valid deck size" in {
      val initialGameState = createBuilder()
        .withGamePhase(TestGamePhases.askDeckSizePhase)
        .withSetupPlayerCount(Some(2))
        .withSetupPlayerNames(List("Alice", "Bob"))
        .build()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, commandFactory, gameSetup, undoRedoManagerFactory, de.htwg.DurakApp.testutil.TestGamePhasesInstance)
      
      controller.processPlayerAction(SetDeckSizeAction(36))
      
      controller.gameState.setupDeckSize shouldBe Some(36)
      controller.gameState.lastEvent shouldBe Some(GameEvent.GameSetupComplete)
    }
    
    "accept minimum deck size equal to player count" in {
      val initialGameState = createBuilder()
        .withGamePhase(TestGamePhases.askDeckSizePhase)
        .withSetupPlayerCount(Some(3))
        .withSetupPlayerNames(List("Alice", "Bob", "Charlie"))
        .build()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, commandFactory, gameSetup, undoRedoManagerFactory, de.htwg.DurakApp.testutil.TestGamePhasesInstance)
      
      controller.processPlayerAction(SetDeckSizeAction(20))
      
      controller.gameState.setupDeckSize shouldBe Some(20)
    }
    
    "reject deck size less than player count" in {
      val initialGameState = createBuilder()
        .withGamePhase(TestGamePhases.askDeckSizePhase)
        .withSetupPlayerCount(Some(3))
        .withSetupPlayerNames(List("Alice", "Bob", "Charlie"))
        .build()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, commandFactory, gameSetup, undoRedoManagerFactory, de.htwg.DurakApp.testutil.TestGamePhasesInstance)
      
      controller.processPlayerAction(SetDeckSizeAction(2))
      
      controller.gameState.lastEvent shouldBe Some(GameEvent.SetupError)
    }
    
    "reject deck size greater than 36" in {
      val initialGameState = createBuilder()
        .withGamePhase(TestGamePhases.askDeckSizePhase)
        .withSetupPlayerCount(Some(2))
        .withSetupPlayerNames(List("Alice", "Bob"))
        .build()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, commandFactory, gameSetup, undoRedoManagerFactory, de.htwg.DurakApp.testutil.TestGamePhasesInstance)
      
      controller.processPlayerAction(SetDeckSizeAction(50))
      
      controller.gameState.lastEvent shouldBe Some(GameEvent.SetupError)
    }
  }

  "ControllerImpl with PlayAgainAction" should {
    "restart game with same players" in {
      val initialGameState = createBuilder()
        .withGamePhase(TestGamePhases.askPlayAgainPhase)
        .withSetupPlayerCount(Some(2))
        .withSetupPlayerNames(List("Alice", "Bob"))
        .withSetupDeckSize(Some(36))
        .build()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, commandFactory, gameSetup, undoRedoManagerFactory, de.htwg.DurakApp.testutil.TestGamePhasesInstance)
      
      controller.processPlayerAction(PlayAgainAction)
      
      controller.gameState.setupPlayerNames shouldBe List("Alice", "Bob")
      controller.gameState.lastEvent shouldBe Some(GameEvent.GameSetupComplete)
    }
    
    "reset undo/redo manager on restart" in {
      val initialGameState = createBuilder()
        .withGamePhase(TestGamePhases.askPlayAgainPhase)
        .withSetupPlayerCount(Some(2))
        .withSetupPlayerNames(List("Alice", "Bob"))
        .withSetupDeckSize(Some(36))
        .build()
      val undoRedoManager = undoRedoManagerFactory.create()
      val testCommand = commandFactory.phaseChange()
      val managerWithHistory = undoRedoManager.save(testCommand, initialGameState)
      val controller = ControllerImpl(initialGameState, managerWithHistory, commandFactory, gameSetup, undoRedoManagerFactory, de.htwg.DurakApp.testutil.TestGamePhasesInstance)
      
      controller.processPlayerAction(PlayAgainAction)
      
    }
    
    "handle ExitGameAction" in {
      val initialGameState = createBuilder()
        .withGamePhase(TestGamePhases.askPlayAgainPhase)
        .withSetupPlayerCount(Some(2))
        .withSetupPlayerNames(List("Alice", "Bob"))
        .withSetupDeckSize(Some(36))
        .build()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, commandFactory, gameSetup, undoRedoManagerFactory, de.htwg.DurakApp.testutil.TestGamePhasesInstance)
      
      controller.processPlayerAction(ExitGameAction)
      
      controller.gameState.lastEvent shouldBe Some(GameEvent.ExitApplication)
    }
    
    "reject invalid action in TestGamePhases.askPlayAgainPhase" in {
      val initialGameState = createBuilder()
        .withGamePhase(TestGamePhases.askPlayAgainPhase)
        .withSetupPlayerCount(Some(2))
        .withSetupPlayerNames(List("Alice", "Bob"))
        .withSetupDeckSize(Some(36))
        .build()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, commandFactory, gameSetup, undoRedoManagerFactory, de.htwg.DurakApp.testutil.TestGamePhasesInstance)
      
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
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, commandFactory, gameSetup, undoRedoManagerFactory, de.htwg.DurakApp.testutil.TestGamePhasesInstance)
      
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
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, commandFactory, gameSetup, undoRedoManagerFactory, de.htwg.DurakApp.testutil.TestGamePhasesInstance)
      
      val result = controller.redo()
      
      result shouldBe None
      controller.gameState.lastEvent shouldBe Some(GameEvent.CannotRedo)
    }
  }

  "ControllerImpl with invalid actions in setup phase" should {
    "reject invalid action in TestGamePhases.setupPhase" in {
      val initialGameState = createBuilder()
        .withGamePhase(TestGamePhases.setupPhase)
        .build()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, commandFactory, gameSetup, undoRedoManagerFactory, de.htwg.DurakApp.testutil.TestGamePhasesInstance)
      
      controller.processPlayerAction(PlayCardAction(Card(Suit.Hearts, Rank.Six)))
      
      controller.gameState.lastEvent shouldBe Some(GameEvent.SetupError)
    }
  }

  "ControllerImpl getStatusString" should {
    "return current game phase as string" in {
      val initialGameState = createBuilder()
        .withGamePhase(TestGamePhases.attackPhase)
        .build()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(initialGameState, undoRedoManager, commandFactory, gameSetup, undoRedoManagerFactory, de.htwg.DurakApp.testutil.TestGamePhasesInstance)
      
      val status = controller.getStatusString()
      
      status shouldBe "AttackPhase"
    }
  }
}
