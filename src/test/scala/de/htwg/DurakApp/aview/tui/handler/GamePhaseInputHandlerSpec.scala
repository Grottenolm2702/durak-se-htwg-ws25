package de.htwg.DurakApp.aview.tui.handler

import de.htwg.DurakApp.testutil._
import de.htwg.DurakApp.testutil.{TestHelper, StubGamePhases, StubGamePhasesImpl}

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Suit, Rank, GameState, Player}
import de.htwg.DurakApp.model.state._
import de.htwg.DurakApp.controller._

class GamePhaseInputHandlerSpec extends AnyWordSpec with Matchers {

  val trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true)

  "A GamePhaseInputHandler" should {
    "handle player count input in StubGamePhases.askPlayerCountPhase" in {
      val gameState = TestHelper.GameState(
        players = List.empty,
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
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

      val handler = new GamePhaseInputHandler(
        None,
        new StubGamePhasesImpl()
      )
      val result = handler.handleRequest("3", gameState)

      result shouldBe SetPlayerCountAction(3)
    }

    "handle player count input in StubGamePhases.setupPhase" in {
      val gameState = TestHelper.GameState(
        players = List.empty,
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
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

      val handler = new GamePhaseInputHandler(
        None,
        new StubGamePhasesImpl()
      )
      val result = handler.handleRequest("2", gameState)

      result shouldBe SetPlayerCountAction(2)
    }

    "return InvalidAction for non-numeric input in StubGamePhases.askPlayerCountPhase" in {
      val gameState = TestHelper.GameState(
        players = List.empty,
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
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

      val handler = new GamePhaseInputHandler(
        None,
        new StubGamePhasesImpl()
      )
      val result = handler.handleRequest("abc", gameState)

      result shouldBe InvalidAction
    }

    "handle player name input in StubGamePhases.askPlayerNamesPhase" in {
      val gameState = TestHelper.GameState(
        players = List.empty,
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 0,
        gamePhase = StubGamePhases.askPlayerNamesPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = Some(2),
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )

      val handler = new GamePhaseInputHandler(
        None,
        new StubGamePhasesImpl()
      )
      val result = handler.handleRequest("Alice", gameState)

      result shouldBe AddPlayerNameAction("Alice")
    }

    "handle deck size input in StubGamePhases.askDeckSizePhase" in {
      val gameState = TestHelper.GameState(
        players = List.empty,
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 0,
        gamePhase = StubGamePhases.askDeckSizePhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = Some(2),
        setupPlayerNames = List("Alice", "Bob"),
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )

      val handler = new GamePhaseInputHandler(
        None,
        new StubGamePhasesImpl()
      )
      val result = handler.handleRequest("36", gameState)

      result shouldBe SetDeckSizeAction(36)
    }

    "return InvalidAction for non-numeric input in StubGamePhases.askDeckSizePhase" in {
      val gameState = TestHelper.GameState(
        players = List.empty,
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 0,
        gamePhase = StubGamePhases.askDeckSizePhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = Some(2),
        setupPlayerNames = List("Alice", "Bob"),
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )

      val handler = new GamePhaseInputHandler(
        None,
        new StubGamePhasesImpl()
      )
      val result = handler.handleRequest("large", gameState)

      result shouldBe InvalidAction
    }

    "handle yes input in StubGamePhases.askPlayAgainPhase" in {
      val player1 = TestHelper.Player("Alice", List.empty)
      val player2 = TestHelper.Player("Bob", List.empty)

      val gameState = TestHelper.GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = StubGamePhases.askPlayAgainPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = Some(2),
        setupPlayerNames = List("Alice", "Bob"),
        setupDeckSize = Some(36),
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )

      val handler = new GamePhaseInputHandler(
        None,
        new StubGamePhasesImpl()
      )
      val result = handler.handleRequest("yes", gameState)

      result shouldBe PlayAgainAction
    }

    "handle no input in StubGamePhases.askPlayAgainPhase" in {
      val player1 = TestHelper.Player("Alice", List.empty)
      val player2 = TestHelper.Player("Bob", List.empty)

      val gameState = TestHelper.GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = StubGamePhases.askPlayAgainPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = Some(2),
        setupPlayerNames = List("Alice", "Bob"),
        setupDeckSize = Some(36),
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )

      val handler = new GamePhaseInputHandler(
        None,
        new StubGamePhasesImpl()
      )
      val result = handler.handleRequest("no", gameState)

      result shouldBe ExitGameAction
    }

    "return InvalidAction for invalid input in StubGamePhases.askPlayAgainPhase" in {
      val player1 = TestHelper.Player("Alice", List.empty)
      val player2 = TestHelper.Player("Bob", List.empty)

      val gameState = TestHelper.GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = StubGamePhases.askPlayAgainPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = Some(2),
        setupPlayerNames = List("Alice", "Bob"),
        setupDeckSize = Some(36),
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )

      val handler = new GamePhaseInputHandler(
        None,
        new StubGamePhasesImpl()
      )
      val result = handler.handleRequest("maybe", gameState)

      result shouldBe InvalidAction
    }

    "delegate to next handler in other phases" in {
      val player1 = TestHelper.Player("Alice", List(TestHelper.Card(Suit.Hearts, Rank.Six)))
      val player2 = TestHelper.Player("Bob", List(TestHelper.Card(Suit.Diamonds, Rank.Seven)))

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

      val nextHandler = InvalidInputHandler()
      val handler =
        GamePhaseInputHandler(Some(nextHandler), new StubGamePhasesImpl())
      val result = handler.handleRequest("test", gameState)

      result shouldBe InvalidAction
    }
  }
}
