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
  InvalidAction
}

class Controller(var gameState: GameState, var undoRedoManager: UndoRedoManager)
    extends Observable {

  def processPlayerAction(action: PlayerAction): Unit = {
    val oldGameStateBeforeAction = this.gameState
    val result = CommandFactory.createCommand(action, oldGameStateBeforeAction)

    result match {
      case Left(event) =>
        this.gameState = oldGameStateBeforeAction.copy(lastEvent = Some(event))
        notifyObservers

      case Right(command) =>
        val gameStateAfterCommand = command.execute(oldGameStateBeforeAction)
        this.gameState = gameStateAfterCommand
        undoRedoManager =
          undoRedoManager.save(command, oldGameStateBeforeAction)
        notifyObservers

        @scala.annotation.tailrec
        def handlePhaseRecursively(
            currentState: GameState,
            currentUndoRedoManager: UndoRedoManager
        ): Unit = {
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
          } else {}
        }

        handlePhaseRecursively(this.gameState, this.undoRedoManager)
    }
  }

  def undo(): Unit = {
    undoRedoManager.undo(this.gameState) match {
      case Some((newManager, previousState)) =>
        undoRedoManager = newManager
        this.gameState = previousState
        notifyObservers
      case None =>
        this.gameState =
          this.gameState.copy(lastEvent = Some(GameEvent.CannotUndo))
        notifyObservers
    }
  }

  def redo(): Unit = {
    undoRedoManager.redo(this.gameState) match {
      case Some((newManager, nextState)) =>
        undoRedoManager = newManager
        this.gameState = nextState
        notifyObservers
      case None =>
        this.gameState =
          this.gameState.copy(lastEvent = Some(GameEvent.CannotRedo))
        notifyObservers
    }
  }

  def getStatusString(): String = {
    gameState.gamePhase.toString
  }
}
