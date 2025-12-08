package de.htwg.DurakApp.controller

import de.htwg.DurakApp.model.GameState
import de.htwg.DurakApp.model.state.GameEvent
import de.htwg.DurakApp.util.{Observable, UndoRedoManager}
import de.htwg.DurakApp.controller.command.{
  GameCommand,
  CommandFactory,
  PhaseChangeCommand
}
import de.htwg.DurakApp.controller.{
  PlayerAction,
  PlayCardAction,
  PassAction,
  TakeCardsAction,
  InvalidAction,
  SetPlayerCountAction,
  AddPlayerNameAction,
  SetDeckSizeAction
}
import de.htwg.DurakApp.model.builder.GameStateBuilder
import de.htwg.DurakApp.model.{Card, Player, Rank, Suit}
import de.htwg.DurakApp.model.state.*
import scala.util.Random

class Controller(var gameState: GameState, var undoRedoManager: UndoRedoManager)
    extends Observable {

  def processPlayerAction(action: PlayerAction): GameState = {
    gameState.gamePhase match {
      case SetupPhase | AskPlayerCountPhase | AskPlayerNamesPhase |
          AskDeckSizePhase =>
        action match {
          case SetPlayerCountAction(count) =>
            if (count >= 2 && count <= 6) {
              gameState = gameState.copy(
                setupPlayerCount = Some(count),
                gamePhase = AskPlayerNamesPhase,
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
                  gamePhase = AskDeckSizePhase,
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
            if (size >= 2 && size <= 36) {
              val initializedGameState =
                Setup.setupGame(gameState.setupPlayerNames, size)

              initializedGameState match {
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

      case _ =>
        val oldGameStateBeforeAction = this.gameState
        val result =
          CommandFactory.createCommand(action, oldGameStateBeforeAction)

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
                  new PhaseChangeCommand(),
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
