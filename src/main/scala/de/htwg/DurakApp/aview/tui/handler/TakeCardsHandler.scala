package de.htwg.DurakApp.aview.tui.handler

import de.htwg.DurakApp.controller.{
  TakeCardsAction,
  PlayerAction,
  InvalidAction,
  Controller
}
import de.htwg.DurakApp.model.GameState

class TakeCardsHandler(override val next: Option[InputHandler] = None)
    extends InputHandler {
  override def handleRequest(
      input: String,
      gameState: GameState
  ): PlayerAction = {
    val inputArgs = input.trim.toLowerCase.split("\\s+").toList
    inputArgs.headOption match {
      case Some("take") =>
        TakeCardsAction
      case _ =>
        super.handleRequest(input, gameState)
    }
  }
}
