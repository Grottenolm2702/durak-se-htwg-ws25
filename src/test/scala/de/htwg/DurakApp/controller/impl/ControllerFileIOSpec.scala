package de.htwg.DurakApp.controller.impl

import de.htwg.DurakApp.testutil._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Suit, Rank, GameState, Player}
import de.htwg.DurakApp.model.state._
import de.htwg.DurakApp.controller._
import de.htwg.DurakApp.controller.command.CommandFactory
import de.htwg.DurakApp.util.{UndoRedoManagerFactory, FileIOInterface}
import de.htwg.DurakApp.model.builder.GameStateBuilderFactory
import scala.util.{Try, Success, Failure}

class ControllerFileIOSpec extends AnyWordSpec with Matchers {
  val gameStateBuilderFactory: GameStateBuilderFactory =
    new StubGameStateBuilderFactory()
  val gameSetup: GameSetup =
    new StubGameSetup()
  val undoRedoManagerFactory: UndoRedoManagerFactory =
    new StubUndoRedoManagerFactory()
  val commandFactory: CommandFactory = new StubCommandFactory()
  val stubGamePhases = new StubGamePhasesImpl()

  def createBuilder() = gameStateBuilderFactory.create()

  class MockFileIO extends FileIOInterface {
    var savedState: Option[GameState] = None
    var loadResult: Try[GameState] = Failure(new Exception("No state loaded"))
    var saveResult: Try[Unit] = Success(())

    override def save(gameState: GameState): Try[Unit] = {
      savedState = Some(gameState)
      saveResult
    }

    override def load(): Try[GameState] = loadResult
  }

  "ControllerImpl with SaveGameAction" should {
    "save game state successfully" in {
      val trumpCard = Card(Suit.Hearts, Rank.Six, isTrump = true)
      val player1 = Player("Alice", List.empty, isDone = false)
      val player2 = Player("Bob", List.empty, isDone = false)
      val initialGameState = createBuilder()
        .withPlayers(List(player1, player2))
        .withTrumpCard(trumpCard)
        .withGamePhase(StubGamePhases.attackPhase)
        .build()
        .get

      val mockFileIO = new MockFileIO()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = new ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        mockFileIO
      )

      val result = controller.processPlayerAction(SaveGameAction)

      result.lastEvent shouldBe Some(GameEvent.GameSaved)
      mockFileIO.savedState shouldBe defined
      mockFileIO.savedState.get.players should contain theSameElementsAs List(
        player1,
        player2
      )
    }

    "handle save error gracefully" in {
      val initialGameState = createBuilder()
        .withGamePhase(StubGamePhases.attackPhase)
        .build()
        .get

      val mockFileIO = new MockFileIO()
      mockFileIO.saveResult = Failure(new Exception("Save failed"))

      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = new ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        mockFileIO
      )

      val result = controller.processPlayerAction(SaveGameAction)

