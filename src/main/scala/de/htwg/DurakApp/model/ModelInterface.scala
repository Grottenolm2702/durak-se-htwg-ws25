package de.htwg.DurakApp.model

import de.htwg.DurakApp.model.state.GamePhase

trait ModelInterface {
  def getGameState: GameState
}
