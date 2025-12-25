package de.htwg.DurakApp.controller

import de.htwg.DurakApp.model.GameState
import de.htwg.DurakApp.util.{UndoRedoManager, Observer}

/** Controller trait for managing game flow and state transitions.
  *
  * The Controller is the central component that processes player actions,
  * manages undo/redo operations, and notifies observers (views) of state
  * changes. It implements the Observable pattern to allow multiple views to
  * react to game state updates.
  *
  * The Controller follows the Command pattern for undo/redo functionality,
  * allowing players to reverse and replay their actions.
  */
trait Controller:

  /** Processes a player action and updates the game state.
    *
    * This method is the main entry point for all player interactions. It
    * validates the action, executes it through the command pattern, handles
    * phase transitions, and notifies all observers.
    *
    * @param action
    *   The player action to process (e.g., PlayCardAction, PassAction)
    * @return
    *   The updated game state after processing the action
    */
  def processPlayerAction(action: PlayerAction): GameState

  /** Undoes the last action.
    *
    * Reverts the game state to the previous state before the last action. Uses
    * the command pattern to restore state from the undo stack.
    *
    * @return
    *   Some(GameState) if undo was successful, None if no actions to undo
    */
  def undo(): Option[GameState]

  /** Redoes the last undone action.
    *
    * Reapplies the most recently undone action. Only works if undo was called
    * before and no new actions were performed since.
    *
    * @return
    *   Some(GameState) if redo was successful, None if no actions to redo
    */
  def redo(): Option[GameState]

  /** Returns a string representation of the current game status.
    *
    * Typically returns the name of the current game phase for display purposes.
    *
    * @return
    *   A status string describing the current game state
    */
  def getStatusString(): String

  /** Returns the current game state.
    *
    * Provides read access to the current state of the game.
    *
    * @return
    *   The current GameState
    */
  def gameState: GameState

  /** Adds an observer to be notified of state changes.
    *
    * Observers (typically views) are notified whenever the game state changes.
    *
    * @param observer
    *   The observer to add
    */
  def add(observer: Observer): Unit

  /** Removes an observer from the notification list.
    *
    * @param observer
    *   The observer to remove
    */
  def remove(observer: Observer): Unit

  /** Notifies all registered observers of a state change.
    *
    * Called internally after state changes to update all views.
    */
  def notifyObservers: Unit

/** Factory object for creating Controller instances. */
object Controller:
  /** Creates a new Controller.
    *
    * @param gameState
    *   The initial game state
    * @param undoRedoManager
    *   The manager for undo/redo command history
    * @return
    *   A new Controller instance
    */
  def apply(
      gameState: GameState,
      undoRedoManager: UndoRedoManager
  ): Controller =
    import de.htwg.DurakApp.controller.command.CommandFactory
    impl.ControllerImpl(gameState, undoRedoManager, CommandFactory)
  
  /** Creates a new Controller with explicit CommandFactory (for DI).
    *
    * @param gameState
    *   The initial game state
    * @param undoRedoManager
    *   The manager for undo/redo command history
    * @param commandFactory
    *   The command factory to use
    * @return
    *   A new Controller instance
    */
  def apply(
      gameState: GameState,
      undoRedoManager: UndoRedoManager,
      commandFactory: command.CommandFactory.type
  ): Controller =
    impl.ControllerImpl(gameState, undoRedoManager, commandFactory)
