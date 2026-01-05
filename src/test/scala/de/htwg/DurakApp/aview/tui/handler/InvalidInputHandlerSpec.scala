package de.htwg.DurakApp.aview.tui.handler

import de.htwg.DurakApp.testutil.TestHelpers._

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Suit, Rank, GameState, Player}
import de.htwg.DurakApp.testutil.TestGamePhases
import de.htwg.DurakApp.controller.InvalidAction

class InvalidInputHandlerSpec extends AnyWordSpec with Matchers {

  val player1 = Player("Alice", List(Card(Suit.Hearts, Rank.Six)))
  val player2 = Player("Bob", List(Card(Suit.Diamonds, Rank.Seven)))
  val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)

  val gameState = GameState(
    players = List(player1, player2),
    deck = List.empty,
    table = Map.empty,
    discardPile = List.empty,
    trumpCard = trumpCard,
    attackerIndex = 0,
    defenderIndex = 1,
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

  "An InvalidInputHandler" should {
    "always return InvalidAction" in {
      val handler = InvalidInputHandler()
      val result = handler.handleRequest("anything", gameState)

      result shouldBe InvalidAction
    }

    "return InvalidAction for empty input" in {
      val handler = InvalidInputHandler()
      val result = handler.handleRequest("", gameState)

      result shouldBe InvalidAction
    }

    "return InvalidAction for any random string" in {
      val handler = InvalidInputHandler()
      val result = handler.handleRequest("xyz123", gameState)

      result shouldBe InvalidAction
    }
  }
}
