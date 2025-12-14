package de.htwg.DurakApp.aview.tui.handler

import de.htwg.DurakApp.controller.ControllerInterface.{
  InvalidAction,
  PlayerAction
}
import de.htwg.DurakApp.model.ModelInterface.GameState

trait InputHandler {
  val next: Option[InputHandler]

  def handleRequest(input: String, gameState: GameState): PlayerAction = {
    next
      .map(handler => handler.handleRequest(input, gameState))
      .getOrElse(InvalidAction)
  }
}
