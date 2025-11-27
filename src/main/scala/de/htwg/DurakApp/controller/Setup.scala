package de.htwg.DurakApp.controller

import de.htwg.DurakApp.model.*
import de.htwg.DurakApp.model.state.SetupPhase

import scala.util.Random

object Setup {
  def createDeck(requestedDeckSize: Int): List[Card] = {
    val suits = Suit.values.toList
    val standardRanks = Rank.values.toList

    val fullStandardDeck = for {
      suit <- suits
      rank <- standardRanks
    } yield Card(suit, rank)

    val effectiveDeckSize = requestedDeckSize.min(fullStandardDeck.length)

    Random.shuffle(fullStandardDeck).take(effectiveDeckSize)
  }

  def setupGame(playerNames: List[String], deckSize: Int): GameState = {
    if (playerNames.length < 2)
      throw new IllegalArgumentException("Need at least two players.")

    val deck = createDeck(deckSize)
    val minCardsRequired = playerNames.length * 6 + 1 // Minimum 6 cards per player + 1 for trump
    if (deck.length < minCardsRequired) {
      throw new IllegalArgumentException(
        s"Not enough cards for ${playerNames.length} players. Need at least ${minCardsRequired} cards, but only have ${deck.length}."
      )
    }

    val players = playerNames.map(name => Player(name, List.empty))

    val preSetupState = GameState(
      players = players,
      deck = deck,
      table = Map.empty,
      discardPile = List.empty,
      trumpCard = deck.head,
      attackerIndex = 0,
      defenderIndex = 0,
      gamePhase = SetupPhase,
      lastEvent = None
    )

    preSetupState.gamePhase.handle(preSetupState)
  }
}
