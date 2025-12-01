package de.htwg.DurakApp.controller

import de.htwg.DurakApp.model.GameState
import de.htwg.DurakApp.model.state.GameEvent
import de.htwg.DurakApp.util.{Observable, ImmutableUndoRedoManager}
import de.htwg.DurakApp.controller.command.{Command, CommandFactory}
import de.htwg.DurakApp.controller.{
  PlayerAction,
  PlayCardAction,
  PassAction,
  TakeCardsAction,
  InvalidAction
}

class Controller(var gameState: GameState) extends Observable {
  private var undoRedoManager: ImmutableUndoRedoManager = ImmutableUndoRedoManager().save(gameState)

  def processPlayerAction(action: PlayerAction): Unit = {
    val oldGameState = this.gameState
    val result = CommandFactory.createCommand(action, gameState)
    result match {
      case Left(event) =>
        this.gameState = gameState.copy(lastEvent = Some(event))
      case Right(command) =>
        this.gameState = command.execute(gameState)
        undoRedoManager = undoRedoManager.save(this.gameState)
    }
    notifyObservers
  }

  def undo(): Unit = {
    undoRedoManager.undo match {
      case Some((newManager, previousState)) =>
        undoRedoManager = newManager
        this.gameState = previousState
        notifyObservers
      case None =>
        this.gameState = this.gameState.copy(lastEvent = Some(GameEvent.CannotUndo))
        notifyObservers
    }
  }

  def redo(): Unit = {
    undoRedoManager.redo match {
      case Some((newManager, nextState)) =>
        undoRedoManager = newManager
        this.gameState = nextState
        notifyObservers
      case None =>
        this.gameState = this.gameState.copy(lastEvent = Some(GameEvent.CannotRedo))
        notifyObservers
    }
  }

  def getStatusString(): String = {
    gameState.gamePhase.toString
  }
}
