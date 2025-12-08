package de.htwg.DurakApp.model.state

import de.htwg.DurakApp.model.GameState
import de.htwg.DurakApp.model.state.GameEvent

case object GameStartPhase extends GamePhase {
  override def handle(gameState: GameState): GameState =
    gameState.copy(lastEvent =
      Some(GameEvent.GameSetupComplete)
    )
}
