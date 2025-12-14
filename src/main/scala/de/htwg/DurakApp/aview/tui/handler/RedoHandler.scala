package de.htwg.DurakApp.aview.tui.handler

import de.htwg.DurakApp.controller.ControllerInterface.{
  Controller,
  PlayerAction,
  RedoAction,
  InvalidAction
}
import de.htwg.DurakApp.model.ModelInterface.GameState

class RedoHandler(
    controller: Controller,
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
