package de.htwg.DurakApp.controller.impl

import de.htwg.DurakApp.controller.GameSetup
import de.htwg.DurakApp.model.{Card, Player, GameState, Rank, Suit}
import de.htwg.DurakApp.model.builder.GameStateBuilderFactory
import de.htwg.DurakApp.model.state.GamePhases
import com.google.inject.Inject

import scala.util.Random

class GameSetupImpl @Inject() (
    gamePhases: GamePhases,
    gameStateBuilderFactory: GameStateBuilderFactory
) extends GameSetup {

  private def createDeck(requestedDeckSize: Int): List[Card] = {
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
    val minimumPlayersRequired = 2
    if (playerNames.length < minimumPlayersRequired) {
      return None
    }

    val shuffledDeck = createDeck(deckSize)
    val minimumCardsNeeded = playerNames.length
    if (shuffledDeck.length < minimumCardsNeeded) {
      return None
    }

    val playersWithEmptyHands =
      playerNames.map(name => Player(name, List.empty))

    gameStateBuilderFactory
      .create()
      .withPlayers(playersWithEmptyHands)
      .withDeck(shuffledDeck)
      .withTable(Map.empty)
      .withDiscardPile(List.empty)
      .withTrumpCard(shuffledDeck.head)
      .withAttackerIndex(0)
      .withDefenderIndex(0)
      .withGamePhase(gamePhases.setupPhase)
      .withLastEvent(None)
      .withPassedPlayers(Set.empty)
      .withRoundWinner(None)
      .build()
      .toOption
      .map(initialGameState =>
        initialGameState.gamePhase.handle(initialGameState)
      )
  }
}
