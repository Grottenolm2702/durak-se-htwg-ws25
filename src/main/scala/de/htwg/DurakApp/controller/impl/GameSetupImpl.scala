package de.htwg.DurakApp.controller.impl

import de.htwg.DurakApp.controller.GameSetup
import de.htwg.DurakApp.model.{
  Card,
  CardFactory,
  PlayerFactory,
  GameState,
  GameStateFactory,
  Rank,
  Suit
}
import de.htwg.DurakApp.model.builder.GameStateBuilderFactory
import de.htwg.DurakApp.model.state.GamePhases
import com.google.inject.Inject

import scala.util.Random

class GameSetupImpl @Inject() (
    gameStateFactory: GameStateFactory,
    playerFactory: PlayerFactory,
    cardFactory: CardFactory,
    gamePhases: GamePhases,
    gameStateBuilderFactory: GameStateBuilderFactory
) extends GameSetup {

  private def createDeck(requestedDeckSize: Int): List[Card] = {
    val suits = Suit.values.toList
    val standardRanks = Rank.values.toList

    val fullStandardDeck = for {
      suit <- suits
      rank <- standardRanks
    } yield cardFactory(suit, rank)

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
      playerNames.map(name => playerFactory(name, List.empty))

    val initialGameState = gameStateBuilderFactory
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

    Some(initialGameState.gamePhase.handle(initialGameState))
  }
}
