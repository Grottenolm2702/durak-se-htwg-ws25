package de.htwg.DurakApp.aview.tui.handler

import de.htwg.DurakApp.controller.{PassAction, PlayerAction, InvalidAction}
import de.htwg.DurakApp.model.GameState

class PassHandler extends InputHandler {
  override def handleRequest(input: String, gameState: GameState): PlayerAction = {
    val inputArgs = input.trim.toLowerCase.split("\\s+").toList
    inputArgs.headOption match {
      case Some("pass") =>
        PassAction
      case _ =>
        next.map(_.handleRequest(input, gameState)).getOrElse(InvalidAction)
    }
  }
}
