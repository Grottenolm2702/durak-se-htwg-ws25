package de.htwg.DurakApp.aview.tui.handler

import de.htwg.DurakApp.controller.{
  TakeCardsAction,
  PlayerAction,
  InvalidAction
}
import de.htwg.DurakApp.model.GameState

class TakeCardsHandler extends InputHandler {
  override def handleRequest(
      input: String,
      gameState: GameState
  ): PlayerAction = {
    val inputArgs = input.trim.toLowerCase.split("\\s+").toList
    inputArgs.headOption match {
      case Some("take") =>
        TakeCardsAction
      case _ =>
        next.map(_.handleRequest(input, gameState)).getOrElse(InvalidAction)
    }
  }
}
