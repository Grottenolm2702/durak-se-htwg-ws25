package de.htwg.DurakApp.aview.tui.handler

import de.htwg.DurakApp.controller.{
  PlayerAction,
  SetPlayerCountAction,
  AddPlayerNameAction,
  SetDeckSizeAction,
  PlayAgainAction,
  ExitGameAction,
  InvalidAction
}

import de.htwg.DurakApp.model.GameState
import de.htwg.DurakApp.model.state.GamePhases

import scala.util.Try

class GamePhaseInputHandler(
    override val next: Option[InputHandler],
    gamePhases: GamePhases
) extends InputHandler {
  override def handleRequest(input: String, game: GameState): PlayerAction = {
    if (
      game.gamePhase == gamePhases.setupPhase || game.gamePhase == gamePhases.askPlayerCountPhase
    ) {
      Try(input.trim.toInt)
        .map(SetPlayerCountAction.apply)
        .getOrElse(InvalidAction)
    } else if (game.gamePhase == gamePhases.askPlayerNamesPhase) {
      AddPlayerNameAction(input.trim)
    } else if (game.gamePhase == gamePhases.askDeckSizePhase) {
      Try(input.trim.toInt)
        .map(SetDeckSizeAction.apply)
        .getOrElse(InvalidAction)
    } else if (game.gamePhase == gamePhases.askPlayAgainPhase) {
      input.trim.toLowerCase match {
        case "yes" => PlayAgainAction
        case "no"  => ExitGameAction
        case _     => InvalidAction
      }
    } else {
      super.handleRequest(input, game)
    }
  }
}
