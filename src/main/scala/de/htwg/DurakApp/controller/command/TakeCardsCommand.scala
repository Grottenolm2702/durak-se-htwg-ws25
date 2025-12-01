package de.htwg.DurakApp.controller.command

import de.htwg.DurakApp.model.GameState

case class TakeCardsCommand() extends GameCommand {
  override def execute(gameState: GameState): GameState = {
    val defenderIdx = gameState.defenderIndex
    gameState.gamePhase.takeCards(defenderIdx, gameState)
  }
}
