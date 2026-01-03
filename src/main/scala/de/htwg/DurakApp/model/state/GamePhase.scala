package de.htwg.DurakApp.model.state

import de.htwg.DurakApp.model.{Card, GameState}

trait GamePhase {
  def handle(gameState: GameState): GameState

  def playCard(card: Card, playerIdx: Int, gameState: GameState): GameState =
    gameState.copy(lastEvent = Some(GameEvent.InvalidMove))

  def pass(playerIdx: Int, gameState: GameState): GameState =
    gameState.copy(lastEvent = Some(GameEvent.InvalidMove))

  def takeCards(playerIdx: Int, gameState: GameState): GameState =
    gameState.copy(lastEvent = Some(GameEvent.InvalidMove))
}
