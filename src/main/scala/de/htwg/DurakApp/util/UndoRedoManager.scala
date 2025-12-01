package de.htwg.DurakApp.util

import de.htwg.DurakApp.model.GameState

case class ImmutableUndoRedoManager(
    undoStack: List[GameState],
    redoStack: List[GameState]
) {
  def save(state: GameState): ImmutableUndoRedoManager = {
    this.copy(undoStack = state :: undoStack, redoStack = Nil)
  }

  def undo: Option[(ImmutableUndoRedoManager, GameState)] = {
    undoStack match {
      case Nil => None
      case head :: tail =>
        val newManager = this.copy(undoStack = tail, redoStack = head :: redoStack)
        Some((newManager, head))
    }
  }

  def redo: Option[(ImmutableUndoRedoManager, GameState)] = {
    redoStack match {
      case Nil => None
      case head :: tail =>
        val newManager = this.copy(undoStack = head :: undoStack, redoStack = tail)
        Some((newManager, head))
    }
  }
}

object ImmutableUndoRedoManager {
  def apply(): ImmutableUndoRedoManager = ImmutableUndoRedoManager(Nil, Nil)
}
