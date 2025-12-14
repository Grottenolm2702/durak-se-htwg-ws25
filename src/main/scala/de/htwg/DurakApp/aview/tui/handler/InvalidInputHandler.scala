package de.htwg.DurakApp.aview.tui.handler

import de.htwg.DurakApp.controller.ControllerInterface.{InvalidAction, PlayerAction}
import de.htwg.DurakApp.model.ModelInterface.GameState

class InvalidInputHandler(override val next: Option[InputHandler] = None)
    extends InputHandler {
  override def handleRequest(
      input: String,
      gameState: GameState
  ): PlayerAction = {
    InvalidAction
  }
}
