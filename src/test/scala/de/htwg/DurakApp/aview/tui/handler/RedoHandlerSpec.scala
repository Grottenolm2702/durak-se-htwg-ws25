package de.htwg.DurakApp.aview.tui.handler
import de.htwg.DurakApp.testutil._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Suit, Rank, GameState, Player}
import de.htwg.DurakApp.testutil.*
import de.htwg.DurakApp.controller.{RedoAction, InvalidAction, Controller}
class RedoHandlerSpec extends AnyWordSpec with Matchers {
  def createController(): Controller = {
    val initialState = TestHelper.createTestGameState()
    new SpyController(initialState, new StubUndoRedoManager())
  }
  val controller = createController()
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
  "A RedoHandler" should {
    "handle redo command" in {
      val handler = RedoHandler(controller)
      val result = handler.handleRequest("redo", gameState)
      result shouldBe RedoAction
    }
    "handle y shortcut for redo" in {
      val handler = RedoHandler(controller)
      val result = handler.handleRequest("y", gameState)
      result shouldBe RedoAction
    }
    "handle r shortcut for redo" in {
      val handler = RedoHandler(controller)
      val result = handler.handleRequest("r", gameState)
      result shouldBe RedoAction
    }
    "handle uppercase REDO command" in {
      val handler = RedoHandler(controller)
      val result = handler.handleRequest("REDO", gameState)
      result shouldBe RedoAction
    }
    "delegate to next handler for non-redo command" in {
      val nextHandler = InvalidInputHandler()
      val handler = RedoHandler(controller, Some(nextHandler))
      val result = handler.handleRequest("pass", gameState)
      result shouldBe InvalidAction
    }
    "use default None for next parameter when not provided" in {
      val handler = RedoHandler(controller)
      handler.next shouldBe None
      val result = handler.handleRequest("unknown", gameState)
      result shouldBe InvalidAction
    }
    "fallback to InvalidAction when next is None" in {
      val handler = new RedoHandler(controller)
      val result = handler.handleRequest("something", gameState)
      result shouldBe InvalidAction
    }
  }
}
