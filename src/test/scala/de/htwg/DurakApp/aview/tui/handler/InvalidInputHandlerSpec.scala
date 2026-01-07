package de.htwg.DurakApp.aview.tui.handler
import de.htwg.DurakApp.testutil._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Suit, Rank, GameState, Player}
import de.htwg.DurakApp.testutil.*
import de.htwg.DurakApp.controller.InvalidAction
class InvalidInputHandlerSpec extends AnyWordSpec with Matchers {
  val player1 =
    TestHelper.Player("Alice", List(TestHelper.Card(Suit.Hearts, Rank.Six)))
  val player2 =
    TestHelper.Player("Bob", List(TestHelper.Card(Suit.Diamonds, Rank.Seven)))
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
