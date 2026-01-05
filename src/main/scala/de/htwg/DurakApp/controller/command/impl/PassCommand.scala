package de.htwg.DurakApp.controller.command.impl

import de.htwg.DurakApp.controller.command.{GameCommand, PassCommand as PassCommandTrait}
import de.htwg.DurakApp.model.GameState
import de.htwg.DurakApp.model.state.GamePhases

private[command] case class PassCommand(gamePhases: GamePhases) extends PassCommandTrait {
  override def execute(gameState: GameState): GameState = {
    val activePlayerIndex = 
      if (gamePhases.isDefensePhase(gameState.gamePhase)) {
        gameState.defenderIndex
      } else {
        gameState.currentAttackerIndex.getOrElse(gameState.attackerIndex)
      }
    gameState.gamePhase.pass(activePlayerIndex, gameState)
  }
}
