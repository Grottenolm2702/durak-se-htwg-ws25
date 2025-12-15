package de.htwg.DurakApp.controller.command

import de.htwg.DurakApp.model.ModelInterface.{Card, GameState, GameEvent}

/** Command Component Interface
  *
  * This is the public port to the Command component. All external access to
  * command functionality must go through this interface.
  */
object CommandInterface:
  type GameCommand = de.htwg.DurakApp.controller.command.GameCommand

  object CommandFactory:
    def createCommand(
        action: de.htwg.DurakApp.controller.PlayerAction,
        gameState: GameState
    ): Either[GameEvent, GameCommand] =
      impl.CommandFactory.createCommand(action, gameState)

  object PlayCardCommand:
    def apply(card: Card): GameCommand =
      impl.PlayCardCommand(card)

  object PassCommand:
    def apply(): GameCommand =
      impl.PassCommand()

  object TakeCardsCommand:
    def apply(): GameCommand =
      impl.TakeCardsCommand()

  object PhaseChangeCommand:
    def apply(): GameCommand =
      impl.PhaseChangeCommand()
