package de.htwg.DurakApp.model.state.impl

import de.htwg.DurakApp.testutil.TestHelpers._

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Suit, Rank, GameState, Player}

class AskPlayAgainPhaseImplSpec extends AnyWordSpec with Matchers {

  "AskPlayAgainPhaseImpl" should {
    "have correct string representation" in {
      AskPlayAgainPhaseImpl.toString shouldBe "AskPlayAgainPhase"
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
        gamePhase = AskPlayAgainPhaseImpl,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      
      val result = AskPlayAgainPhaseImpl.handle(gameState)
      result shouldBe gameState
    }
  }
}
