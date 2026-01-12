package de.htwg.DurakApp.model.state.impl

import de.htwg.DurakApp.model.{Card, GameState}
import de.htwg.DurakApp.model.state.{AttackPhase, GameEvent}

case object AttackPhaseImpl extends AttackPhase {
  override def toString: String = "AttackPhase"

  override def handle(gameState: GameState): GameState = {
    if (gameState.currentAttackerIndex.isEmpty) {
      gameState.copy(currentAttackerIndex = Some(gameState.mainAttackerIndex))
    } else {
      gameState
    }
  }

  private def getNextAttacker(gameState: GameState): Option[Int] = {
    val currentAttackerIndex =
      gameState.currentAttackerIndex.getOrElse(gameState.mainAttackerIndex)
    val totalPlayers = gameState.players.size
    val mainAttackerIndex = gameState.mainAttackerIndex

    val nextAvailableAttacker = (1 until totalPlayers)
      .map { offsetFromCurrent =>
        (currentAttackerIndex + offsetFromCurrent) % totalPlayers
      }
      .find { playerIndex =>
        playerIndex != gameState.defenderIndex &&
        playerIndex != mainAttackerIndex &&
        !gameState.passedPlayers.contains(playerIndex)
      }

    nextAvailableAttacker.orElse {
      if (
        !gameState.passedPlayers.contains(
          mainAttackerIndex
        ) && currentAttackerIndex != mainAttackerIndex
      ) {
        Some(mainAttackerIndex)
      } else {
        None
      }
    }
  }

  override def playCard(
      card: Card,
      playerIndex: Int,
      gameState: GameState
  ): GameState = {
    if (playerIndex < 0 || playerIndex >= gameState.players.size) {
      return gameState.copy(lastEvent = Some(GameEvent.InvalidMove))
    }
    if (playerIndex == gameState.defenderIndex) {
      return gameState.copy(lastEvent = Some(GameEvent.NotYourTurn))
    }

    val expectedAttackerIndex =
      gameState.currentAttackerIndex.getOrElse(gameState.mainAttackerIndex)
    if (playerIndex != expectedAttackerIndex) {
      return gameState.copy(lastEvent = Some(GameEvent.NotYourTurn))
    }

    val attackingPlayer = gameState.players(playerIndex)
    if (!attackingPlayer.hand.contains(card)) {
      return gameState.copy(lastEvent = Some(GameEvent.InvalidMove))
    }

    val ranksOnTable = gameState.table.keys
      .map(_.rank)
      .toSet ++ gameState.table.values.flatten.map(_.rank).toSet
    if (gameState.table.nonEmpty && !ranksOnTable.contains(card.rank)) {
      return gameState.copy(lastEvent = Some(GameEvent.InvalidMove))
    }

    val defendingPlayer = gameState.players(gameState.defenderIndex)
    val undefendedAttacks = gameState.table.values.count(_.isEmpty)
    if (
      gameState.table.size >= 6 || undefendedAttacks >= defendingPlayer.hand.size
    ) {
      return gameState.copy(lastEvent = Some(GameEvent.InvalidMove))
    }

    val updatedPlayerHand = attackingPlayer.hand.filterNot(_ == card)
    val updatedPlayers =
      gameState.players.updated(
        playerIndex,
        attackingPlayer.copy(hand = updatedPlayerHand)
      )
    val updatedTable = gameState.table + (card -> None)

    gameState.copy(
      players = updatedPlayers,
      table = updatedTable,
      gamePhase = DefensePhaseImpl,
      lastEvent = Some(GameEvent.Attack(card)),
      currentAttackerIndex = None,
      lastAttackerIndex = Some(playerIndex)
    )
  }

  override def pass(playerIndex: Int, gameState: GameState): GameState = {
    if (playerIndex < 0 || playerIndex >= gameState.players.size) {
      return gameState.copy(lastEvent = Some(GameEvent.InvalidMove))
    }
    if (playerIndex == gameState.defenderIndex) {
      return gameState.copy(lastEvent = Some(GameEvent.NotYourTurn))
    }

    val expectedAttackerIndex =
      gameState.currentAttackerIndex.getOrElse(gameState.mainAttackerIndex)
    if (playerIndex != expectedAttackerIndex) {
      return gameState.copy(lastEvent = Some(GameEvent.NotYourTurn))
    }

    if (gameState.table.isEmpty) {
      return gameState.copy(lastEvent = Some(GameEvent.InvalidMove))
    }

    val updatedPassedPlayers = gameState.passedPlayers + playerIndex
    val nextAttackerIndex = getNextAttacker(
      gameState.copy(passedPlayers = updatedPassedPlayers)
    )

    nextAttackerIndex match {
      case Some(nextPlayerIndex) =>
        gameState.copy(
          passedPlayers = updatedPassedPlayers,
          currentAttackerIndex = Some(nextPlayerIndex),
          lastEvent = Some(GameEvent.Pass)
        )
      case None =>
        gameState.copy(
          gamePhase = DrawPhaseImpl,
          roundWinner = Some(gameState.defenderIndex),
          lastEvent = Some(GameEvent.Pass),
          currentAttackerIndex = None
        )
    }
  }
}
