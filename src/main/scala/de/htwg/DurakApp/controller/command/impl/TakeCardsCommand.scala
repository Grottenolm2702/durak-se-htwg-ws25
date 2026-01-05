package de.htwg.DurakApp.controller.command.impl

import de.htwg.DurakApp.controller.command.{GameCommand, TakeCardsCommand as TakeCardsCommandTrait}
import de.htwg.DurakApp.model.GameState

 case class TakeCardsCommand() extends TakeCardsCommandTrait {
  override def execute(gameState: GameState): GameState = {
    val defenderIndex = gameState.defenderIndex
    gameState.gamePhase.takeCards(defenderIndex, gameState)
  }
}
