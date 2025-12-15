package de.htwg.DurakApp.util.impl

import de.htwg.DurakApp.util.UndoRedoManager
import de.htwg.DurakApp.controller.ControllerInterface.GameCommand
import de.htwg.DurakApp.model.ModelInterface.GameState

private[util] case class UndoRedoManagerImpl(
    undoStack: List[(GameCommand, GameState)],
    redoStack: List[(GameCommand, GameState)]
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
          redoStack = (command, previousState) :: redoStack
        )
        Some((newManager, undoneState))
      case Nil => None
    }
  }

  def redo(
      currentGameState: GameState
  ): Option[(UndoRedoManager, GameState)] = {
    redoStack match {
      case (command, stateBeforeRedoCommand) :: tailOfRedoStack =>
        val redoneState = command.execute(currentGameState)
        val newManager = this.copy(
          undoStack = (command, stateBeforeRedoCommand) :: undoStack,
          redoStack = tailOfRedoStack
        )
        Some((newManager, redoneState))
      case Nil => None
    }
  }
}
