package de.htwg.DurakApp.model.state.impl

import de.htwg.DurakApp.model.GameState
import de.htwg.DurakApp.model.state.{AskPlayAgainPhase, GameEvent}

case object AskPlayAgainPhaseImpl extends AskPlayAgainPhase {
  override def toString: String = "AskPlayAgainPhase"

  override def handle(gameState: GameState): GameState = {
    gameState
  }
}
