package de.htwg.DurakApp.aview.tui.handler

import de.htwg.DurakApp.controller.{
  ControllerInterface,
  PlayerAction,
  RedoAction,
  InvalidAction
}
import de.htwg.DurakApp.model.GameState

class RedoHandler(
    controller: ControllerInterface,
    override val next: Option[InputHandler] = None
) extends InputHandler {
  override def handleRequest(
      input: String,
      gameState: GameState
  ): PlayerAction = {
    input.toLowerCase match {
      case "redo" | "y" | "r" =>
        controller.redo()
        RedoAction
      case _ =>
        super.handleRequest(input, gameState)
    }
  }
}
