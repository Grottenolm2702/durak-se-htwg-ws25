package de.htwg.DurakApp.model.state

import de.htwg.DurakApp.model.GameState

case object AskDeckSizePhase extends GamePhase {
  override def handle(gameState: GameState): GameState =
    gameState
}
