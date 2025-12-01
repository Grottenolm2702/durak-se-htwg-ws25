package de.htwg.DurakApp.aview.tui.handler

import de.htwg.DurakApp.controller.{PlayerAction, PlayCardAction, InvalidAction}
import de.htwg.DurakApp.model.GameState
import de.htwg.DurakApp.model.state.DefensePhase

import scala.util.Try

class PlayCardHandler(override val next: Option[InputHandler] = None)
    extends InputHandler {
  override def handleRequest(
      input: String,
      gameState: GameState
  ): PlayerAction = {
    val inputArgs = input.trim.toLowerCase.split("\\s+").toList
    inputArgs.headOption match {
      case Some("play") if inputArgs.length > 1 =>
        Try(inputArgs(1).toInt).toOption match {
          case Some(index) =>
            val activePlayer = gameState.gamePhase match {
              case DefensePhase => gameState.players(gameState.defenderIndex)
              case _            => gameState.players(gameState.attackerIndex)
            }
            if (index >= 0 && index < activePlayer.hand.length) {
              PlayCardAction(activePlayer.hand(index))
            } else {
              InvalidAction
            }
          case None => InvalidAction
        }
      case _ =>
        super.handleRequest(input, gameState)
    }
  }
}
