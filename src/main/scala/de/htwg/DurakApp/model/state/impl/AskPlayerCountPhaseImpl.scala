package de.htwg.DurakApp.model.state.impl

import de.htwg.DurakApp.model.GameState
import de.htwg.DurakApp.model.state.GamePhase

case object AskPlayerCountPhaseImpl extends GamePhase {
  override def toString: String = "AskPlayerCountPhase"

  override def handle(gameState: GameState): GameState =
    gameState
}
