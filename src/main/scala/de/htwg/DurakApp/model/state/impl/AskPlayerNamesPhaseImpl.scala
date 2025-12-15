package de.htwg.DurakApp.model.state.impl

import de.htwg.DurakApp.model.GameState
import de.htwg.DurakApp.model.state.GamePhase

private[state] case object AskPlayerNamesPhaseImpl extends GamePhase {
  override def toString: String = "AskPlayerNamesPhase"
  
  override def handle(gameState: GameState): GameState =
    gameState
}
