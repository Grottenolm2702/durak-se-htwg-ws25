package de.htwg.DurakApp.model.state.impl

import de.htwg.DurakApp.model.{Card, GameState, Player, Suit}
import de.htwg.DurakApp.model.state.{GamePhase, SetupPhase}
import de.htwg.DurakApp.model.builder.GameStateBuilder

import scala.util.Random

case object SetupPhaseImpl extends SetupPhase {
  override def toString: String = "SetupPhase"

  override def handle(gameState: GameState): GameState = {
    val deckToUse = gameState.deck

    val (dealtPlayers, remainingDeck) =
      dealCards(gameState.players, deckToUse)

    val (rawTrumpCard, restOfDeck) = if (remainingDeck.isEmpty) {
      (
        deckToUse.last,
        List.empty
      )
    } else {
      (remainingDeck.last, remainingDeck.dropRight(1))
    }
    val trumpCard = rawTrumpCard.copy(isTrump = true)
    val finalDeck = restOfDeck

    val attackerIndex = findFirstAttacker(dealtPlayers, trumpCard.suit)
    val defenderIndex = (attackerIndex + 1) % dealtPlayers.size

    val finalGameState = gameState.copy(
      players = dealtPlayers,
      deck = finalDeck,
      table = Map.empty,
      discardPile = List.empty,
      trumpCard = trumpCard,
      mainAttackerIndex = attackerIndex,
      defenderIndex = defenderIndex,
      gamePhase = RoundPhaseImpl,
      lastEvent = None,
      passedPlayers = Set.empty,
      roundWinner = None
    )

    finalGameState.gamePhase.handle(finalGameState)
  }

  private def dealCards(
      players: List[Player],
      deck: List[Card]
  ): (List[Player], List[Card]) = {
    val numPlayers = players.size
    val handSize = (deck.length / numPlayers).min(6)
    val cardsToDeal = numPlayers * handSize
    val (dealtCards, remainingDeck) = deck.splitAt(cardsToDeal)

    val handsByPlayer: List[List[Card]] =
      if (handSize == 0) List.fill(numPlayers)(List.empty)
      else dealtCards.grouped(numPlayers).toList.transpose

    val updatedPlayers = players.zipWithIndex.map { case (player, idx) =>
      player.copy(hand = handsByPlayer(idx))
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
