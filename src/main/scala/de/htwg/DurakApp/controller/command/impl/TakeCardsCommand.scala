package de.htwg.DurakApp.controller.command.impl

import de.htwg.DurakApp.controller.command.GameCommand
import de.htwg.DurakApp.model.ModelInterface.GameState

case class TakeCardsCommand() extends GameCommand {
  override def execute(gameState: GameState): GameState = {
    val defenderIndex = gameState.defenderIndex
    gameState.gamePhase.takeCards(defenderIndex, gameState)
  }
}
