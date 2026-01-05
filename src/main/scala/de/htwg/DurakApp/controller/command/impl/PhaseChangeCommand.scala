package de.htwg.DurakApp.controller.command.impl

import de.htwg.DurakApp.controller.command.{GameCommand, PhaseChangeCommand as PhaseChangeCommandTrait}
import de.htwg.DurakApp.model.GameState

 case class PhaseChangeCommand() extends PhaseChangeCommandTrait {
  override def execute(gameState: GameState): GameState = gameState
  override def undo(
      currentGameState: GameState,
      previousGameState: GameState
  ): GameState = previousGameState
}
