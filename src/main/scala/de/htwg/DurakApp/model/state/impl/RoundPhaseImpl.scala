package de.htwg.DurakApp.model.state.impl

import de.htwg.DurakApp.model.GameState
import de.htwg.DurakApp.model.state.{RoundPhase, GameEvent}

case object RoundPhaseImpl extends RoundPhase {
  override def toString: String = "RoundPhase"

  override def handle(gameState: GameState): GameState = {
    val remainingPlayersWithCards = gameState.players.filter(_.hand.nonEmpty)
    val gameIsOver =
      remainingPlayersWithCards.size <= 1 && gameState.deck.isEmpty

    if (gameIsOver) {
      return EndPhaseImpl.handle(gameState)
    }

    val updatedDiscardPile = if (gameState.roundWinner.isDefined) {
      val allCardsFromTable =
        gameState.table.keys.toList ++ gameState.table.values.flatten.toList
      gameState.discardPile ++ allCardsFromTable
    } else {
      gameState.discardPile
    }

    val gameStateForNextRound = gameState.copy(
      table = Map.empty,
      discardPile = updatedDiscardPile,
      passedPlayers = Set.empty,
      lastEvent =
        Some(GameEvent.RoundEnd(cleared = gameState.roundWinner.isDefined)),
      roundWinner = None,
      gamePhase = AttackPhaseImpl,
      currentAttackerIndex = None,
      lastAttackerIndex = None
    )

    gameStateForNextRound.gamePhase.handle(gameStateForNextRound)
  }
}
