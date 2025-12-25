package de.htwg.DurakApp.controller.command.impl

import de.htwg.DurakApp.controller.command.GameCommand
import de.htwg.DurakApp.model.GameState
import de.htwg.DurakApp.model.state.DefensePhase

case class PassCommand() extends GameCommand {
  override def execute(gameState: GameState): GameState = {
    val activePlayerIndex = gameState.gamePhase match {
      case DefensePhase => gameState.defenderIndex
      case _ =>
        gameState.currentAttackerIndex.getOrElse(gameState.attackerIndex)
    }
    gameState.gamePhase.pass(activePlayerIndex, gameState)
  }
}
