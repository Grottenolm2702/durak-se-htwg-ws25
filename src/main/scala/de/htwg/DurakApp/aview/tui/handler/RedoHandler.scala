package de.htwg.DurakApp.aview.tui.handler

import de.htwg.DurakApp.controller.{Controller, PlayerAction, RedoAction}
import de.htwg.DurakApp.model.GameState

class RedoHandler(controller: Controller) extends InputHandler {
  override def handleRequest(input: String, gameState: GameState): PlayerAction = {
    input.toLowerCase match {
      case "redo" | "y" |"r" =>
        controller.redo()
        RedoAction
      case _ =>
        next match {
          case Some(handler) => handler.handleRequest(input, gameState)
          case None => throw new IllegalArgumentException("No handler found for the given input.")
        }
    }
  }
}
