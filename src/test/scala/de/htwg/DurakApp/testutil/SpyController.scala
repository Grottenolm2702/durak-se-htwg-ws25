package de.htwg.DurakApp.testutil

import de.htwg.DurakApp.controller.{Controller, PlayerAction}
import de.htwg.DurakApp.model.GameState
import de.htwg.DurakApp.model.state.GameEvent
import de.htwg.DurakApp.util.Observer

class SpyController(
    initialState: GameState,
    undoRedoMgr: de.htwg.DurakApp.util.UndoRedoManager
) extends Controller:

  var currentState: GameState = initialState
  var processedActions: List[PlayerAction] = List.empty
  var observers: List[Observer] = List.empty

  def gameState: GameState = currentState

  def processPlayerAction(action: PlayerAction): GameState =
    processedActions = processedActions :+ action
    currentState = currentState.copy(lastEvent = Some(GameEvent.InvalidMove))
    notifyObservers
    currentState

  def undo(): Option[GameState] =
    undoRedoMgr.undo(currentState) match
      case Some((_, prevState)) =>
        currentState = prevState
        notifyObservers
        Some(currentState)
      case None =>
        currentState = currentState.copy(lastEvent = Some(GameEvent.CannotUndo))
        notifyObservers
        Some(currentState)

  def redo(): Option[GameState] =
    undoRedoMgr.redo(currentState) match
      case Some((_, nextState)) =>
        currentState = nextState
        notifyObservers
        Some(currentState)
      case None =>
        currentState = currentState.copy(lastEvent = Some(GameEvent.CannotRedo))
        notifyObservers
        Some(currentState)

  def getStatusString(): String = currentState.gamePhase.getClass.getSimpleName

  def add(observer: Observer): Unit =
    observers = observers :+ observer

  def remove(observer: Observer): Unit =
    observers = observers.filterNot(_ == observer)

  def notifyObservers: Unit =
    observers.foreach(_.update)
