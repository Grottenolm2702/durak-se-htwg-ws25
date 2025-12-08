package de.htwg.DurakApp.model.state

import de.htwg.DurakApp.model.GameState
import de.htwg.DurakApp.model.state.GameEvent

case object AskPlayAgainPhase extends GamePhase {
  override def handle(gameState: GameState): GameState = {
    // The lastEvent should already be GameEvent.GameOver from EndPhase.
    // This phase should not modify the lastEvent, but rather wait for user input.
    gameState
  }
}
