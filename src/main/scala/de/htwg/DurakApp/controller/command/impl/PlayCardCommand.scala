package de.htwg.DurakApp.controller.command.impl

import de.htwg.DurakApp.controller.command.GameCommand
import de.htwg.DurakApp.model.{Card, GameState}
import de.htwg.DurakApp.model.state.GameEvent

case class PlayCardCommand(card: Card) extends GameCommand {
  override def execute(gameState: GameState): GameState = {

    val activePlayerIndex =
      if (gameState.gamePhase == de.htwg.DurakApp.model.state.impl.DefensePhaseImpl) {
        gameState.defenderIndex
      } else {
        gameState.currentAttackerIndex.getOrElse(gameState.attackerIndex)
      }

    val activePlayer = gameState.players(activePlayerIndex)

    if (!activePlayer.hand.contains(card)) {
      gameState.copy(lastEvent = Some(GameEvent.InvalidMove))
    } else {
      gameState.gamePhase.playCard(card, activePlayerIndex, gameState)
    }
  }
}
