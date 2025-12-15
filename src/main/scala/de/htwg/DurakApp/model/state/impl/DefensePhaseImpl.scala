package de.htwg.DurakApp.model.state.impl

import de.htwg.DurakApp.model.{Card, GameState, Suit}
import de.htwg.DurakApp.model.state.{GamePhase, GameEvent}

private[state] case object DefensePhaseImpl extends GamePhase {
  override def toString: String = "DefensePhase"

  override def handle(gameState: GameState): GameState = {
    gameState
  }

  override def playCard(
      card: Card,
      playerIndex: Int,
      gameState: GameState
  ): GameState = {
    if (
      playerIndex < 0 || playerIndex >= gameState.players.size || playerIndex != gameState.defenderIndex
    ) {
      return gameState.copy(lastEvent = Some(GameEvent.NotYourTurn))
    }

    val defendingPlayer = gameState.players(playerIndex)
    if (!defendingPlayer.hand.contains(card)) {
      return gameState.copy(lastEvent = Some(GameEvent.InvalidMove))
    }

    val undefendedAttackCard =
      gameState.table
        .find { case (_, defenseCard) => defenseCard.isEmpty }
        .map(_._1)
    if (undefendedAttackCard.isEmpty) {
      return gameState.copy(lastEvent = Some(GameEvent.InvalidMove))
    }
    val attackingCard = undefendedAttackCard.get

    if (!canDefend(attackingCard, card, gameState.trumpCard.suit)) {
      return gameState.copy(lastEvent = Some(GameEvent.InvalidMove))
    }

    val updatedDefenderHand = defendingPlayer.hand.filterNot(_ == card)
    val updatedPlayers =
      gameState.players.updated(
        playerIndex,
        defendingPlayer.copy(hand = updatedDefenderHand)
      )
    val updatedTable = gameState.table.updated(attackingCard, Some(card))

    val allAttacksDefended = updatedTable.values.forall(_.isDefined)
    val nextPhase = if (allAttacksDefended) AttackPhaseImpl else DefensePhaseImpl

    val nextAttackerIndex = if (allAttacksDefended) {
      val previousAttackerIndex =
        gameState.lastAttackerIndex.getOrElse(gameState.attackerIndex)
      getNextAttacker(gameState, previousAttackerIndex)
    } else None

    gameState.copy(
      players = updatedPlayers,
      table = updatedTable,
      gamePhase = nextPhase,
      lastEvent = Some(GameEvent.Defend(card)),
      currentAttackerIndex = nextAttackerIndex
    )
  }

  private def getNextAttacker(
      gameState: GameState,
      currentAttackerIndex: Int
  ): Option[Int] = {
    val totalPlayers = gameState.players.size
    val mainAttackerIndex = gameState.attackerIndex
    val defenderIndex = gameState.defenderIndex

    val nextAvailablePlayer = (1 until totalPlayers)
      .map { offsetFromCurrent =>
        (currentAttackerIndex + offsetFromCurrent) % totalPlayers
      }
      .find { playerIndex =>
        playerIndex != defenderIndex && !gameState.passedPlayers.contains(
          playerIndex
        )
      }

    nextAvailablePlayer.orElse {
      if (
        !gameState.passedPlayers.contains(
          mainAttackerIndex
        ) && mainAttackerIndex != defenderIndex
      ) {
        Some(mainAttackerIndex)
      } else {
        None
      }
    }
  }

  override def takeCards(playerIndex: Int, gameState: GameState): GameState = {
    if (
      playerIndex < 0 || playerIndex >= gameState.players.size || playerIndex != gameState.defenderIndex
    ) {
      return gameState.copy(lastEvent = Some(GameEvent.NotYourTurn))
    }
    val defendingPlayer = gameState.players(playerIndex)

    val allCardsFromTable =
      gameState.table.keys.toList ++ gameState.table.values.flatten.toList
    val updatedDefenderHand = defendingPlayer.hand ++ allCardsFromTable
    val updatedPlayers =
      gameState.players.updated(
        playerIndex,
        defendingPlayer.copy(hand = updatedDefenderHand)
      )

    val updatedGameState = gameState.copy(
      players = updatedPlayers,
      table = Map.empty,
      gamePhase = DrawPhaseImpl,
      roundWinner = None,
      lastEvent = Some(GameEvent.Take)
    )
    updatedGameState
  }

  private def canDefend(
      attackCard: Card,
      defenseCard: Card,
      trumpSuit: Suit
  ): Boolean = {
    if (
      attackCard.suit == defenseCard.suit && defenseCard.rank.value > attackCard.rank.value
    ) {
      return true
    }
    if (attackCard.suit != trumpSuit && defenseCard.suit == trumpSuit) {
      return true
    }
    false
  }
}
