package de.htwg.DurakApp.controller.command.impl

import de.htwg.DurakApp.controller.command.GameCommand
import de.htwg.DurakApp.model.ModelInterface.GameState

case class PhaseChangeCommand() extends GameCommand {
  override def execute(gameState: GameState): GameState = gameState
  override def undo(
      currentGameState: GameState,
      previousGameState: GameState
  ): GameState = previousGameState
}
