package de.htwg.DurakApp.controller.command

import de.htwg.DurakApp.model.GameState

trait Command {
  def execute(gameState: GameState): GameState
}
