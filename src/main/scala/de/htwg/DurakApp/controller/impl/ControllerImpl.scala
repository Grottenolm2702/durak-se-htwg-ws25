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
    var gameState: GameState,
    var undoRedoManager: UndoRedoManager,
    commandFactory: CommandFactory,
    gameSetup: GameSetup,
    undoRedoManagerFactory: UndoRedoManagerFactory,
    gamePhases: GamePhases
) extends Observable
    with Controller {

  def processPlayerAction(action: PlayerAction): GameState = {
    if (isSetupPhase) {
      processSetupAction(action)
    } else if (gameState.gamePhase == gamePhases.askPlayAgainPhase) {
      processPlayAgainAction(action)
    } else {
      processGameAction(action)
    }
  }

  private def isSetupPhase: Boolean = {
    gameState.gamePhase == gamePhases.setupPhase ||
    gameState.gamePhase == gamePhases.askPlayerCountPhase ||
    gameState.gamePhase == gamePhases.askPlayerNamesPhase ||
    gameState.gamePhase == gamePhases.askDeckSizePhase
  }

  private def processSetupAction(action: PlayerAction): GameState = {
    action match {
      case SetPlayerCountAction(count) => handleSetPlayerCount(count)
      case AddPlayerNameAction(name)   => handleAddPlayerName(name)
      case SetDeckSizeAction(size)     => handleSetDeckSize(size)
      case _                           => setSetupError()
    }
    notifyObservers
    this.gameState
  }

  private def handleSetPlayerCount(count: Int): Unit = {
    if (count < 2 || count > 6) {
      gameState = gameState.copy(lastEvent = Some(GameEvent.SetupError))
      return
    }
    
    gameState = gameState.copy(
      setupPlayerCount = Some(count),
      gamePhase = gamePhases.askPlayerNamesPhase,
      lastEvent = Some(GameEvent.AskPlayerNames)
    )
  }

  private def handleAddPlayerName(name: String): Unit = {
    if (name.trim.isEmpty) {
      gameState = gameState.copy(lastEvent = Some(GameEvent.SetupError))
      return
    }

    val currentNames = gameState.setupPlayerNames
    val expectedCount = gameState.setupPlayerCount.getOrElse(0)
    val newNames = currentNames :+ name.trim

    if (newNames.size > expectedCount) {
      gameState = gameState.copy(lastEvent = Some(GameEvent.SetupError))
      return
    }

    if (newNames.size < expectedCount) {
      gameState = gameState.copy(
        setupPlayerNames = newNames,
        lastEvent = Some(GameEvent.AskPlayerNames)
      )
    } else {
      gameState = gameState.copy(
        setupPlayerNames = newNames,
        gamePhase = gamePhases.askDeckSizePhase,
        lastEvent = Some(GameEvent.AskDeckSize)
      )
    }
  }

  private def handleSetDeckSize(size: Int): Unit = {
    val minSize = gameState.setupPlayerNames.size
    
    if (size < minSize || size > 36) {
      gameState = gameState.copy(lastEvent = Some(GameEvent.SetupError))
      return
    }

    gameSetup.setupGame(gameState.setupPlayerNames, size) match {
      case Some(newGameState) =>
        gameState = newGameState.copy(
          lastEvent = Some(GameEvent.GameSetupComplete),
          setupPlayerCount = gameState.setupPlayerCount,
          setupPlayerNames = gameState.setupPlayerNames,
          setupDeckSize = Some(size)
        )
      case None =>
        gameState = gameState.copy(lastEvent = Some(GameEvent.SetupError))
    }
  }

  private def processPlayAgainAction(action: PlayerAction): GameState = {
    action match {
      case PlayAgainAction  => handlePlayAgain()
      case ExitGameAction   => handleExitGame()
      case _                => setInvalidMoveError()
    }
    notifyObservers
    this.gameState
  }

  private def handlePlayAgain(): Unit = {
    val newPlayerNames = gameState.setupPlayerNames
    val newDeckSize = gameState.setupDeckSize.getOrElse(36)

    gameSetup.setupGame(newPlayerNames, newDeckSize) match {
      case Some(newGameState) =>
        gameState = newGameState.copy(
          setupPlayerCount = Some(newPlayerNames.size),
          setupPlayerNames = newPlayerNames,
          setupDeckSize = Some(newDeckSize),
          lastEvent = Some(GameEvent.GameSetupComplete)
        )
        undoRedoManager = undoRedoManagerFactory.create()
      case None =>
        gameState = gameState.copy(lastEvent = Some(GameEvent.SetupError))
    }
  }

  private def handleExitGame(): Unit = {
    gameState = gameState.copy(lastEvent = Some(GameEvent.ExitApplication))
  }

  private def setSetupError(): Unit = {
    gameState = gameState.copy(lastEvent = Some(GameEvent.SetupError))
  }

  private def setInvalidMoveError(): Unit = {
    gameState = gameState.copy(lastEvent = Some(GameEvent.InvalidMove))
  }

  private def processGameAction(action: PlayerAction): GameState = {
    val oldGameStateBeforeAction = this.gameState
    val result = commandFactory.createCommand(action, oldGameStateBeforeAction)

    result match {
      case Left(event) =>
        handleCommandError(event, oldGameStateBeforeAction)
      case Right(command) =>
        handleCommandSuccess(command, oldGameStateBeforeAction)
    }
    this.gameState
  }

  private def handleCommandError(event: GameEvent, oldState: GameState): Unit = {
    this.gameState = oldState.copy(lastEvent = Some(event))
    notifyObservers
  }

  private def handleCommandSuccess(command: GameCommand, oldState: GameState): Unit = {
    val gameStateAfterCommand = command.execute(oldState)
    this.gameState = gameStateAfterCommand
    undoRedoManager = undoRedoManager.save(command, oldState)
    notifyObservers

    val finalStateFromPhaseHandling = handlePhaseRecursively(this.gameState, this.undoRedoManager)
    this.gameState = finalStateFromPhaseHandling
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

    this.gameState = nextState
    this.undoRedoManager = currentUndoRedoManager.save(
      commandFactory.phaseChange(),
      currentState
    )
    notifyObservers
    handlePhaseRecursively(this.gameState, this.undoRedoManager)
  }

  def undo(): Option[GameState] = {
    undoRedoManager.undo(this.gameState) match {
      case Some((newManager, previousState)) =>
        undoRedoManager = newManager
        this.gameState = previousState
        notifyObservers
        Some(this.gameState)
      case None =>
        this.gameState =
          this.gameState.copy(lastEvent = Some(GameEvent.CannotUndo))
        notifyObservers
        None
    }
  }

  def redo(): Option[GameState] = {
    undoRedoManager.redo(this.gameState) match {
      case Some((newManager, nextState)) =>
        undoRedoManager = newManager
        this.gameState = nextState
        notifyObservers
        Some(this.gameState)
      case None =>
        this.gameState =
          this.gameState.copy(lastEvent = Some(GameEvent.CannotRedo))
        notifyObservers
        None
    }
  }

  def getStatusString(): String = {
    gameState.gamePhase.toString
  }
}
