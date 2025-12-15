package de.htwg.DurakApp.controller

import de.htwg.DurakApp.model.ModelInterface.GameState
import de.htwg.DurakApp.util.{UndoRedoManager, Observer}

trait Controller:
  def processPlayerAction(action: PlayerAction): GameState
  def undo(): Option[GameState]
  def redo(): Option[GameState]
  def getStatusString(): String
  def gameState: GameState
  def add(observer: Observer): Unit
  def remove(observer: Observer): Unit
  def notifyObservers: Unit

object Controller:
  def apply(
      gameState: GameState,
      undoRedoManager: UndoRedoManager
  ): Controller =
    impl.ControllerImpl(gameState, undoRedoManager)
