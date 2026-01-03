package de.htwg.DurakApp.controller.command

import de.htwg.DurakApp.model.GameState

trait GameCommand {
  def execute(gameState: GameState): GameState
  def undo(
      currentGameState: GameState,
      previousGameState: GameState
  ): GameState = previousGameState
}
