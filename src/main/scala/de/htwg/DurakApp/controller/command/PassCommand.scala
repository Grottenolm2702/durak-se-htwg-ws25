package de.htwg.DurakApp.controller.command

import de.htwg.DurakApp.model.GameState
import de.htwg.DurakApp.model.state.DefensePhase

case class PassCommand() extends GameCommand {
  override def execute(gameState: GameState): GameState = {
    val activePlayerIdx = gameState.gamePhase match {
      case DefensePhase => gameState.defenderIndex
      case _            => gameState.attackerIndex
    }
    gameState.gamePhase.pass(activePlayerIdx, gameState)
  }
}
