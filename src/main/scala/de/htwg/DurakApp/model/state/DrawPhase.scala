package de.htwg.DurakApp.model.state

import de.htwg.DurakApp.model.{Card, GameState, Player}

case object DrawPhase extends GamePhase {
  override def handle(gameState: GameState): GameState = {
    val attacker = gameState.attackerIndex
    val passiveAttackers = gameState.players.indices.filter(p =>
      p != attacker && p != gameState.defenderIndex && !gameState.passedPlayers
        .contains(p)
    )
    val defender = gameState.defenderIndex

    val drawOrder =
      (List(attacker) ++ passiveAttackers) ++ (if (
                                                 gameState.roundWinner.isDefined
                                               ) List(defender)
                                               else Nil)

    val (finalPlayers, finalDeck) =
      drawOrder.foldLeft((gameState.players, gameState.deck)) {
        case ((currentPlayers, currentDeck), playerIdx) =>
          if (currentDeck.isEmpty) {
            (currentPlayers, currentDeck)
          } else {
            val player = currentPlayers(playerIdx)
            val cardsToDraw = 6 - player.hand.size
            if (cardsToDraw > 0) {
              val (drawnCards, nextDeck) = currentDeck.splitAt(cardsToDraw)
              val updatedPlayer = player.copy(hand = player.hand ++ drawnCards)
              (currentPlayers.updated(playerIdx, updatedPlayer), nextDeck)
            } else {
              (currentPlayers, currentDeck)
            }
          }
      }

    val (nextAttacker, nextDefender) = if (gameState.roundWinner.isDefined) {
      val newAttacker = defender
      (newAttacker, (newAttacker + 1) % finalPlayers.size)
    } else {
      val newAttacker = (defender + 1) % finalPlayers.size
      (newAttacker, (newAttacker + 1) % finalPlayers.size)
    }

    val newState = gameState.copy(
      players = finalPlayers,
      deck = finalDeck,
      attackerIndex = nextAttacker,
      defenderIndex = nextDefender,
      gamePhase = RoundPhase,
      lastEvent = Some(GameEvent.Draw)
    )
    newState.gamePhase.handle(newState)
  }
}
