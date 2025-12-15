package de.htwg.DurakApp.model.state.impl

import de.htwg.DurakApp.model.GameState
import de.htwg.DurakApp.model.state.GamePhase

private[state] case object AskDeckSizePhaseImpl extends GamePhase {
  override def toString: String = "AskDeckSizePhase"
  
  override def handle(gameState: GameState): GameState =
    gameState
}
