package de.htwg.DurakApp.testutil
import de.htwg.DurakApp.util.{UndoRedoManager, UndoRedoManagerFactory}
import de.htwg.DurakApp.controller.command.GameCommand
import de.htwg.DurakApp.model.GameState

class StubUndoRedoManagerFactory extends UndoRedoManagerFactory:
  def create(): UndoRedoManager =
    new StubUndoRedoManager()

  def create(
      undoStack: List[(GameCommand, GameState)],
      redoStack: List[(GameCommand, GameState)]
  ): UndoRedoManager =
    new StubUndoRedoManager()
