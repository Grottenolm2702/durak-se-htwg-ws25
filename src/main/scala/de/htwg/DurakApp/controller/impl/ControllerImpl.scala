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
    if (gameState.gamePhase == gamePhases.setupPhase ||
        gameState.gamePhase == gamePhases.askPlayerCountPhase ||
        gameState.gamePhase == gamePhases.askPlayerNamesPhase ||
        gameState.gamePhase == gamePhases.askDeckSizePhase) {
      action match {
        case SetPlayerCountAction(count) =>
          if (count >= 2 && count <= 6) {
            gameState = gameState.copy(
              setupPlayerCount = Some(count),
              gamePhase = gamePhases.askPlayerNamesPhase,
              lastEvent = Some(GameEvent.AskPlayerNames)
            )
          } else {
            gameState = gameState.copy(lastEvent = Some(GameEvent.SetupError))
          }
        case AddPlayerNameAction(name) =>
            val currentNames = gameState.setupPlayerNames
            val expectedCount = gameState.setupPlayerCount.getOrElse(0)
            if (name.trim.nonEmpty) {
              val newNames = currentNames :+ name.trim
              if (newNames.size < expectedCount) {
                gameState = gameState.copy(
                  setupPlayerNames = newNames,
                  lastEvent = Some(GameEvent.AskPlayerNames)
                )
              } else if (newNames.size == expectedCount) {
                gameState = gameState.copy(
                  setupPlayerNames = newNames,
                  gamePhase = gamePhases.askDeckSizePhase,
                  lastEvent = Some(GameEvent.AskDeckSize)
                )
              } else {
                gameState =
                  gameState.copy(lastEvent = Some(GameEvent.SetupError))
              }
            } else {
              gameState = gameState.copy(lastEvent = Some(GameEvent.SetupError))
            }
          case SetDeckSizeAction(size) =>
            val minSize = gameState.setupPlayerNames.size
            if (size >= minSize && size <= 36) {
              gameSetup.setupGame(gameState.setupPlayerNames, size) match {
                case Some(newGameState) =>
                  gameState = newGameState.copy(
                    lastEvent = Some(GameEvent.GameSetupComplete),
                    setupPlayerCount = gameState.setupPlayerCount,
                    setupPlayerNames = gameState.setupPlayerNames,
                    setupDeckSize = Some(size)
                  )
                case None =>
                  gameState =
                    gameState.copy(lastEvent = Some(GameEvent.SetupError))
              }
            } else {
              gameState = gameState.copy(lastEvent = Some(GameEvent.SetupError))
            }
          case _ =>
            gameState = gameState.copy(lastEvent = Some(GameEvent.SetupError))
      }
      notifyObservers
      this.gameState
    } else if (gameState.gamePhase == gamePhases.askPlayAgainPhase) {
      action match {
        case PlayAgainAction =>
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
              // Reset undo/redo manager for new game using factory
              undoRedoManager = undoRedoManagerFactory.create()
            case None =>
              gameState =
                gameState.copy(lastEvent = Some(GameEvent.SetupError))
          }
        case ExitGameAction =>
          gameState =
            gameState.copy(lastEvent = Some(GameEvent.ExitApplication))
        case _ =>
          gameState = gameState.copy(lastEvent = Some(GameEvent.InvalidMove))
      }
      notifyObservers
      this.gameState
    } else {
      val oldGameStateBeforeAction = this.gameState
      val result =
          commandFactory.createCommand(action, oldGameStateBeforeAction)

        result match {
          case Left(event) =>
            this.gameState =
              oldGameStateBeforeAction.copy(lastEvent = Some(event))
            notifyObservers

          case Right(command) =>
            val gameStateAfterCommand =
              command.execute(oldGameStateBeforeAction)
            this.gameState = gameStateAfterCommand
            undoRedoManager =
              undoRedoManager.save(command, oldGameStateBeforeAction)
            notifyObservers

            @scala.annotation.tailrec
            def handlePhaseRecursively(
                currentState: GameState,
                currentUndoRedoManager: UndoRedoManager
            ): GameState = {
              val oldPhaseStateBeforeHandle = currentState
              val nextState = currentState.gamePhase.handle(currentState)

              if (nextState != currentState) {
                this.gameState = nextState
                this.undoRedoManager = currentUndoRedoManager.save(
                  commandFactory.phaseChange(),
                  oldPhaseStateBeforeHandle
                )
                notifyObservers
                handlePhaseRecursively(this.gameState, this.undoRedoManager)
              } else {
                currentState
              }
            }

            val finalStateFromPhaseHandling =
              handlePhaseRecursively(this.gameState, this.undoRedoManager)
            this.gameState = finalStateFromPhaseHandling
        }
        this.gameState
    }
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
