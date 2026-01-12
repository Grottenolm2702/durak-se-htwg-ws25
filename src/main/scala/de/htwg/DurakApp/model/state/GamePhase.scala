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

trait SetupPhase extends GamePhase
trait AskPlayerCountPhase extends GamePhase
trait AskPlayerNamesPhase extends GamePhase
trait AskDeckSizePhase extends GamePhase
trait AskPlayAgainPhase extends GamePhase
trait GameStartPhase extends GamePhase
trait AttackPhase extends GamePhase
trait DefensePhase extends GamePhase
trait DrawPhase extends GamePhase
trait RoundPhase extends GamePhase
trait EndPhase extends GamePhase
