package de.htwg.DurakApp.aview.tui.handler

import de.htwg.DurakApp.controller.{
  ControllerInterface,
  PlayerAction,
  UndoAction,
  InvalidAction
}
import de.htwg.DurakApp.model.GameState

class UndoHandler(
    controller: ControllerInterface,
    override val next: Option[InputHandler] = None
) extends InputHandler {
  override def handleRequest(
      input: String,
      gameState: GameState
  ): PlayerAction = {
    input.toLowerCase match {
      case "undo" | "z" | "u" =>
        controller.undo()
        UndoAction
      case _ =>
        super.handleRequest(input, gameState)
    }
  }
}
