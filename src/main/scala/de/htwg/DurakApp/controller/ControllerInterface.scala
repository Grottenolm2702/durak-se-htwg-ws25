package de.htwg.DurakApp.controller

import de.htwg.DurakApp.model.ModelInterface.GameState
import de.htwg.DurakApp.controller.{
  Controller as InternalController,
  PlayerAction as InternalPlayerAction,
  PlayCardAction as InternalPlayCardAction,
  PassAction as InternalPassAction,
  TakeCardsAction as InternalTakeCardsAction,
  InvalidAction as InternalInvalidAction,
  UndoAction as InternalUndoAction,
  RedoAction as InternalRedoAction,
  SetPlayerCountAction as InternalSetPlayerCountAction,
  AddPlayerNameAction as InternalAddPlayerNameAction,
  SetDeckSizeAction as InternalSetDeckSizeAction,
  PlayAgainAction as InternalPlayAgainAction,
  ExitGameAction as InternalExitGameAction
}
import de.htwg.DurakApp.controller.command.{
  CommandInterface as InternalCommandInterface,
  GameCommand as InternalGameCommand
}
import de.htwg.DurakApp.util.UndoRedoManager

/** Controller Component Interface
  *
  * This is the public port to the Controller component. All external access to
  * controller functionality must go through this interface.
  *
  * The Controller manages the game flow, processes player actions, handles
  * undo/redo operations, and notifies observers of state changes. It acts as
  * the mediator between the Model (game state) and View (user interface)
  * components.
  *
  * Provides factory methods for creating controller objects through traits
  * only. Implementation details are hidden in the impl package.
  *
  * @example
  *   {{{
  * import de.htwg.DurakApp.controller.ControllerInterface.*
  *
  * val controller = Controller(initialGameState, undoRedoManager)
  * controller.processPlayerAction(PlayCardAction(card))
  * controller.undo()
  *   }}}
  */
object ControllerInterface:

  // Type Aliases

  /** Type alias for the Controller trait. The main controller interface for
    * game flow management.
    */
  type Controller = InternalController

  /** Type alias for the CommandInterface. Provides access to the command
    * pattern implementation.
    */
  type CommandInterface = InternalCommandInterface.type

  /** Type alias for GameCommand. Represents executable game commands that can
    * be undone/redone.
    */
  type GameCommand = InternalGameCommand

  /** Type alias for PlayerAction. Base trait for all player actions in the
    * game.
    */
  type PlayerAction = InternalPlayerAction

  /** Type alias for PlayCardAction. Action to play a specific card.
    */
  type PlayCardAction = InternalPlayCardAction

  /** Type alias for SetPlayerCountAction. Action to set the number of players
    * during setup.
    */
  type SetPlayerCountAction = InternalSetPlayerCountAction

  /** Type alias for AddPlayerNameAction. Action to add a player name during
    * setup.
    */
  type AddPlayerNameAction = InternalAddPlayerNameAction

  /** Type alias for SetDeckSizeAction. Action to set the deck size during
    * setup.
    */
  type SetDeckSizeAction = InternalSetDeckSizeAction

  // Command Interface

  /** Provides access to the command pattern implementation for undo/redo. */
  val CommandInterface = InternalCommandInterface

  // Player Action Objects

  /** Factory for creating PlayCardAction instances. */
  val PlayCardAction = InternalPlayCardAction

  /** Action object representing a pass action. */
  val PassAction = InternalPassAction

  /** Action object representing taking cards from the table. */
  val TakeCardsAction = InternalTakeCardsAction

  /** Action object representing an invalid action. */
  val InvalidAction = InternalInvalidAction

  /** Action object representing an undo request. */
  val UndoAction = InternalUndoAction

  /** Action object representing a redo request. */
  val RedoAction = InternalRedoAction

  /** Factory for creating SetPlayerCountAction instances. */
  val SetPlayerCountAction = InternalSetPlayerCountAction

  /** Factory for creating AddPlayerNameAction instances. */
  val AddPlayerNameAction = InternalAddPlayerNameAction

  /** Factory for creating SetDeckSizeAction instances. */
  val SetDeckSizeAction = InternalSetDeckSizeAction

  /** Action object representing a play again request. */
  val PlayAgainAction = InternalPlayAgainAction

  /** Action object representing an exit game request. */
  val ExitGameAction = InternalExitGameAction

  /** Factory for creating Controller instances.
    *
    * The Controller manages game state transitions and player actions.
    */
  object Controller:
    /** Creates a new Controller instance.
      *
      * @param gameState
      *   The initial game state
      * @param undoRedoManager
      *   The undo/redo manager for command history
      * @return
      *   A new Controller instance
      */
    def apply(
        gameState: GameState,
        undoRedoManager: UndoRedoManager
    ): InternalController =
      InternalController(gameState, undoRedoManager)
