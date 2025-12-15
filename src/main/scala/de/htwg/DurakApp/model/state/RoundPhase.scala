package de.htwg.DurakApp.model.state

import de.htwg.DurakApp.model.GameState

case object RoundPhase extends GamePhase {
  override def handle(gameState: GameState): GameState = {
    val remainingPlayersWithCards = gameState.players.filter(_.hand.nonEmpty)
    val gameIsOver =
      remainingPlayersWithCards.size <= 1 && gameState.deck.isEmpty

    if (gameIsOver) {
      return EndPhase.handle(gameState)
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
      gamePhase = AttackPhase,
      currentAttackerIndex = None,
      lastAttackerIndex = None
    )

    gameStateForNextRound.gamePhase.handle(gameStateForNextRound)
  }
}
