package de.htwg.DurakApp.controller.command

import de.htwg.DurakApp.model.state.{DefensePhase, GameEvent}
import de.htwg.DurakApp.model.GameState

case class PlayCardCommand(cardIdentifier: String) extends Command {
  override def execute(gameState: GameState): GameState = {
    val (playerIdx, playerHand) = gameState.gamePhase match {
      case DefensePhase => (gameState.defenderIndex, gameState.players(gameState.defenderIndex).hand)
      case _ => (gameState.attackerIndex, gameState.players(gameState.attackerIndex).hand)
    }

    cardIdentifier.toIntOption match {
      case Some(index) =>
        if (index >= 0 && index < playerHand.length) {
          val card = playerHand(index)
          gameState.gamePhase.playCard(card, playerIdx, gameState)
        } else {
          gameState.copy(lastEvent = Some(GameEvent.InvalidMove))
        }
      case None =>
        gameState.copy(lastEvent = Some(GameEvent.InvalidMove))
    }
  }
}
