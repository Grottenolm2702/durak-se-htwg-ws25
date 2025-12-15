package de.htwg.DurakApp.controller.command

import de.htwg.DurakApp.model.ModelInterface.GameState

case class TakeCardsCommand() extends GameCommand {
  override def execute(gameState: GameState): GameState = {
    val defenderIndex = gameState.defenderIndex
    gameState.gamePhase.takeCards(defenderIndex, gameState)
  }
}
