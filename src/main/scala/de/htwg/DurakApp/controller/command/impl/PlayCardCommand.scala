package de.htwg.DurakApp.controller.command.impl

import de.htwg.DurakApp.controller.command.{
  GameCommand,
  PlayCardCommand as PlayCardCommandTrait
}
import de.htwg.DurakApp.model.{Card, GameState}
import de.htwg.DurakApp.model.state.{GameEvent, GamePhases}

case class PlayCardCommand(card: Card, gamePhases: GamePhases)
    extends PlayCardCommandTrait {
  override def execute(gameState: GameState): GameState = {

    val activePlayerIndex =
      if (gamePhases.isDefensePhase(gameState.gamePhase)) {
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
