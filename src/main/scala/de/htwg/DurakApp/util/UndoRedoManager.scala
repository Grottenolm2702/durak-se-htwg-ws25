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
      case _ :: previousState :: tailOfUndoStack =>
        val current = undoStack.head
        val newManager = this.copy(undoStack = previousState :: tailOfUndoStack, redoStack = current :: redoStack)
        Some((newManager, previousState))
      case _ :: Nil => None
      case Nil => None
    }
  }

  def redo: Option[(ImmutableUndoRedoManager, GameState)] = {
    redoStack match {
      case headOfRedoStack :: tailOfRedoStack =>
        val newManager = this.copy(undoStack = headOfRedoStack :: undoStack, redoStack = tailOfRedoStack)
        Some((newManager, headOfRedoStack))
      case Nil => None
    }
  }
}

object ImmutableUndoRedoManager {
  def apply(): ImmutableUndoRedoManager = ImmutableUndoRedoManager(Nil, Nil)
}
