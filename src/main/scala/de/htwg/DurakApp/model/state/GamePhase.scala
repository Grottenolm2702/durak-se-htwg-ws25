package de.htwg.DurakApp.model.state

import de.htwg.DurakApp.model.{Card, GameState, Player}

sealed trait GameEvent
object GameEvent {
  case object InvalidMove extends GameEvent
  case object NotYourTurn extends GameEvent
  case class Attack(card: Card) extends GameEvent
  case class Defend(defenseCard: Card) extends GameEvent
  case object Pass extends GameEvent
  case object Take extends GameEvent
  case object Draw extends GameEvent
  case class RoundEnd(cleared: Boolean) extends GameEvent
  case class GameOver(winner: Player, loser: Option[Player]) extends GameEvent
  case object CannotUndo extends GameEvent
  case object CannotRedo extends GameEvent
  case object AskPlayerCount extends GameEvent
  case object AskPlayerNames extends GameEvent
  case object AskDeckSize extends GameEvent
  case object GameSetupComplete extends GameEvent
  case object SetupError extends GameEvent
}

trait GamePhase {
  def handle(gameState: GameState): GameState

  def playCard(card: Card, playerIdx: Int, gameState: GameState): GameState =
    gameState.copy(lastEvent = Some(GameEvent.InvalidMove))

  def pass(playerIdx: Int, gameState: GameState): GameState =
    gameState.copy(lastEvent = Some(GameEvent.InvalidMove))

  def takeCards(playerIdx: Int, gameState: GameState): GameState =
    gameState.copy(lastEvent = Some(GameEvent.InvalidMove))
}
