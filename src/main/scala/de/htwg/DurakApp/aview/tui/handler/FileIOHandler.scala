package de.htwg.DurakApp.aview.tui.handler

import de.htwg.DurakApp.controller.{
  PlayerAction,
  SaveGameAction,
  LoadGameAction
}
import de.htwg.DurakApp.model.GameState

class FileIOHandler(
    override val next: Option[InputHandler] = None
) extends InputHandler {
  override def handleRequest(
      input: String,
      gameState: GameState
  ): PlayerAction = {
    input.toLowerCase match {
      case "save" | "s" => SaveGameAction
      case "load" | "l" => LoadGameAction
      case _            => super.handleRequest(input, gameState)
    }
  }
}
