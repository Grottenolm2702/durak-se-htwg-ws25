package de.htwg.DurakApp.model.state

import de.htwg.DurakApp.model.GameState

case object RoundPhase extends GamePhase {
  override def handle(gameState: GameState): GameState = {
    val playersWithCards = gameState.players.filter(_.hand.nonEmpty)
    if (playersWithCards.size <= 1 && gameState.deck.isEmpty) {
      return EndPhase.handle(gameState)
    }

    val newDiscardPile = if (gameState.roundWinner.isDefined) {
      val cardsFromTable =
        gameState.table.keys.toList ++ gameState.table.values.flatten.toList
      gameState.discardPile ++ cardsFromTable
    } else {
      gameState.discardPile
    }

    val stateForNewRound = gameState.copy(
      table = Map.empty,
      discardPile = newDiscardPile,
      passedPlayers = Set.empty,
      lastEvent =
        Some(GameEvent.RoundEnd(cleared = gameState.roundWinner.isDefined)),
      roundWinner = None,
      gamePhase = AttackPhase,
      currentAttackerIndex = None,
      lastAttackerIndex = None
    )

    stateForNewRound.gamePhase.handle(stateForNewRound)
  }
}
