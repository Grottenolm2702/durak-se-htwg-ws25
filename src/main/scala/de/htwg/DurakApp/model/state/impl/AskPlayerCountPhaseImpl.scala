package de.htwg.DurakApp.model.state.impl

import de.htwg.DurakApp.model.GameState
import de.htwg.DurakApp.model.state.AskPlayerCountPhase

case object AskPlayerCountPhaseImpl extends AskPlayerCountPhase {
  override def toString: String = "AskPlayerCountPhase"

  override def handle(gameState: GameState): GameState =
    gameState
}
