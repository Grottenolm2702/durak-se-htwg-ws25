package de.htwg.DurakApp.aview.tui.handler

import de.htwg.DurakApp.testutil._

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Suit, Rank, GameState, Player}
import de.htwg.DurakApp.testutil.*
import de.htwg.DurakApp.controller.{PassAction, InvalidAction}

class PassHandlerSpec extends AnyWordSpec with Matchers {

  val player1 = TestHelper.Player("Alice", List(TestHelper.Card(Suit.Hearts, Rank.Six)))
  val player2 = TestHelper.Player("Bob", List(TestHelper.Card(Suit.Diamonds, Rank.Seven)))
  val trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true)

  val gameState = TestHelper.GameState(
    players = List(player1, player2),
    deck = List.empty,
    table = Map.empty,
    discardPile = List.empty,
    trumpCard = trumpCard,
    attackerIndex = 0,
    defenderIndex = 1,
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

  "A PassHandler" should {
    "handle pass command" in {
      val handler = PassHandler()
      val result = handler.handleRequest("pass", gameState)

      result shouldBe PassAction
    }

    "handle uppercase PASS command" in {
      val handler = PassHandler()
      val result = handler.handleRequest("PASS", gameState)

      result shouldBe PassAction
    }

    "handle pass with extra whitespace" in {
      val handler = PassHandler()
      val result = handler.handleRequest("  pass  ", gameState)

      result shouldBe PassAction
    }

    "delegate to next handler for non-pass command" in {
      val nextHandler = InvalidInputHandler()
      val handler = PassHandler(Some(nextHandler))
      val result = handler.handleRequest("play 0", gameState)

      result shouldBe InvalidAction
    }
  }
}
