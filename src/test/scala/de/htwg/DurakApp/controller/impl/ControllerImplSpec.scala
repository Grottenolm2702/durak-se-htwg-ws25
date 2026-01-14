package de.htwg.DurakApp.controller.impl
import de.htwg.DurakApp.testutil._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Suit, Rank, GameState, Player}
import de.htwg.DurakApp.model.state._
import de.htwg.DurakApp.controller._
import de.htwg.DurakApp.controller.command.{CommandFactory, PlayCardCommand}
import de.htwg.DurakApp.util.UndoRedoManagerFactory
import de.htwg.DurakApp.model.builder.GameStateBuilderFactory
class ControllerImplSpec extends AnyWordSpec with Matchers {
  val gameStateBuilderFactory: GameStateBuilderFactory =
    new StubGameStateBuilderFactory()
  val gameSetup: GameSetup =
    new StubGameSetup()
  val undoRedoManagerFactory: UndoRedoManagerFactory =
    new StubUndoRedoManagerFactory()
  val commandFactory: CommandFactory = new StubCommandFactory()
  val stubGamePhases = new StubGamePhasesImpl()
  val stubFileIO = new StubFileIO()
  def createBuilder() = gameStateBuilderFactory.create()
  "ControllerImpl with SetPlayerCountAction" should {
    "accept valid player count of 2" in {
      val initialGameState = createBuilder()
        .withGamePhase(StubGamePhases.askPlayerCountPhase)
        .build()
        .get
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        stubFileIO
      )
      controller.processPlayerAction(SetPlayerCountAction(2))
      controller.gameState.setupPlayerCount shouldBe Some(2)
      controller.gameState.gamePhase shouldBe StubGamePhases.askPlayerNamesPhase
    }
    "accept valid player count of 6" in {
      val initialGameState = createBuilder()
        .withGamePhase(StubGamePhases.askPlayerCountPhase)
        .build()
        .get
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        stubFileIO
      )
      controller.processPlayerAction(SetPlayerCountAction(6))
      controller.gameState.setupPlayerCount shouldBe Some(6)
      controller.gameState.gamePhase shouldBe StubGamePhases.askPlayerNamesPhase
    }
    "reject player count less than 2" in {
      val initialGameState = createBuilder()
        .withGamePhase(StubGamePhases.askPlayerCountPhase)
        .build()
        .get
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        stubFileIO
      )
      controller.processPlayerAction(SetPlayerCountAction(1))
      controller.gameState.lastEvent shouldBe Some(GameEvent.SetupError)
    }
    "reject player count greater than 6" in {
      val initialGameState = createBuilder()
        .withGamePhase(StubGamePhases.askPlayerCountPhase)
        .build()
        .get
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        stubFileIO
      )
      controller.processPlayerAction(SetPlayerCountAction(7))
      controller.gameState.lastEvent shouldBe Some(GameEvent.SetupError)
    }
  }
  "ControllerImpl with AddPlayerNameAction" should {
    "add first player name" in {
      val initialGameState = createBuilder()
        .withGamePhase(StubGamePhases.askPlayerNamesPhase)
        .withSetupPlayerCount(Some(2))
        .build()
        .get
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        stubFileIO
      )
      controller.processPlayerAction(AddPlayerNameAction("Alice"))
      controller.gameState.setupPlayerNames shouldBe List("Alice")
      controller.gameState.gamePhase shouldBe StubGamePhases.askPlayerNamesPhase
    }
    "add all player names and transition to AskDeckSize" in {
      val initialGameState = createBuilder()
        .withGamePhase(StubGamePhases.askPlayerNamesPhase)
        .withSetupPlayerCount(Some(2))
        .withSetupPlayerNames(List("Alice"))
        .build()
        .get
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        stubFileIO
      )
      controller.processPlayerAction(AddPlayerNameAction("Bob"))
      controller.gameState.setupPlayerNames shouldBe List("Alice", "Bob")
      controller.gameState.gamePhase shouldBe StubGamePhases.askDeckSizePhase
    }
    "reject empty player name" in {
      val initialGameState = createBuilder()
        .withGamePhase(StubGamePhases.askPlayerNamesPhase)
        .withSetupPlayerCount(Some(2))
        .build()
        .get
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        stubFileIO
      )
      controller.processPlayerAction(AddPlayerNameAction(""))
      controller.gameState.lastEvent shouldBe Some(GameEvent.SetupError)
    }
    "reject whitespace-only player name" in {
      val initialGameState = createBuilder()
        .withGamePhase(StubGamePhases.askPlayerNamesPhase)
        .withSetupPlayerCount(Some(2))
        .build()
        .get
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        stubFileIO
      )
      controller.processPlayerAction(AddPlayerNameAction("   "))
      controller.gameState.lastEvent shouldBe Some(GameEvent.SetupError)
    }
    "trim player name" in {
      val initialGameState = createBuilder()
        .withGamePhase(StubGamePhases.askPlayerNamesPhase)
        .withSetupPlayerCount(Some(2))
        .build()
        .get
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        stubFileIO
      )
      controller.processPlayerAction(AddPlayerNameAction("  Alice  "))
      controller.gameState.setupPlayerNames shouldBe List("Alice")
    }
    "reject adding more names than expected count" in {
      val initialGameState = createBuilder()
        .withGamePhase(StubGamePhases.askPlayerNamesPhase)
        .withSetupPlayerCount(Some(2))
        .withSetupPlayerNames(List("Alice", "Bob"))
        .build()
        .get
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        stubFileIO
      )
      controller.processPlayerAction(AddPlayerNameAction("Charlie"))
      controller.gameState.lastEvent shouldBe Some(GameEvent.SetupError)
    }
  }
  "ControllerImpl with SetDeckSizeAction" should {
    "accept valid deck size" in {
      val initialGameState = createBuilder()
        .withGamePhase(StubGamePhases.askDeckSizePhase)
        .withSetupPlayerCount(Some(2))
        .withSetupPlayerNames(List("Alice", "Bob"))
        .build()
        .get
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        stubFileIO
      )
      controller.processPlayerAction(SetDeckSizeAction(36))
      controller.gameState.setupDeckSize shouldBe Some(36)
      controller.gameState.lastEvent shouldBe Some(GameEvent.GameSetupComplete)
    }
    "accept minimum deck size equal to player count" in {
      val initialGameState = createBuilder()
        .withGamePhase(StubGamePhases.askDeckSizePhase)
        .withSetupPlayerCount(Some(3))
        .withSetupPlayerNames(List("Alice", "Bob", "Charlie"))
        .build()
        .get
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        stubFileIO
      )
      controller.processPlayerAction(SetDeckSizeAction(20))
      controller.gameState.setupDeckSize shouldBe Some(20)
    }
    "reject deck size less than player count" in {
      val initialGameState = createBuilder()
        .withGamePhase(StubGamePhases.askDeckSizePhase)
        .withSetupPlayerCount(Some(3))
        .withSetupPlayerNames(List("Alice", "Bob", "Charlie"))
        .build()
        .get
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        stubFileIO
      )
      controller.processPlayerAction(SetDeckSizeAction(2))
      controller.gameState.lastEvent shouldBe Some(GameEvent.SetupError)
    }
    "reject deck size greater than 36" in {
      val initialGameState = createBuilder()
        .withGamePhase(StubGamePhases.askDeckSizePhase)
        .withSetupPlayerCount(Some(2))
        .withSetupPlayerNames(List("Alice", "Bob"))
        .build()
        .get
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        stubFileIO
      )
      controller.processPlayerAction(SetDeckSizeAction(50))
      controller.gameState.lastEvent shouldBe Some(GameEvent.SetupError)
    }
    "handle gameSetup failure when setupGame returns None" in {
      val failingGameSetup = new GameSetup {
        def setupGame(
            playerNames: List[String],
            deckSize: Int
        ): Option[GameState] = None
      }
      val initialGameState = createBuilder()
        .withGamePhase(StubGamePhases.askDeckSizePhase)
        .withSetupPlayerCount(Some(2))
        .withSetupPlayerNames(List("Alice", "Bob"))
        .build()
        .get
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        failingGameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        stubFileIO
      )
      controller.processPlayerAction(SetDeckSizeAction(20))
      controller.gameState.lastEvent shouldBe Some(GameEvent.SetupError)
    }
  }
  "ControllerImpl with PlayAgainAction" should {
    "restart game with same players" in {
      val initialGameState = createBuilder()
        .withGamePhase(StubGamePhases.askPlayAgainPhase)
        .withSetupPlayerCount(Some(2))
        .withSetupPlayerNames(List("Alice", "Bob"))
        .withSetupDeckSize(Some(36))
        .build()
        .get
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        stubFileIO
      )
      controller.processPlayerAction(PlayAgainAction)
      controller.gameState.setupPlayerNames shouldBe List("Alice", "Bob")
      controller.gameState.lastEvent shouldBe Some(GameEvent.GameSetupComplete)
    }
    "reset undo/redo manager on restart" in {
      val initialGameState = createBuilder()
        .withGamePhase(StubGamePhases.askPlayAgainPhase)
        .withSetupPlayerCount(Some(2))
        .withSetupPlayerNames(List("Alice", "Bob"))
        .withSetupDeckSize(Some(36))
        .build()
        .get
      val undoRedoManager = undoRedoManagerFactory.create()
      val testCommand = commandFactory.phaseChange()
      val managerWithHistory =
        undoRedoManager.save(testCommand, initialGameState)
      val controller = ControllerImpl(
        initialGameState,
        managerWithHistory,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        stubFileIO
      )
      controller.processPlayerAction(PlayAgainAction)
    }
    "handle ExitGameAction" in {
      val initialGameState = createBuilder()
        .withGamePhase(StubGamePhases.askPlayAgainPhase)
        .withSetupPlayerCount(Some(2))
        .withSetupPlayerNames(List("Alice", "Bob"))
        .withSetupDeckSize(Some(36))
        .build()
        .get
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        stubFileIO
      )
      controller.processPlayerAction(ExitGameAction)
      controller.gameState.lastEvent shouldBe Some(GameEvent.ExitApplication)
    }
    "reject invalid action in StubGamePhases.askPlayAgainPhase" in {
      val initialGameState = createBuilder()
        .withGamePhase(StubGamePhases.askPlayAgainPhase)
        .withSetupPlayerCount(Some(2))
        .withSetupPlayerNames(List("Alice", "Bob"))
        .withSetupDeckSize(Some(36))
        .build()
        .get
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        stubFileIO
      )
      controller.processPlayerAction(InvalidAction)
      controller.gameState.lastEvent shouldBe Some(GameEvent.InvalidMove)
    }
    "handle PlayAgain when setupGame fails" in {
      val failingGameSetup = new GameSetup {
        def setupGame(
            playerNames: List[String],
            deckSize: Int
        ): Option[GameState] = None
      }
      val initialGameState = createBuilder()
        .withGamePhase(StubGamePhases.askPlayAgainPhase)
        .withSetupPlayerCount(Some(2))
        .withSetupPlayerNames(List("Alice", "Bob"))
        .withSetupDeckSize(Some(36))
        .build()
        .get
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        failingGameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        stubFileIO
      )
      controller.processPlayerAction(PlayAgainAction)
      controller.gameState.lastEvent shouldBe Some(GameEvent.SetupError)
    }
  }
  "ControllerImpl undo functionality" should {
    "return CannotUndo when nothing to undo" in {
      val player1 =
        TestHelper.Player("Alice", List(TestHelper.Card(Suit.Hearts, Rank.Six)))
      val player2 = TestHelper.Player(
        "Bob",
        List(TestHelper.Card(Suit.Diamonds, Rank.Seven))
      )
      val trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val initialGameState = TestHelper.GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        mainAttackerIndex = 0,
        defenderIndex = 1,
        gamePhase = StubGamePhases.setupPhase,
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
      val controller = ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        stubFileIO
      )
      val result = controller.undo()
      result shouldBe None
      controller.gameState.lastEvent shouldBe Some(GameEvent.CannotUndo)
    }
    "undo successfully when history exists" in {
      val player1 =
        TestHelper.Player("Alice", List(TestHelper.Card(Suit.Hearts, Rank.Six)))
      val player2 = TestHelper.Player(
        "Bob",
        List(TestHelper.Card(Suit.Diamonds, Rank.Seven))
      )
      val trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val currentGameState = TestHelper.GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        mainAttackerIndex = 0,
        defenderIndex = 1,
        gamePhase = StubGamePhases.setupPhase,
        lastEvent = Some(GameEvent.Pass),
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      val previousGameState =
        currentGameState.copy(lastEvent = Some(GameEvent.Draw))
      val testCommand = commandFactory.phaseChange()
      val managerWithHistory =
        undoRedoManagerFactory.create().save(testCommand, previousGameState)
      val controller = ControllerImpl(
        currentGameState,
        managerWithHistory,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        stubFileIO
      )
      val result = controller.undo()
      result shouldBe defined
      result.get.lastEvent shouldBe Some(GameEvent.Draw)
    }
  }
  "ControllerImpl redo functionality" should {
    "return CannotRedo when nothing to redo" in {
      val player1 =
        TestHelper.Player("Alice", List(TestHelper.Card(Suit.Hearts, Rank.Six)))
      val player2 = TestHelper.Player(
        "Bob",
        List(TestHelper.Card(Suit.Diamonds, Rank.Seven))
      )
      val trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val initialGameState = TestHelper.GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        mainAttackerIndex = 0,
        defenderIndex = 1,
        gamePhase = StubGamePhases.setupPhase,
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
      val controller = ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        stubFileIO
      )
      val result = controller.redo()
      result shouldBe None
      controller.gameState.lastEvent shouldBe Some(GameEvent.CannotRedo)
    }
    "redo successfully when redo stack exists" in {
      val player1 =
        TestHelper.Player("Alice", List(TestHelper.Card(Suit.Hearts, Rank.Six)))
      val player2 = TestHelper.Player(
        "Bob",
        List(TestHelper.Card(Suit.Diamonds, Rank.Seven))
      )
      val trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val currentGameState = TestHelper.GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        mainAttackerIndex = 0,
        defenderIndex = 1,
        gamePhase = StubGamePhases.setupPhase,
        lastEvent = Some(GameEvent.Pass),
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      val nextGameState =
        currentGameState.copy(lastEvent = Some(GameEvent.Draw))
      val testCommand = commandFactory.phaseChange()
      val managerWithRedo = new StubUndoRedoManager(
        undoStack = List.empty,
        redoStack = List((testCommand, nextGameState))
      )
      val controller = ControllerImpl(
        currentGameState,
        managerWithRedo,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        stubFileIO
      )
      val result = controller.redo()
      result shouldBe defined
      result.get.lastEvent shouldBe Some(GameEvent.Draw)
    }
  }
  "ControllerImpl with invalid actions in setup phase" should {
    "reject invalid action in StubGamePhases.setupPhase" in {
      val initialGameState = createBuilder()
        .withGamePhase(StubGamePhases.setupPhase)
        .build()
        .get
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        stubFileIO
      )
      controller.processPlayerAction(
        PlayCardAction(TestHelper.Card(Suit.Hearts, Rank.Six))
      )
      controller.gameState.lastEvent shouldBe Some(GameEvent.SetupError)
    }
  }
  "ControllerImpl getStatusString" should {
    "return current game phase as string" in {
      val initialGameState = createBuilder()
        .withGamePhase(StubGamePhases.attackPhase)
        .build()
        .get
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        stubFileIO
      )
      val status = controller.getStatusString()
      status shouldBe "AttackPhase"
    }
  }
  "ControllerImpl processGameAction" should {
    "call processGameAction for PlayCardAction in attack phase" in {
      val card = TestHelper.Card(Suit.Hearts, Rank.Six)
      val player1 = TestHelper.Player("Alice", List(card))
      val player2 = TestHelper.Player("Bob", List.empty)
      val initialGameState = TestHelper.GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true),
        mainAttackerIndex = 0,
        defenderIndex = 1,
        gamePhase = StubGamePhases.attackPhase,
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
      val controller = ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        stubFileIO
      )
      val result = controller.processPlayerAction(PlayCardAction(card))
      result.gamePhase shouldBe StubGamePhases.attackPhase
    }
    "call processGameAction for PassAction in attack phase" in {
      val card = TestHelper.Card(Suit.Hearts, Rank.Six)
      val player1 = TestHelper.Player("Alice", List(card))
      val player2 = TestHelper.Player("Bob", List.empty)
      val initialGameState = TestHelper.GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map(card -> None),
        discardPile = List.empty,
        trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true),
        mainAttackerIndex = 0,
        defenderIndex = 1,
        gamePhase = StubGamePhases.attackPhase,
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
      val controller = ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        stubFileIO
      )
      val result = controller.processPlayerAction(PassAction)
      result.gamePhase shouldBe StubGamePhases.attackPhase
    }
    "call processGameAction for TakeCardsAction in defense phase" in {
      val card = TestHelper.Card(Suit.Hearts, Rank.Six)
      val player1 = TestHelper.Player("Alice", List.empty)
      val player2 = TestHelper.Player("Bob", List.empty)
      val initialGameState = TestHelper.GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map(card -> None),
        discardPile = List.empty,
        trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true),
        mainAttackerIndex = 0,
        defenderIndex = 1,
        gamePhase = StubGamePhases.defensePhase,
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
      val controller = ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        stubFileIO
      )
      val result = controller.processPlayerAction(TakeCardsAction)
      result.gamePhase shouldBe StubGamePhases.defensePhase
    }
    "handle command error when createCommand returns Left" in {
      val player1 = TestHelper.Player("Alice", List.empty)
      val player2 = TestHelper.Player("Bob", List.empty)
      val initialGameState = TestHelper.GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true),
        mainAttackerIndex = 0,
        defenderIndex = 1,
        gamePhase = StubGamePhases.attackPhase,
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
      val controller = ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        stubFileIO
      )
      val result = controller.processPlayerAction(InvalidAction)
      result.lastEvent shouldBe Some(GameEvent.InvalidMove)
    }
    "handle SetPlayerCountAction in game phase returns error" in {
      val player1 = TestHelper.Player("Alice", List.empty)
      val player2 = TestHelper.Player("Bob", List.empty)
      val initialGameState = TestHelper.GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true),
        mainAttackerIndex = 0,
        defenderIndex = 1,
        gamePhase = StubGamePhases.attackPhase,
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
      val controller = ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactory,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        stubFileIO
      )
      val result = controller.processPlayerAction(SetPlayerCountAction(2))
      result.lastEvent shouldBe Some(GameEvent.InvalidMove)
    }
    "handle phase transitions recursively" in {
      val card = TestHelper.Card(Suit.Hearts, Rank.Six)
      val player1 = TestHelper.Player("Alice", List(card))
      val player2 = TestHelper.Player("Bob", List.empty)
      val transitionPhase = new StubGamePhaseWithTransition(
        "TransitionPhase",
        StubGamePhases.attackPhase
      )
      val initialGameState = TestHelper.GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true),
        mainAttackerIndex = 0,
        defenderIndex = 1,
        gamePhase = transitionPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      val commandFactoryWithTransition = new StubCommandFactory {
        override def playCard(cardValue: Card): PlayCardCommand =
          new PlayCardCommand:
            val card: Card = cardValue
            def execute(state: GameState): GameState =
              state.copy(gamePhase = transitionPhase)
            def undo(state: GameState): GameState = state
      }
      val undoRedoManager = undoRedoManagerFactory.create()
      val controller = ControllerImpl(
        initialGameState,
        undoRedoManager,
        commandFactoryWithTransition,
        gameSetup,
        undoRedoManagerFactory,
        stubGamePhases,
        stubFileIO
      )
      val result = controller.processPlayerAction(PlayCardAction(card))
      result.gamePhase shouldBe StubGamePhases.attackPhase
      result.lastEvent shouldBe Some(GameEvent.Draw)
    }
  }
}
