package de.htwg.DurakApp.aview.tui.handler

import de.htwg.DurakApp.controller.PlayerAction
import de.htwg.DurakApp.model.GameState

trait InputHandler {
  var next: Option[InputHandler] = None

  def handleRequest(input: String, gameState: GameState): PlayerAction

  def setNext(handler: InputHandler): InputHandler = {
    next = Some(handler)
    handler
  }
}
