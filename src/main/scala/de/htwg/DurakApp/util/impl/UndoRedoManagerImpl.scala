package de.htwg.DurakApp.util.impl

import de.htwg.DurakApp.util.UndoRedoManager
import de.htwg.DurakApp.controller.command.GameCommand
import de.htwg.DurakApp.model.GameState

private[util] case class UndoRedoManagerImpl(
    undoStack: List[(GameCommand, GameState)],
    redoStack: List[(GameCommand, GameState, GameState)]
) extends UndoRedoManager {

  def save(
      command: GameCommand,
      oldState: GameState
  ): UndoRedoManager = {
    this.copy(undoStack = (command, oldState) :: undoStack, redoStack = Nil)
  }

  def undo(
      currentGameState: GameState
  ): Option[(UndoRedoManager, GameState)] = {
    undoStack match {
      case (command, previousState) :: tailOfUndoStack =>
        val undoneState = command.undo(currentGameState, previousState)
        val newManager = this.copy(
          undoStack = tailOfUndoStack,
          redoStack = (command, previousState, currentGameState) :: redoStack
        )
        Some((newManager, undoneState))
      case Nil => None
    }
  }

  def redo(
      currentGameState: GameState
  ): Option[(UndoRedoManager, GameState)] = {
    redoStack match {
      case (
            command,
            stateBeforeCommand,
            stateAfterCommand
          ) :: tailOfRedoStack =>
        val newManager = this.copy(
          undoStack = (command, stateBeforeCommand) :: undoStack,
          redoStack = tailOfRedoStack
        )
        Some((newManager, stateAfterCommand))
      case Nil => None
    }
  }
}
