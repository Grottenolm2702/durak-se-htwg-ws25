package de.htwg.DurakApp.aview.tui.handler

import de.htwg.DurakApp.controller.ControllerInterface.{
  PlayerAction,
  PlayCardAction,
  InvalidAction
}
import de.htwg.DurakApp.model.ModelInterface.{GameState, StateInterface}
import de.htwg.DurakApp.model.ModelInterface.StateInterface.DefensePhase

import scala.util.{Failure, Success, Try}

class PlayCardHandler(override val next: Option[InputHandler] = None)
    extends InputHandler {
  override def handleRequest(
      input: String,
      gameState: GameState
  ): PlayerAction = {
    val inputArgs = input.trim.toLowerCase.split("\\s+").toList
    inputArgs.headOption match {
      case Some("play") if inputArgs.length > 1 =>
        Try(inputArgs(1).toInt) match {
          case Success(index) =>
            val activePlayer = gameState.gamePhase match {
              case DefensePhase => gameState.players(gameState.defenderIndex)
              case _ =>
                val idx = gameState.currentAttackerIndex.getOrElse(
                  gameState.attackerIndex
                )
                gameState.players(idx)
            }
            if (index >= 0 && index < activePlayer.hand.length) {
              PlayCardAction(activePlayer.hand(index))
            } else {
              println("Ungültiger Kartenindex.")
              InvalidAction
            }
          case Failure(e) =>
            println(
              s"Ungültige Eingabe (${e.getMessage}). Bitte eine Zahl als Kartenindex angeben."
            )
            InvalidAction
        }
      case _ =>
        super.handleRequest(input, gameState)
    }
  }
}
