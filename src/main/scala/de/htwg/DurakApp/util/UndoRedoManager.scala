package de.htwg.DurakApp.util

import de.htwg.DurakApp.controller.command.GameCommand

import de.htwg.DurakApp.model.GameState

/** Manager for undo/redo functionality using the Command pattern.
  *
  * Instances are created through Guice DI (see DurakModule). Do not instantiate
  * manually - inject this trait instead.
  */
trait UndoRedoManager:
  def undoStack: List[(GameCommand, GameState)]
  def redoStack: List[(GameCommand, GameState, GameState)]
  def save(command: GameCommand, oldState: GameState): UndoRedoManager
  def undo(currentGameState: GameState): Option[(UndoRedoManager, GameState)]
  def redo(currentGameState: GameState): Option[(UndoRedoManager, GameState)]

/** Factory for creating new UndoRedoManager instances.
  *
  * Used when a fresh manager is needed (e.g., starting a new game). Inject this
  * factory via Guice DI.
  */
trait UndoRedoManagerFactory:
  def create(): UndoRedoManager
  def create(
      undoStack: List[(GameCommand, GameState)],
      redoStack: List[(GameCommand, GameState)]
  ): UndoRedoManager
