package de.htwg.DurakApp.model.state

import de.htwg.DurakApp.model.{Card, GameState, Player}

case object DrawPhase extends GamePhase {
  override def handle(gameState: GameState): GameState = {
    val mainAttackerIndex = gameState.attackerIndex
    val otherAttackersIndices = gameState.players.indices.filter(playerIndex =>
      playerIndex != mainAttackerIndex &&
        playerIndex != gameState.defenderIndex &&
        !gameState.passedPlayers.contains(playerIndex)
    )
    val defenderIndex = gameState.defenderIndex

    val cardDrawOrder =
      (List(mainAttackerIndex) ++ otherAttackersIndices) ++
        (if (gameState.roundWinner.isDefined) List(defenderIndex) else Nil)

    val (playersWithDrawnCards, remainingDeck) =
      cardDrawOrder.foldLeft((gameState.players, gameState.deck)) {
        case ((currentPlayers, currentDeck), playerIndex) =>
          if (currentDeck.isEmpty) {
            (currentPlayers, currentDeck)
          } else {
            val player = currentPlayers(playerIndex)
            val cardsNeeded = 6 - player.hand.size
            if (cardsNeeded > 0) {
              val (drawnCards, deckAfterDraw) = currentDeck.splitAt(cardsNeeded)
              val playerWithNewCards =
                player.copy(hand = player.hand ++ drawnCards)
              (
                currentPlayers.updated(playerIndex, playerWithNewCards),
                deckAfterDraw
              )
            } else {
              (currentPlayers, currentDeck)
            }
          }
      }

    val (nextAttackerIndex, nextDefenderIndex) =
      if (gameState.roundWinner.isDefined) {
        val successfulDefenderBecomesAttacker = defenderIndex
        (
          successfulDefenderBecomesAttacker,
          (successfulDefenderBecomesAttacker + 1) % playersWithDrawnCards.size
        )
      } else {
        val playerAfterDefenderBecomesAttacker =
          (defenderIndex + 1) % playersWithDrawnCards.size
        (
          playerAfterDefenderBecomesAttacker,
          (playerAfterDefenderBecomesAttacker + 1) % playersWithDrawnCards.size
        )
      }

    val updatedGameState = gameState.copy(
      players = playersWithDrawnCards,
      deck = remainingDeck,
      attackerIndex = nextAttackerIndex,
      defenderIndex = nextDefenderIndex,
      gamePhase = RoundPhase,
      lastEvent = Some(GameEvent.Draw)
    )
    updatedGameState
  }
}
