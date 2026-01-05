package de.htwg.DurakApp.model.state.impl

import de.htwg.DurakApp.testutil.TestHelpers._
import de.htwg.DurakApp.testutil.TestGamePhases

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Suit, Rank, GameState, Player}

class AskDeckSizePhaseImplSpec extends AnyWordSpec with Matchers {

  "AskDeckSizePhaseImpl" should {
    "have correct string representation" in {
      AskDeckSizePhaseImpl.toString shouldBe "AskDeckSizePhase"
    }

    "handle returns same state" in {
      val player1 = Player("Alice", List.empty)
      val player2 = Player("Bob", List.empty)
      val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)

      val gameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = AskDeckSizePhaseImpl,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = Some(2),
        setupPlayerNames = List("Alice", "Bob"),
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )

      val result = AskDeckSizePhaseImpl.handle(gameState)
      result shouldBe gameState
    }
  }
}
