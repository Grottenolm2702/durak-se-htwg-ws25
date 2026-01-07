package de.htwg.DurakApp.model.impl

import de.htwg.DurakApp.testutil._

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{GameState, Player, Card, Suit, Rank}
import de.htwg.DurakApp.testutil.*

class GameStateImplSpec extends AnyWordSpec with Matchers {
  "GameStateImpl" should {
    "be created through GameState factory" in {
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace)
      val player = TestHelper.Player("Alice", List(card))
      val gameState = TestHelper.GameState(
        players = List(player),
        deck = List(),
        table = Map(),
        discardPile = List(),
        trumpCard = card,
        attackerIndex = 0,
        defenderIndex = 0,
        gamePhase = StubGamePhases.setupPhase,
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
