package de.htwg.DurakApp.model.state.impl

import de.htwg.DurakApp.testutil._
import de.htwg.DurakApp.testutil.*

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Suit, Rank, GameState, Player}

class AskPlayerNamesPhaseImplSpec extends AnyWordSpec with Matchers {

  "AskPlayerNamesPhaseImpl" should {
    "have correct string representation" in {
      AskPlayerNamesPhaseImpl.toString shouldBe "AskPlayerNamesPhase"
    }

    "handle returns same state" in {
      val player1 = TestHelper.Player("Alice", List.empty)
      val player2 = TestHelper.Player("Bob", List.empty)
      val trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true)

      val gameState = TestHelper.GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = AskPlayerNamesPhaseImpl,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = Some(2),
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )

      val result = AskPlayerNamesPhaseImpl.handle(gameState)
      result shouldBe gameState
    }
  }
}
