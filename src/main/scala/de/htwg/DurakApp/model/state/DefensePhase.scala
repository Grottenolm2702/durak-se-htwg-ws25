package de.htwg.DurakApp.model.state

import de.htwg.DurakApp.model.{Card, GameState, Suit}

case object DefensePhase extends GamePhase {

  override def handle(gameState: GameState): GameState = {
    gameState
  }

  override def playCard(
      card: Card,
      playerIdx: Int,
      gameState: GameState
  ): GameState = {
    if (
      playerIdx < 0 || playerIdx >= gameState.players.size || playerIdx != gameState.defenderIndex
    ) {
      return gameState.copy(lastEvent = Some(GameEvent.NotYourTurn))
    }

    val player = gameState.players(playerIdx)
    if (!player.hand.contains(card)) {
      return gameState.copy(lastEvent = Some(GameEvent.InvalidMove))
    }

    val attackCardOpt =
      gameState.table.find { case (_, defense) => defense.isEmpty }.map(_._1)
    if (attackCardOpt.isEmpty) {
      return gameState.copy(lastEvent = Some(GameEvent.InvalidMove))
    }
    val attackCard = attackCardOpt.get

    if (!canDefend(attackCard, card, gameState.trumpCard.suit)) {
      return gameState.copy(lastEvent = Some(GameEvent.InvalidMove))
    }

    val newHand = player.hand.filterNot(_ == card)
    val newPlayers =
      gameState.players.updated(playerIdx, player.copy(hand = newHand))
    val newTable = gameState.table.updated(attackCard, Some(card))

    val allDefended = newTable.values.forall(_.isDefined)
    val nextPhase = if (allDefended) AttackPhase else DefensePhase

    val nextAttackerIndex = if (allDefended) {
      val prevAttacker =
        gameState.lastAttackerIndex.getOrElse(gameState.attackerIndex)
      getNextAttacker(gameState, prevAttacker)
    } else None

    gameState.copy(
      players = newPlayers,
      table = newTable,
      gamePhase = nextPhase,
      lastEvent = Some(GameEvent.Defend(card)),
      currentAttackerIndex = nextAttackerIndex
    )
  }

  private def getNextAttacker(
      gameState: GameState,
      currentIdx: Int
  ): Option[Int] = {
    val numPlayers = gameState.players.size
    val mainAttacker = gameState.attackerIndex
    val defender = gameState.defenderIndex

    val nextPlayer = (1 until numPlayers)
      .map { offset =>
        (currentIdx + offset) % numPlayers
      }
      .find { idx =>
        idx != defender && !gameState.passedPlayers.contains(idx)
      }

    nextPlayer.orElse {
      if (
        !gameState.passedPlayers.contains(
          mainAttacker
        ) && mainAttacker != defender
      ) {
        Some(mainAttacker)
      } else {
        None
      }
    }
  }

  override def takeCards(playerIdx: Int, gameState: GameState): GameState = {
    if (
      playerIdx < 0 || playerIdx >= gameState.players.size || playerIdx != gameState.defenderIndex
    ) {
      return gameState.copy(lastEvent = Some(GameEvent.NotYourTurn))
    }
    val defender = gameState.players(playerIdx)

    val cardsFromTable =
      gameState.table.keys.toList ++ gameState.table.values.flatten.toList
    val newHand = defender.hand ++ cardsFromTable
    val newPlayers =
      gameState.players.updated(playerIdx, defender.copy(hand = newHand))

    val newState = gameState.copy(
      players = newPlayers,
      table = Map.empty,
      gamePhase = DrawPhase,
      roundWinner = None,
      lastEvent = Some(GameEvent.Take)
    )
    newState
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
