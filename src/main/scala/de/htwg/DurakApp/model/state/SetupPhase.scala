package de.htwg.DurakApp.model.state

import de.htwg.DurakApp.model.{Card, GameState, Player, Suit}

import scala.util.Random

case object SetupPhase extends GamePhase {
  override def handle(gameState: GameState): GameState = {
    val shuffledDeck = Random.shuffle(gameState.deck)

    val (dealtPlayers, remainingDeck) = dealCards(gameState.players, shuffledDeck)

    val trumpCard = remainingDeck.last.copy(isTrump = true) // Take last card and mark as trump
    val finalDeck = remainingDeck // Trump card remains in the deck

    val attackerIndex = findFirstAttacker(dealtPlayers, trumpCard.suit)
    val defenderIndex = (attackerIndex + 1) % dealtPlayers.size

    val initialState = GameState(
      players = dealtPlayers,
      deck = finalDeck,
      table = Map.empty,
      discardPile = List.empty,
      trumpCard = trumpCard,
      attackerIndex = attackerIndex,
      defenderIndex = defenderIndex,
      gamePhase = RoundPhase,
      lastEvent = None
    )
    initialState.gamePhase.handle(initialState)
  }

  private def dealCards(players: List[Player], deck: List[Card]): (List[Player], List[Card]) = {
    val numPlayers = players.size
    val handSize = (deck.length / numPlayers).min(6)
    val cardsToDeal = numPlayers * handSize

    val (dealtCards, remainingDeck) = deck.splitAt(cardsToDeal)
    val hands = dealtCards.grouped(numPlayers).toList.transpose

    val updatedPlayers = players.zipWithIndex.map { case (player, idx) =>
      if (idx < hands.size) player.copy(hand = hands(idx)) else player
    }

    (updatedPlayers, remainingDeck)
  }

  private def findFirstAttacker(players: List[Player], trumpSuit: Suit): Int = {
    players.zipWithIndex
      .map { case (player, index) =>
        val lowestTrump = player.hand
          .filter(_.suit == trumpSuit)
          .minByOption(_.rank.value)
        (index, lowestTrump)
      }
      .collect { case (index, Some(card)) => (index, card) }
      .minByOption { case (_, card) => card.rank.value }
      .map { case (index, _) => index }
      .getOrElse(0)
  }
}
