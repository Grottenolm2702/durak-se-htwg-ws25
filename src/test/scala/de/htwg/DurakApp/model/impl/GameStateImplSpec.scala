package de.htwg.DurakApp.model.impl

import de.htwg.DurakApp.testutil.TestHelpers._

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{GameState, Player, Card, Suit, Rank}
import de.htwg.DurakApp.testutil.TestGamePhases

class GameStateImplSpec extends AnyWordSpec with Matchers {
  "GameStateImpl" should {
    "be created through GameState factory" in {
      val card = Card(Suit.Hearts, Rank.Ace)
      val player = Player("Alice", List(card))
      val gameState = GameState(
        players = List(player),
        deck = List(),
        table = Map(),
        discardPile = List(),
        trumpCard = card,
        attackerIndex = 0,
        defenderIndex = 0,
        gamePhase = TestGamePhases.setupPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )

      gameState.players should have size 1
      gameState.deck shouldBe empty
    }
  }
}
