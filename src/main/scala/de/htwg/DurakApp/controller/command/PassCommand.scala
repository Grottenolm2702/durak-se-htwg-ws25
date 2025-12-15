package de.htwg.DurakApp.controller.command

import de.htwg.DurakApp.model.ModelInterface.GameState
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
