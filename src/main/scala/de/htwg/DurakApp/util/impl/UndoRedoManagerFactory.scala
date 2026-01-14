package de.htwg.DurakApp.util.impl

import de.htwg.DurakApp.util.{UndoRedoManager, UndoRedoManagerFactory}
import de.htwg.DurakApp.controller.command.GameCommand
import de.htwg.DurakApp.model.GameState
import com.google.inject.Inject

class UndoRedoManagerFactoryImpl @Inject() () extends UndoRedoManagerFactory:
  def create(): UndoRedoManager =
    UndoRedoManagerImpl(Nil, Nil)
  
  def create(
      undoStack: List[(GameCommand, GameState)],
      redoStack: List[(GameCommand, GameState)]
  ): UndoRedoManager =
    UndoRedoManagerImpl(undoStack, redoStack)
