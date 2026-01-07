package de.htwg.DurakApp.testutil

import de.htwg.DurakApp.util.{UndoRedoManager, UndoRedoManagerFactory}

class StubUndoRedoManagerFactory extends UndoRedoManagerFactory:
  def create(): UndoRedoManager =
    new StubUndoRedoManager()
