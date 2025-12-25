package de.htwg.DurakApp.aview.tui.handler

import de.htwg.DurakApp.controller.{
  InvalidAction,
  PlayerAction
}
import de.htwg.DurakApp.model.GameState

class InvalidInputHandler(override val next: Option[InputHandler] = None)
    extends InputHandler {
  override def handleRequest(
      input: String,
      gameState: GameState
  ): PlayerAction = {
    InvalidAction
  }
}
