package de.htwg.DurakApp.aview.tui.handler

import de.htwg.DurakApp.controller.{Controller, PlayerAction, UndoAction, InvalidAction}
import de.htwg.DurakApp.model.GameState

class UndoHandler(controller: Controller) extends InputHandler {
  override def handleRequest(input: String, gameState: GameState): PlayerAction = {
    input.toLowerCase match {
      case "undo" | "z" | "u" =>
        controller.undo()
        UndoAction
      case _ =>
        next.map(_.handleRequest(input, gameState)).getOrElse(InvalidAction)
    }
  }
}
