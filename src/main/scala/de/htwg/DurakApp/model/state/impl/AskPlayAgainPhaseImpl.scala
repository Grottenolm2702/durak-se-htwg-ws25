package de.htwg.DurakApp.model.state.impl

import de.htwg.DurakApp.model.GameState
import de.htwg.DurakApp.model.state.{GamePhase, GameEvent}

private[state] case object AskPlayAgainPhaseImpl extends GamePhase {
  override def toString: String = "AskPlayAgainPhase"

  override def handle(gameState: GameState): GameState = {
    gameState
  }
}
