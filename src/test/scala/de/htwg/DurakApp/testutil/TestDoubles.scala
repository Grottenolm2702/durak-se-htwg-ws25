package de.htwg.DurakApp.testutil

import de.htwg.DurakApp.controller.{Controller, GameSetup, PlayerAction}
import de.htwg.DurakApp.controller.command.GameCommand
import de.htwg.DurakApp.model.{
  GameState,
  Card,
  Player,
  Suit,
  Rank,
  PlayerFactory,
  GameStateFactory,
  CardFactory
}
import de.htwg.DurakApp.model.builder.GameStateBuilderFactory
import de.htwg.DurakApp.model.state.{GamePhase, GameEvent, GamePhases}
import de.htwg.DurakApp.model.state.impl.*
import de.htwg.DurakApp.util.{UndoRedoManager, Observer}

// ============================================================
// EXISTING Test helpers (keeping compatibility)
// ============================================================

// Test helpers for creating model instances
object TestFactories:
  // Use Guice injector for factories instead of direct instantiation
  private lazy val injector =
    com.google.inject.Guice.createInjector(new de.htwg.DurakApp.DurakModule)

  val cardFactory: CardFactory = injector.getInstance(classOf[CardFactory])
  val playerFactory: PlayerFactory =
    injector.getInstance(classOf[PlayerFactory])
  val gameStateFactory: GameStateFactory =
    injector.getInstance(classOf[GameStateFactory])
  val gameStateBuilderFactory: GameStateBuilderFactory =
    injector.getInstance(classOf[GameStateBuilderFactory])

object TestGamePhases:
  val setupPhase: GamePhase = SetupPhaseImpl
  val askPlayerCountPhase: GamePhase = AskPlayerCountPhaseImpl
  val askPlayerNamesPhase: GamePhase = AskPlayerNamesPhaseImpl
  val askDeckSizePhase: GamePhase = AskDeckSizePhaseImpl
  val askPlayAgainPhase: GamePhase = AskPlayAgainPhaseImpl
  val gameStartPhase: GamePhase = GameStartPhaseImpl
  val attackPhase: GamePhase = AttackPhaseImpl
  val defensePhase: GamePhase = DefensePhaseImpl
  val drawPhase: GamePhase = DrawPhaseImpl
  val roundPhase: GamePhase = RoundPhaseImpl
  val endPhase: GamePhase = EndPhaseImpl

object TestGamePhasesInstance extends GamePhases {
  def setupPhase = TestGamePhases.setupPhase
  def askPlayerCountPhase = TestGamePhases.askPlayerCountPhase
  def askPlayerNamesPhase = TestGamePhases.askPlayerNamesPhase
  def askDeckSizePhase = TestGamePhases.askDeckSizePhase
  def askPlayAgainPhase = TestGamePhases.askPlayAgainPhase
  def gameStartPhase = TestGamePhases.gameStartPhase
  def attackPhase = TestGamePhases.attackPhase
  def defensePhase = TestGamePhases.defensePhase
  def drawPhase = TestGamePhases.drawPhase
  def roundPhase = TestGamePhases.roundPhase
  def endPhase = TestGamePhases.endPhase
}

// ============================================================
// STUBS - keeping existing ones for compatibility
// ============================================================

class StubGameSetup extends GameSetup:
  private val cardFactory = TestFactories.cardFactory
  private val playerFactory = TestFactories.playerFactory
  private val gameStateFactory = TestFactories.gameStateFactory

  def setupGame(playerNames: List[String], deckSize: Int): Option[GameState] =
    if playerNames.size < 2 || playerNames.size > 6 || deckSize < 2 || deckSize > 36
    then None
    else
      val players = playerNames.map(name => playerFactory(name, List.empty))
      val trumpCard = cardFactory(Suit.Hearts, Rank.Six, isTrump = true)
      Some(
        gameStateFactory(
          players = players,
          deck = List.empty,
          table = Map.empty,
          discardPile = List.empty,
          trumpCard = trumpCard,
          attackerIndex = 0,
          defenderIndex = 1,
          gamePhase = TestGamePhases.setupPhase,
          lastEvent = Some(GameEvent.GameSetupComplete),
          passedPlayers = Set.empty,
          roundWinner = None,
          setupPlayerCount = Some(playerNames.size),
          setupPlayerNames = playerNames,
          setupDeckSize = Some(deckSize),
          currentAttackerIndex = None,
          lastAttackerIndex = None
        )
      )

