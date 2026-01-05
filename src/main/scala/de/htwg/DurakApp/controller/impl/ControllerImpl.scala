package de.htwg.DurakApp.controller.impl

import de.htwg.DurakApp.controller.{Controller, GameSetup}
import de.htwg.DurakApp.model.GameState
import de.htwg.DurakApp.model.state.{GameEvent, GamePhases}
import de.htwg.DurakApp.util.{Observable, UndoRedoManager, UndoRedoManagerFactory}
import de.htwg.DurakApp.controller.command.{GameCommand, CommandFactory}
import de.htwg.DurakApp.controller.{
  PlayerAction,
  PlayCardAction,
  PassAction,
  TakeCardsAction,
  InvalidAction,
  SetPlayerCountAction,
  AddPlayerNameAction,
  SetDeckSizeAction,
  PlayAgainAction,
  ExitGameAction
}
import scala.util.Random
import com.google.inject.Inject

/** Implementation of Controller trait.
  * 
  * This class is package-private and should only be instantiated through
  * Guice DI (see DurakModule).
  */
class ControllerImpl @Inject() (
    private var _gameState: GameState,
    private var undoRedoManager: UndoRedoManager,
    commandFactory: CommandFactory,
    gameSetup: GameSetup,
    undoRedoManagerFactory: UndoRedoManagerFactory,
    gamePhases: GamePhases
) extends Observable
    with Controller {

  def gameState: GameState = _gameState

  def processPlayerAction(action: PlayerAction): GameState = {
    if (isSetupPhase) {
      processSetupAction(action)
    } else if (_gameState.gamePhase == gamePhases.askPlayAgainPhase) {
      processPlayAgainAction(action)
    } else {
      processGameAction(action)
    }
  }

  private def isSetupPhase: Boolean = {
    _gameState.gamePhase == gamePhases.setupPhase ||
    _gameState.gamePhase == gamePhases.askPlayerCountPhase ||
    _gameState.gamePhase == gamePhases.askPlayerNamesPhase ||
    _gameState.gamePhase == gamePhases.askDeckSizePhase
  }

  private def processSetupAction(action: PlayerAction): GameState = {
    action match {
      case SetPlayerCountAction(count) => handleSetPlayerCount(count)
      case AddPlayerNameAction(name)   => handleAddPlayerName(name)
      case SetDeckSizeAction(size)     => handleSetDeckSize(size)
      case _                           => setSetupError()
    }
    notifyObservers
    _gameState
  }

  private def handleSetPlayerCount(count: Int): Unit = {
    if (count < 2 || count > 6) {
      _gameState = gameState.copy(lastEvent = Some(GameEvent.SetupError))
      return
    }
    
    _gameState = gameState.copy(
      setupPlayerCount = Some(count),
      gamePhase = gamePhases.askPlayerNamesPhase,
      lastEvent = Some(GameEvent.AskPlayerNames)
    )
  }

  private def handleAddPlayerName(name: String): Unit = {
    if (name.trim.isEmpty) {
      _gameState = gameState.copy(lastEvent = Some(GameEvent.SetupError))
      return
    }

    val currentNames = _gameState.setupPlayerNames
    val expectedCount = _gameState.setupPlayerCount.getOrElse(0)
    val newNames = currentNames :+ name.trim

    if (newNames.size > expectedCount) {
      _gameState = gameState.copy(lastEvent = Some(GameEvent.SetupError))
      return
    }

    if (newNames.size < expectedCount) {
      _gameState = gameState.copy(
        setupPlayerNames = newNames,
        lastEvent = Some(GameEvent.AskPlayerNames)
      )
    } else {
      _gameState = gameState.copy(
        setupPlayerNames = newNames,
        gamePhase = gamePhases.askDeckSizePhase,
        lastEvent = Some(GameEvent.AskDeckSize)
      )
    }
  }

  private def handleSetDeckSize(size: Int): Unit = {
    val minSize = _gameState.setupPlayerNames.size
    
    if (size < minSize || size > 36) {
      _gameState = gameState.copy(lastEvent = Some(GameEvent.SetupError))
      return
    }

    gameSetup.setupGame(_gameState.setupPlayerNames, size) match {
      case Some(newGameState) =>
        _gameState = newGameState.copy(
          lastEvent = Some(GameEvent.GameSetupComplete),
          setupPlayerCount = _gameState.setupPlayerCount,
          setupPlayerNames = _gameState.setupPlayerNames,
          setupDeckSize = Some(size)
        )
      case None =>
        _gameState = gameState.copy(lastEvent = Some(GameEvent.SetupError))
    }
  }

  private def processPlayAgainAction(action: PlayerAction): GameState = {
    action match {
      case PlayAgainAction  => handlePlayAgain()
      case ExitGameAction   => handleExitGame()
      case _                => setInvalidMoveError()
    }
    notifyObservers
    _gameState
  }

  private def handlePlayAgain(): Unit = {
    val newPlayerNames = _gameState.setupPlayerNames
    val newDeckSize = gameState.setupDeckSize.getOrElse(36)

    gameSetup.setupGame(newPlayerNames, newDeckSize) match {
      case Some(newGameState) =>
        _gameState = newGameState.copy(
          setupPlayerCount = Some(newPlayerNames.size),
          setupPlayerNames = newPlayerNames,
          setupDeckSize = Some(newDeckSize),
          lastEvent = Some(GameEvent.GameSetupComplete)
        )
        undoRedoManager = undoRedoManagerFactory.create()
      case None =>
        _gameState = gameState.copy(lastEvent = Some(GameEvent.SetupError))
    }
  }

  private def handleExitGame(): Unit = {
    _gameState = gameState.copy(lastEvent = Some(GameEvent.ExitApplication))
  }

  private def setSetupError(): Unit = {
    _gameState = gameState.copy(lastEvent = Some(GameEvent.SetupError))
  }

  private def setInvalidMoveError(): Unit = {
    _gameState = gameState.copy(lastEvent = Some(GameEvent.InvalidMove))
  }

  private def processGameAction(action: PlayerAction): GameState = {
    val oldGameStateBeforeAction = _gameState
    val result = commandFactory.createCommand(action, oldGameStateBeforeAction)

    result match {
      case Left(event) =>
        handleCommandError(event, oldGameStateBeforeAction)
      case Right(command) =>
        handleCommandSuccess(command, oldGameStateBeforeAction)
    }
    _gameState
  }

  private def handleCommandError(event: GameEvent, oldState: GameState): Unit = {
    _gameState = oldState.copy(lastEvent = Some(event))
    notifyObservers
  }

  private def handleCommandSuccess(command: GameCommand, oldState: GameState): Unit = {
    val gameStateAfterCommand = command.execute(oldState)
    _gameState = gameStateAfterCommand
    undoRedoManager = undoRedoManager.save(command, oldState)
    notifyObservers

    val finalStateFromPhaseHandling = handlePhaseRecursively(_gameState, this.undoRedoManager)
    _gameState = finalStateFromPhaseHandling
  }

  @scala.annotation.tailrec
  private def handlePhaseRecursively(
      currentState: GameState,
      currentUndoRedoManager: UndoRedoManager
  ): GameState = {
    val nextState = currentState.gamePhase.handle(currentState)

    if (nextState == currentState) {
      return currentState
    }

    _gameState = nextState
    this.undoRedoManager = currentUndoRedoManager.save(
      commandFactory.phaseChange(),
      currentState
    )
    notifyObservers
    handlePhaseRecursively(_gameState, this.undoRedoManager)
  }

  def undo(): Option[GameState] = {
    undoRedoManager.undo(_gameState) match {
      case Some((newManager, previousState)) =>
        undoRedoManager = newManager
        _gameState = previousState
        notifyObservers
        Some(_gameState)
      case None =>
        _gameState =
          _gameState.copy(lastEvent = Some(GameEvent.CannotUndo))
        notifyObservers
        None
    }
  }

  def redo(): Option[GameState] = {
    undoRedoManager.redo(_gameState) match {
      case Some((newManager, nextState)) =>
        undoRedoManager = newManager
        _gameState = nextState
        notifyObservers
        Some(_gameState)
      case None =>
        _gameState =
          _gameState.copy(lastEvent = Some(GameEvent.CannotRedo))
        notifyObservers
        None
    }
  }

  def getStatusString(): String = {
    _gameState.gamePhase.toString
  }
}
