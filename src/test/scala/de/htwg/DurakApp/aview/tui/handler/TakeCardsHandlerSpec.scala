package de.htwg.DurakApp.aview.tui.handler

import de.htwg.DurakApp.testutil._

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Suit, Rank, GameState, Player}
import de.htwg.DurakApp.testutil.*
import de.htwg.DurakApp.controller.{TakeCardsAction, InvalidAction}

class TakeCardsHandlerSpec extends AnyWordSpec with Matchers {

  val player1 =
    TestHelper.Player("Alice", List(TestHelper.Card(Suit.Hearts, Rank.Six)))
  val player2 =
    TestHelper.Player("Bob", List(TestHelper.Card(Suit.Diamonds, Rank.Seven)))
  val trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true)

  val gameState = TestHelper.GameState(
    players = List(player1, player2),
    deck = List.empty,
    table = Map(TestHelper.Card(Suit.Hearts, Rank.Six) -> None),
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

  "A TakeCardsHandler" should {
    "handle take command" in {
      val handler = TakeCardsHandler()
      val result = handler.handleRequest("take", gameState)

      result shouldBe TakeCardsAction
    }

    "handle uppercase TAKE command" in {
      val handler = TakeCardsHandler()
      val result = handler.handleRequest("TAKE", gameState)

      result shouldBe TakeCardsAction
    }

    "handle take with extra whitespace" in {
      val handler = TakeCardsHandler()
      val result = handler.handleRequest("  take  ", gameState)

      result shouldBe TakeCardsAction
    }

    "delegate to next handler for non-take command" in {
      val nextHandler = InvalidInputHandler()
      val handler = TakeCardsHandler(Some(nextHandler))
      val result = handler.handleRequest("pass", gameState)

      result shouldBe InvalidAction
    }
  }
}
