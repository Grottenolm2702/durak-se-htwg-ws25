package de.htwg.DurakApp.controller

import de.htwg.DurakApp.model.*
import de.htwg.DurakApp.model.state.SetupPhase

import de.htwg.DurakApp.model.builder.GameStateBuilder

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

  def setupGame(playerNames: List[String], deckSize: Int): Option[GameState] = {
    if (playerNames.length < 2) {
      return None
    }

    val deck = createDeck(deckSize)
    val minCardsRequired = playerNames.length
    if (deck.length < minCardsRequired) {
      return None
    }

    val players = playerNames.map(name => Player(name, List.empty))

    val preSetupState = GameStateBuilder()
      .withPlayers(players)
      .withDeck(deck)
      .withTable(Map.empty)
      .withDiscardPile(List.empty)
      .withTrumpCard(deck.head)
      .withAttackerIndex(0)
      .withDefenderIndex(0)
      .withGamePhase(SetupPhase)
      .withLastEvent(None)
      .withPassedPlayers(Set.empty)
      .withRoundWinner(None)
      .build()

    Some(preSetupState.gamePhase.handle(preSetupState))
  }
}
