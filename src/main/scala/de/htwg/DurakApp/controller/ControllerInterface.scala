package de.htwg.DurakApp.controller

import de.htwg.DurakApp.model.GameState

/**
 * Controller Component Interface
 * 
 * This is the public port to the Controller component. All external access
 * to controller functionality must go through this interface.
 * 
 * Exports:
 * - Controller: Main controller class
 * - PlayerAction: All action types (PlayCard, Pass, TakeCards, etc.)
 * - Setup: Game setup functionality
 * - Commands: Command pattern implementation
 * 
 * The controller orchestrates game logic and manages state transitions.
 */
object ControllerInterface:
  export de.htwg.DurakApp.controller.Controller
  export de.htwg.DurakApp.controller.{
    PlayerAction,
    PlayCardAction,
    PassAction,
    TakeCardsAction,
    InvalidAction,
    UndoAction,
    RedoAction,
    SetPlayerCountAction,
    AddPlayerNameAction,
    SetDeckSizeAction,
    PlayAgainAction,
    ExitGameAction
  }
  export de.htwg.DurakApp.controller.Setup
  export de.htwg.DurakApp.controller.command.{GameCommand, CommandFactory}

/**
 * Controller Interface Trait
 * 
 * Defines the external contract for controller operations.
 * Extends Observable to support the Observer pattern.
 * 
 * Core Operations:
 * - processPlayerAction: Execute a player action and return new state
 * - undo/redo: Navigate through game history
 * - gameState: Access current game state
 * 
 * Observable Operations:
 * - add/remove: Manage observers (typically views)
 * - notifyObservers: Trigger view updates
 */
trait ControllerInterfaceTrait:
  def processPlayerAction(action: PlayerAction): GameState
  def undo(): Option[GameState]
  def redo(): Option[GameState]
  def getStatusString(): String
  def gameState: GameState
  def add(observer: de.htwg.DurakApp.util.Observer): Unit
  def remove(observer: de.htwg.DurakApp.util.Observer): Unit
  def notifyObservers: Unit
