package de.htwg.DurakApp.aview.tui.handler

import de.htwg.DurakApp.controller.ControllerInterface.*
import de.htwg.DurakApp.model.ModelInterface.*
import de.htwg.DurakApp.model.ModelInterface.StateInterface.*

import scala.util.Try

class GamePhaseInputHandler(override val next: Option[InputHandler])
    extends InputHandler {
  override def handleRequest(input: String, game: GameState): PlayerAction = {
    game.gamePhase match {
      case SetupPhase | AskPlayerCountPhase =>
        Try(input.trim.toInt)
          .map(SetPlayerCountAction.apply)
          .getOrElse(InvalidAction)
      case AskPlayerNamesPhase =>
        AddPlayerNameAction(input.trim)
      case AskDeckSizePhase =>
        Try(input.trim.toInt)
          .map(SetDeckSizeAction.apply)
          .getOrElse(InvalidAction)
      case AskPlayAgainPhase =>
        input.trim.toLowerCase match {
          case "yes" => PlayAgainAction
          case "no"  => ExitGameAction
          case _     => InvalidAction
        }
      case _ =>
        super.handleRequest(input, game)
    }
  }
}
