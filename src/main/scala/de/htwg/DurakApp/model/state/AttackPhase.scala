package de.htwg.DurakApp.model.state

import de.htwg.DurakApp.model.{Card, GameState}

case object AttackPhase extends GamePhase {
  override def handle(gameState: GameState): GameState = {
    gameState
  }

  override def playCard(
      card: Card,
      playerIdx: Int,
      gameState: GameState
  ): GameState = {
    if (playerIdx < 0 || playerIdx >= gameState.players.size) {
      return gameState.copy(lastEvent = Some(GameEvent.InvalidMove))
    }
    if (
      playerIdx != gameState.attackerIndex && !gameState.passedPlayers.contains(
        playerIdx
      )
    ) {
      return gameState.copy(lastEvent = Some(GameEvent.NotYourTurn))
    }

    val player = gameState.players(playerIdx)
    if (!player.hand.contains(card)) {
      return gameState.copy(lastEvent = Some(GameEvent.InvalidMove))
    }

    val tableRanks = gameState.table.keys
      .map(_.rank)
      .toSet ++ gameState.table.values.flatten.map(_.rank).toSet
    if (gameState.table.nonEmpty && !tableRanks.contains(card.rank)) {
      return gameState.copy(lastEvent = Some(GameEvent.InvalidMove))
    }

    val defender = gameState.players(gameState.defenderIndex)
    val openAttacks = gameState.table.values.count(_.isEmpty)
    if (gameState.table.size >= 6 || openAttacks >= defender.hand.size) {
      return gameState.copy(lastEvent = Some(GameEvent.InvalidMove))
    }

    val newHand = player.hand.filterNot(_ == card)
    val newPlayers =
      gameState.players.updated(playerIdx, player.copy(hand = newHand))
    val newTable = gameState.table + (card -> None)

    gameState.copy(
      players = newPlayers,
      table = newTable,
      gamePhase = DefensePhase,
      lastEvent = Some(GameEvent.Attack(card))
    )
  }

  override def pass(playerIdx: Int, gameState: GameState): GameState = {
    if (playerIdx < 0 || playerIdx >= gameState.players.size) {
      return gameState.copy(lastEvent = Some(GameEvent.InvalidMove))
    }
    if (
      playerIdx != gameState.attackerIndex && !gameState.passedPlayers.contains(
        playerIdx
      )
    ) {
      return gameState.copy(lastEvent = Some(GameEvent.NotYourTurn))
    }

    if (gameState.table.isEmpty) {
      return gameState.copy(lastEvent = Some(GameEvent.InvalidMove))
    }

    if (playerIdx == gameState.attackerIndex) {
      val newState = gameState.copy(
        gamePhase = DrawPhase,
        roundWinner = Some(gameState.defenderIndex),
        lastEvent = Some(GameEvent.Pass)
      )
      newState.gamePhase.handle(newState)
    } else {
      gameState.copy(passedPlayers = gameState.passedPlayers + playerIdx)
    }
  }
}
