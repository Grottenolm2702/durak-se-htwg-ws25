package de.htwg.DurakApp.model.state.impl

import de.htwg.DurakApp.model.GameState
import de.htwg.DurakApp.model.state.AskDeckSizePhase

case object AskDeckSizePhaseImpl extends AskDeckSizePhase {
  override def toString: String = "AskDeckSizePhase"

  override def handle(gameState: GameState): GameState =
    gameState
}
