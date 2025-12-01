package de.htwg.DurakApp.aview.tui.handler

import de.htwg.DurakApp.controller.{PassAction, PlayerAction, InvalidAction}
import de.htwg.DurakApp.model.GameState

class PassHandler(override val next: Option[InputHandler] = None) extends InputHandler {
  override def handleRequest(
      input: String,
      gameState: GameState
  ): PlayerAction = {
    val inputArgs = input.trim.toLowerCase.split("\\s+").toList
    inputArgs.headOption match {
      case Some("pass") =>
        PassAction
      case _ =>
        super.handleRequest(input, gameState)
    }
  }
}
