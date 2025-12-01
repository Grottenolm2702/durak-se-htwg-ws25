package de.htwg.DurakApp.aview.tui.handler

import de.htwg.DurakApp.controller.{
  Controller,
  PlayerAction,
  RedoAction,
  InvalidAction
}
import de.htwg.DurakApp.model.GameState

class RedoHandler(controller: Controller) extends InputHandler {
  override def handleRequest(
      input: String,
      gameState: GameState
  ): PlayerAction = {
    input.toLowerCase match {
      case "redo" | "y" | "r" =>
        controller.redo()
        RedoAction
      case _ =>
        next.map(_.handleRequest(input, gameState)).getOrElse(InvalidAction)
    }
  }
}
