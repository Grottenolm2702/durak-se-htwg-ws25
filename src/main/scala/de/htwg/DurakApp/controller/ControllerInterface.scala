package de.htwg.DurakApp.controller

import de.htwg.DurakApp.model.ModelInterface.GameState

/** Controller Component Interface
  *
  * This is the public port to the Controller component. All external access to
  * controller functionality must go through this interface.
  *
  * Provides factory methods for creating controller objects through traits
  * only. Implementation details are hidden in the impl package.
  */
object ControllerInterface:
  type Controller = de.htwg.DurakApp.controller.Controller
  type GameCommand = de.htwg.DurakApp.controller.command.GameCommand
  type PlayerAction = de.htwg.DurakApp.controller.PlayerAction
  type PlayCardAction = de.htwg.DurakApp.controller.PlayCardAction
  type SetPlayerCountAction = de.htwg.DurakApp.controller.SetPlayerCountAction
  type AddPlayerNameAction = de.htwg.DurakApp.controller.AddPlayerNameAction
  type SetDeckSizeAction = de.htwg.DurakApp.controller.SetDeckSizeAction

  val PlayCardAction = de.htwg.DurakApp.controller.PlayCardAction
  val PassAction = de.htwg.DurakApp.controller.PassAction
  val TakeCardsAction = de.htwg.DurakApp.controller.TakeCardsAction
  val InvalidAction = de.htwg.DurakApp.controller.InvalidAction
  val UndoAction = de.htwg.DurakApp.controller.UndoAction
  val RedoAction = de.htwg.DurakApp.controller.RedoAction
  val SetPlayerCountAction = de.htwg.DurakApp.controller.SetPlayerCountAction
  val AddPlayerNameAction = de.htwg.DurakApp.controller.AddPlayerNameAction
  val SetDeckSizeAction = de.htwg.DurakApp.controller.SetDeckSizeAction
  val PlayAgainAction = de.htwg.DurakApp.controller.PlayAgainAction
  val ExitGameAction = de.htwg.DurakApp.controller.ExitGameAction

  object Controller:
    def apply(
        gameState: GameState,
        undoRedoManager: de.htwg.DurakApp.util.UndoRedoManager
    ): de.htwg.DurakApp.controller.Controller =
      de.htwg.DurakApp.controller.Controller(gameState, undoRedoManager)
