package de.htwg.DurakApp.model.state.impl

import de.htwg.DurakApp.model.GameState
import de.htwg.DurakApp.model.state.AskPlayerNamesPhase

case object AskPlayerNamesPhaseImpl extends AskPlayerNamesPhase {
  override def toString: String = "AskPlayerNamesPhase"

  override def handle(gameState: GameState): GameState =
    gameState
}
