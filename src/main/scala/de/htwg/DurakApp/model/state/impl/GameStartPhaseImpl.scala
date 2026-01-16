package de.htwg.DurakApp.model.state.impl

import de.htwg.DurakApp.model.GameState
import de.htwg.DurakApp.model.state.{GameStartPhase, GameEvent}

case object GameStartPhaseImpl extends GameStartPhase {
  override def toString: String = "GameStartPhase"

  override def handle(gameState: GameState): GameState =
    gameState.copy(lastEvent = Some(GameEvent.GameSetupComplete))
}
