package de.htwg.DurakApp.aview.tui.handler

import de.htwg.DurakApp.controller.{PlayerAction, PlayCardAction, InvalidAction}
import de.htwg.DurakApp.model.GameState
import de.htwg.DurakApp.model.state.GamePhases

import scala.util.{Failure, Success, Try}

class PlayCardHandler(
    override val next: Option[InputHandler],
    gamePhases: GamePhases
) extends InputHandler {
  override def handleRequest(
      input: String,
      gameState: GameState
  ): PlayerAction = {
    val inputArgs = input.trim.toLowerCase.split("\\s+").toList
    inputArgs.headOption match {
      case Some("play") if inputArgs.length > 1 =>
        Try(inputArgs(1).toInt) match {
          case Success(index) =>
            val activePlayer =
              if (gamePhases.isDefensePhase(gameState.gamePhase)) {
                gameState.players(gameState.defenderIndex)
              } else {
                val idx = gameState.currentAttackerIndex.getOrElse(
                  gameState.mainAttackerIndex
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
