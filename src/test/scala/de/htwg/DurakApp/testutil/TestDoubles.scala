package de.htwg.DurakApp.testutil

import de.htwg.DurakApp.controller.{Controller, GameSetup, PlayerAction}
import de.htwg.DurakApp.controller.command.GameCommand
import de.htwg.DurakApp.model.{GameState, Card, Player, Suit, Rank, PlayerFactory, GameStateFactory, CardFactory}
import de.htwg.DurakApp.model.impl.{PlayerFactoryImpl, GameStateFactoryImpl, CardFactoryImpl}
import de.htwg.DurakApp.model.state.{GamePhase, GameEvent, SetupPhase}
import de.htwg.DurakApp.util.{UndoRedoManager, Observer}

// Test helpers for creating model instances
object TestFactories:
  val cardFactory: CardFactory = new CardFactoryImpl()
  val playerFactory: PlayerFactory = new PlayerFactoryImpl()
  val gameStateFactory: GameStateFactory = new GameStateFactoryImpl()

class StubGameSetup extends GameSetup:
  private val playerFactory = TestFactories.playerFactory
  private val gameStateFactory = TestFactories.gameStateFactory
  
  def setupGame(playerNames: List[String], deckSize: Int): Option[GameState] =
    if playerNames.size < 2 || playerNames.size > 6 || deckSize < 2 || deckSize > 36 then
      None
    else
      val players = playerNames.map(name => playerFactory(name, List.empty))
      val trumpCard = Card(Suit.Hearts, Rank.Six, isTrump = true)
      Some(gameStateFactory(
        players = players,
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = SetupPhase,
        lastEvent = Some(GameEvent.GameSetupComplete),
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = Some(playerNames.size),
        setupPlayerNames = playerNames,
        setupDeckSize = Some(deckSize),
        currentAttackerIndex = None,
        lastAttackerIndex = None
      ))

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
  private val playerFactory = TestFactories.playerFactory
  private val gameStateFactory = TestFactories.gameStateFactory
  
  def createTestGameState(
    players: List[Player] = List(playerFactory("P1"), playerFactory("P2")),
    deck: List[Card] = List.empty,
    table: Map[Card, Option[Card]] = Map.empty,
    discardPile: List[Card] = List.empty,
    trumpCard: Card = Card(Suit.Hearts, Rank.Six, isTrump = true),
    attackerIndex: Int = 0,
    defenderIndex: Int = 1,
    gamePhase: GamePhase = SetupPhase,
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
