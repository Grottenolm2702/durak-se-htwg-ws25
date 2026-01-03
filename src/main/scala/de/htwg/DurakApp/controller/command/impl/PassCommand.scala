package de.htwg.DurakApp.controller.command.impl

import de.htwg.DurakApp.controller.command.GameCommand
import de.htwg.DurakApp.model.GameState

case class PassCommand() extends GameCommand {
  override def execute(gameState: GameState): GameState = {
    val activePlayerIndex = 
      if (gameState.gamePhase == de.htwg.DurakApp.model.state.impl.DefensePhaseImpl) {
        gameState.defenderIndex
      } else {
        gameState.currentAttackerIndex.getOrElse(gameState.attackerIndex)
      }
    gameState.gamePhase.pass(activePlayerIndex, gameState)
  }
}