class StubUndoRedoManager(
    val undoStack: List[(GameCommand, GameState)] = List.empty,
    val redoStack: List[(GameCommand, GameState)] = List.empty
) extends UndoRedoManager:

  def save(command: GameCommand, currentState: GameState): UndoRedoManager =
    new StubUndoRedoManager(
      undoStack = (command, currentState) :: undoStack,
      redoStack = List.empty
    )

  def undo(currentState: GameState): Option[(UndoRedoManager, GameState)] =
    undoStack match
      case (_, prevState) :: tail =>
        val newManager = new StubUndoRedoManager(
          undoStack = tail,
          redoStack = (null, currentState) :: redoStack
        )
        Some((newManager, prevState))
      case Nil => None

  def redo(currentState: GameState): Option[(UndoRedoManager, GameState)] =
    redoStack match
      case (_, nextState) :: tail =>
        val newManager = new StubUndoRedoManager(
          undoStack = (null, currentState) :: undoStack,
          redoStack = tail
        )
        Some((newManager, nextState))
      case Nil => None

class SpyController(
    initialState: GameState,
    undoRedoMgr: UndoRedoManager,
    gameSetup: GameSetup
) extends Controller:

  var currentState: GameState = initialState
  var processedActions: List[PlayerAction] = List.empty
  var observers: List[Observer] = List.empty

  def gameState: GameState = currentState

  def processPlayerAction(action: PlayerAction): GameState =
    processedActions = processedActions :+ action
    currentState = currentState.copy(lastEvent = Some(GameEvent.InvalidMove))
    notifyObservers
    currentState

  def undo(): Option[GameState] =
    undoRedoMgr.undo(currentState) match
      case Some((_, prevState)) =>
        currentState = prevState
        notifyObservers
        Some(currentState)
      case None =>
        currentState = currentState.copy(lastEvent = Some(GameEvent.CannotUndo))
        notifyObservers
        Some(currentState)

  def redo(): Option[GameState] =
    undoRedoMgr.redo(currentState) match
      case Some((_, nextState)) =>
        currentState = nextState
        notifyObservers
        Some(currentState)
      case None =>
        currentState = currentState.copy(lastEvent = Some(GameEvent.CannotRedo))
        notifyObservers
        Some(currentState)

  def getStatusString(): String = currentState.gamePhase.getClass.getSimpleName

  def add(observer: Observer): Unit =
    observers = observers :+ observer

  def remove(observer: Observer): Unit =
    observers = observers.filterNot(_ == observer)

  def notifyObservers: Unit =
    observers.foreach(_.update)

object TestHelper:
  private val cardFactory = TestFactories.cardFactory
  private val playerFactory = TestFactories.playerFactory
  private val gameStateFactory = TestFactories.gameStateFactory

  def createTestGameState(
      players: List[Player] = List(playerFactory("P1"), playerFactory("P2")),
      deck: List[Card] = List.empty,
      table: Map[Card, Option[Card]] = Map.empty,
      discardPile: List[Card] = List.empty,
      trumpCard: Card = cardFactory(Suit.Hearts, Rank.Six, isTrump = true),
      attackerIndex: Int = 0,
      defenderIndex: Int = 1,
      gamePhase: GamePhase = TestGamePhases.setupPhase,
      lastEvent: Option[GameEvent] = None,
      passedPlayers: Set[Int] = Set.empty,
      roundWinner: Option[Int] = None,
      setupPlayerCount: Option[Int] = None,
      setupPlayerNames: List[String] = List.empty,
      setupDeckSize: Option[Int] = None,
      currentAttackerIndex: Option[Int] = None,
      lastAttackerIndex: Option[Int] = None
  ): GameState =
    gameStateFactory(
      players,
      deck,
      table,
      discardPile,
      trumpCard,
      attackerIndex,
      defenderIndex,
      gamePhase,
      lastEvent,
      passedPlayers,
      roundWinner,
      setupPlayerCount,
      setupPlayerNames,
      setupDeckSize,
      currentAttackerIndex,
      lastAttackerIndex
    )
