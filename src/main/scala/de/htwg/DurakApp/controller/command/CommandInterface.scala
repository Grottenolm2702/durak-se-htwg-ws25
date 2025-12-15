package de.htwg.DurakApp.controller.command

import de.htwg.DurakApp.model.ModelInterface.{Card, GameState, GameEvent}
import de.htwg.DurakApp.controller.command.{
  GameCommand as InternalGameCommand,
  impl
}

/** Command Component Interface
  *
  * This is the public port to the Command component. All external access to
  * command functionality must go through this interface.
  *
  * The Command component implements the Command pattern for game actions,
  * enabling undo/redo functionality. Each command encapsulates a game action
  * and knows how to execute and reverse itself.
  *
  * @example
  * {{{
  * import de.htwg.DurakApp.controller.command.CommandInterface.*
  * 
  * val command = PlayCardCommand(card)
  * val newState = command.execute(currentState)
  * val undoneState = command.undo(newState)
  * }}}
  */
object CommandInterface:
  
  // Type Aliases
  
  /** Type alias for GameCommand.
    * Base trait for all executable game commands that support undo/redo.
    */
  type GameCommand = InternalGameCommand

  // Factory Objects
  
  /** Factory for creating commands from player actions.
    *
    * Converts a PlayerAction into an executable GameCommand, validating
    * the action against the current game state.
    */
  object CommandFactory:
    /** Creates a command from a player action.
      *
      * @param action The player action to convert
      * @param gameState The current game state for validation
      * @return Either a validation error or the created command
      */
    def createCommand(
        action: de.htwg.DurakApp.controller.PlayerAction,
        gameState: GameState
    ): Either[GameEvent, GameCommand] =
      impl.CommandFactory.createCommand(action, gameState)

  /** Factory for creating PlayCardCommand instances.
    *
    * Commands a player to play a specific card from their hand.
    */
  object PlayCardCommand:
    /** Creates a command to play a card.
      *
      * @param card The card to play
      * @return A new PlayCardCommand instance
      */
    def apply(card: Card): GameCommand =
      impl.PlayCardCommand(card)

  /** Factory for creating PassCommand instances.
    *
    * Commands a player to pass their turn.
    */
  object PassCommand:
    /** Creates a command to pass.
      *
      * @return A new PassCommand instance
      */
    def apply(): GameCommand =
      impl.PassCommand()

  /** Factory for creating TakeCardsCommand instances.
    *
    * Commands the defender to take all cards from the table.
    */
  object TakeCardsCommand:
    /** Creates a command to take cards.
      *
      * @return A new TakeCardsCommand instance
      */
    def apply(): GameCommand =
      impl.TakeCardsCommand()

  /** Factory for creating PhaseChangeCommand instances.
    *
    * Commands a phase transition in the game.
    */
  object PhaseChangeCommand:
    /** Creates a command to change phase.
      *
      * @return A new PhaseChangeCommand instance
      */
    def apply(): GameCommand =
      impl.PhaseChangeCommand()