      result.lastEvent shouldBe Some(GameEvent.SaveError)
    }

    "save during setup phase" in {
      val initialGameState = createBuilder()
        .withGamePhase(StubGamePhases.setupPhase)
        .build()
        .get

      val mockFileIO = new MockFileIO()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = new ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        mockFileIO
      )

      val result = controller.processPlayerAction(SaveGameAction)

      result.lastEvent shouldBe Some(GameEvent.GameSaved)
    }

    "save during ask play again phase" in {
      val initialGameState = createBuilder()
        .withGamePhase(StubGamePhases.askPlayAgainPhase)
        .build()
        .get

      val mockFileIO = new MockFileIO()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = new ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        mockFileIO
      )

      val result = controller.processPlayerAction(SaveGameAction)

      result.lastEvent shouldBe Some(GameEvent.GameSaved)
    }
  }

  "ControllerImpl with LoadGameAction" should {
    "load game state successfully" in {
      val trumpCard = Card(Suit.Spades, Rank.Seven, isTrump = true)
      val player1 = Player("Charlie", List.empty, isDone = false)
      val player2 = Player("Dave", List.empty, isDone = false)
      val savedGameState = createBuilder()
        .withPlayers(List(player1, player2))
        .withTrumpCard(trumpCard)
        .withGamePhase(StubGamePhases.defensePhase)
        .build()
        .get

      val mockFileIO = new MockFileIO()
      mockFileIO.loadResult = Success(savedGameState)

      val initialGameState = createBuilder()
        .withGamePhase(StubGamePhases.attackPhase)
        .build()
        .get

      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = new ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        mockFileIO
      )

      val result = controller.processPlayerAction(LoadGameAction)

      result.lastEvent shouldBe Some(GameEvent.GameLoaded)
      result.players should contain theSameElementsAs List(player1, player2)
      result.gamePhase shouldBe StubGamePhases.defensePhase
      result.trumpCard shouldBe trumpCard
    }

    "handle load error gracefully" in {
      val initialGameState = createBuilder()
        .withGamePhase(StubGamePhases.attackPhase)
        .build()
        .get

      val mockFileIO = new MockFileIO()
      mockFileIO.loadResult = Failure(new Exception("Load failed"))

      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = new ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        mockFileIO
      )

      val result = controller.processPlayerAction(LoadGameAction)

      result.lastEvent shouldBe Some(GameEvent.LoadError)
      result.gamePhase shouldBe initialGameState.gamePhase
    }

    "reset undo/redo manager after successful load" in {
      val savedGameState = createBuilder()
        .withGamePhase(StubGamePhases.defensePhase)
        .build()
        .get

      val mockFileIO = new MockFileIO()
      mockFileIO.loadResult = Success(savedGameState)

      val initialGameState = createBuilder()
        .withGamePhase(StubGamePhases.attackPhase)
        .build()
        .get

      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = new ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        mockFileIO
      )

      controller.processPlayerAction(LoadGameAction)

      val undoResult = controller.undo()
      undoResult shouldBe None
    }

    "load during setup phase" in {
      val savedGameState = createBuilder()
        .withGamePhase(StubGamePhases.attackPhase)
        .build()
        .get

      val mockFileIO = new MockFileIO()
      mockFileIO.loadResult = Success(savedGameState)

      val initialGameState = createBuilder()
        .withGamePhase(StubGamePhases.setupPhase)
        .build()
        .get

      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = new ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        mockFileIO
      )

      val result = controller.processPlayerAction(LoadGameAction)

      result.lastEvent shouldBe Some(GameEvent.GameLoaded)
      result.gamePhase shouldBe StubGamePhases.attackPhase
    }
  }

  "ControllerImpl saveGame method" should {
    "directly call saveGame and return updated state" in {
      val initialGameState = createBuilder()
        .withGamePhase(StubGamePhases.attackPhase)
        .build()
        .get

      val mockFileIO = new MockFileIO()
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = new ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        mockFileIO
      )

      val result = controller.saveGame()

      result.lastEvent shouldBe Some(GameEvent.GameSaved)
    }

    "handle save failure" in {
      val initialGameState = createBuilder()
        .withGamePhase(StubGamePhases.attackPhase)
        .build()
        .get

      val mockFileIO = new MockFileIO()
      mockFileIO.saveResult = Failure(new Exception("IO Error"))

      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = new ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        mockFileIO
      )

      val result = controller.saveGame()

      result.lastEvent shouldBe Some(GameEvent.SaveError)
    }
  }

  "ControllerImpl loadGame method" should {
    "directly call loadGame and return loaded state" in {
      val savedGameState = createBuilder()
        .withGamePhase(StubGamePhases.defensePhase)
        .build()
        .get

      val mockFileIO = new MockFileIO()
      mockFileIO.loadResult = Success(savedGameState)

      val initialGameState = createBuilder()
        .withGamePhase(StubGamePhases.attackPhase)
        .build()
        .get

      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = new ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        mockFileIO
      )

      val result = controller.loadGame()

      result.lastEvent shouldBe Some(GameEvent.GameLoaded)
      result.gamePhase shouldBe StubGamePhases.defensePhase
    }

    "handle load failure" in {
      val initialGameState = createBuilder()
        .withGamePhase(StubGamePhases.attackPhase)
        .build()
        .get

      val mockFileIO = new MockFileIO()
      mockFileIO.loadResult = Failure(new Exception("File not found"))

      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = new ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        mockFileIO
      )

      val result = controller.loadGame()

      result.lastEvent shouldBe Some(GameEvent.LoadError)
    }

    "restore redo stack after load" in {
      val trumpCard = Card(Suit.Hearts, Rank.Six, isTrump = true)
      val player1 = Player("Alice", List.empty, isDone = false)
      val player2 = Player("Bob", List.empty, isDone = false)
      
      val gameState1 = createBuilder()
        .withPlayers(List(player1, player2))
        .withTrumpCard(trumpCard)
        .withMainAttackerIndex(0)
        .withGamePhase(StubGamePhases.attackPhase)
        .build()
        .get
      
      val gameState2 = gameState1.copy(mainAttackerIndex = 1)
      val gameState3 = gameState1.copy(mainAttackerIndex = 2)
      
      val stateWithStacks = gameState1.copy(
        undoStack = List(gameState2),
        redoStack = List(gameState3)
      )
      
      val mockFileIO = new MockFileIO()
      mockFileIO.loadResult = Success(stateWithStacks)
      
      // Use real UndoRedoManagerFactory to test the actual behavior
      import de.htwg.DurakApp.util.impl.UndoRedoManagerFactoryImpl
      val realFactory = new UndoRedoManagerFactoryImpl()
      
      val controller = new ControllerImpl(
        gameState1,
        undoRedoManager = realFactory.create(),
        commandFactory,
        gameSetup,
        realFactory,
        stubGamePhases,
        mockFileIO
      )
      
      val loadedState = controller.loadGame()
      
      // Verify state was loaded
      loadedState.lastEvent shouldBe Some(GameEvent.GameLoaded)
      
      // Perform undo - should work because undoStack was restored
      val undoResult = controller.undo()
      undoResult shouldBe defined
      
      // Perform redo - should now work (this was broken before the fix)
      val redoResult = controller.redo()
      redoResult shouldBe defined
    }
  }
}
