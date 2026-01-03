package de.htwg.DurakApp.model.state.impl

import de.htwg.DurakApp.model.GameState
import de.htwg.DurakApp.model.state.{GamePhase, GameEvent}

case object AskPlayAgainPhaseImpl extends GamePhase {
  override def toString: String = "TestGamePhases.askPlayAgainPhase"

  override def handle(gameState: GameState): GameState = {
    gameState
  }
}
