package de.htwg.DurakApp.model.state.impl

import de.htwg.DurakApp.model.GameState
import de.htwg.DurakApp.model.state.{GamePhase, GameEvent}

private[state] case object GameStartPhaseImpl extends GamePhase {
  override def toString: String = "GameStartPhase"
  
  override def handle(gameState: GameState): GameState =
    gameState.copy(lastEvent = Some(GameEvent.GameSetupComplete))
}
