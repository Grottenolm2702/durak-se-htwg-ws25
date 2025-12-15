package de.htwg.DurakApp.util

import de.htwg.DurakApp.controller.ControllerInterface.GameCommand
import de.htwg.DurakApp.model.ModelInterface.GameState

trait UndoRedoManager:
  def undoStack: List[(GameCommand, GameState)]
  def redoStack: List[(GameCommand, GameState)]
  def save(command: GameCommand, oldState: GameState): UndoRedoManager
  def undo(currentGameState: GameState): Option[(UndoRedoManager, GameState)]
  def redo(currentGameState: GameState): Option[(UndoRedoManager, GameState)]

object UndoRedoManager:
  def apply(): UndoRedoManager = impl.UndoRedoManagerImpl(Nil, Nil)
