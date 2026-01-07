package de.htwg.DurakApp.aview.tui.handler

import de.htwg.DurakApp.testutil._
import de.htwg.DurakApp.testutil.{
  TestHelper,
  StubGamePhases,
  StubGamePhasesImpl
}

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Suit, Rank, GameState, Player}

import de.htwg.DurakApp.controller.{PlayCardAction, InvalidAction}

class PlayCardHandlerSpec extends AnyWordSpec with Matchers {

  "A PlayCardHandler" should {
    "handle play command with valid index in StubGamePhases.attackPhase" in {
      val card1 = TestHelper.Card(Suit.Hearts, Rank.Six)
      val card2 = TestHelper.Card(Suit.Hearts, Rank.Seven)
      val player1 = TestHelper.Player("Alice", List(card1, card2))
      val player2 = TestHelper.Player(
        "Bob",
        List(TestHelper.Card(Suit.Diamonds, Rank.Eight))
      )
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
        currentAttackerIndex = Some(0),
        lastAttackerIndex = None
      )

      val handler = PlayCardHandler(None, new StubGamePhasesImpl())
      val result = handler.handleRequest("play 0", gameState)

      result shouldBe PlayCardAction(card1)
    }

    "handle play command with valid index in StubGamePhases.defensePhase" in {
      val card1 = TestHelper.Card(Suit.Hearts, Rank.Six)
      val card2 = TestHelper.Card(Suit.Hearts, Rank.Seven)
      val player1 = TestHelper.Player(
        "Alice",
        List(TestHelper.Card(Suit.Diamonds, Rank.Eight))
      )
      val player2 = TestHelper.Player("Bob", List(card1, card2))
      val trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true)

      val gameState = TestHelper.GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map(TestHelper.Card(Suit.Hearts, Rank.Six) -> None),
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = StubGamePhases.defensePhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )

      val handler = PlayCardHandler(None, new StubGamePhasesImpl())
      val result = handler.handleRequest("play 1", gameState)

      result shouldBe PlayCardAction(card2)
    }

    "return InvalidAction for invalid card index" in {
      val player1 =
        TestHelper.Player("Alice", List(TestHelper.Card(Suit.Hearts, Rank.Six)))
      val player2 = TestHelper.Player(
        "Bob",
        List(TestHelper.Card(Suit.Diamonds, Rank.Seven))
      )
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
        currentAttackerIndex = Some(0),
        lastAttackerIndex = None
      )

      val handler = PlayCardHandler(None, new StubGamePhasesImpl())
      val result = handler.handleRequest("play 5", gameState)

      result shouldBe InvalidAction
    }

    "return InvalidAction for non-numeric index" in {
      val player1 =
        TestHelper.Player("Alice", List(TestHelper.Card(Suit.Hearts, Rank.Six)))
      val player2 = TestHelper.Player(
        "Bob",
        List(TestHelper.Card(Suit.Diamonds, Rank.Seven))
      )
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
        currentAttackerIndex = Some(0),
        lastAttackerIndex = None
      )

      val handler = PlayCardHandler(None, new StubGamePhasesImpl())
      val result = handler.handleRequest("play abc", gameState)

      result shouldBe InvalidAction
    }

    "delegate to next handler for non-play command" in {
      val player1 =
        TestHelper.Player("Alice", List(TestHelper.Card(Suit.Hearts, Rank.Six)))
      val player2 = TestHelper.Player(
        "Bob",
        List(TestHelper.Card(Suit.Diamonds, Rank.Seven))
      )
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
        currentAttackerIndex = Some(0),
        lastAttackerIndex = None
      )

      val nextHandler = InvalidInputHandler()
      val handler = PlayCardHandler(Some(nextHandler), new StubGamePhasesImpl())
      val result = handler.handleRequest("pass", gameState)

      result shouldBe InvalidAction
    }

    "handle uppercase PLAY command" in {
      val card1 = TestHelper.Card(Suit.Hearts, Rank.Six)
      val player1 = TestHelper.Player("Alice", List(card1))
      val player2 = TestHelper.Player(
        "Bob",
        List(TestHelper.Card(Suit.Diamonds, Rank.Seven))
      )
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
        currentAttackerIndex = Some(0),
        lastAttackerIndex = None
      )

      val handler = PlayCardHandler(None, new StubGamePhasesImpl())
      val result = handler.handleRequest("PLAY 0", gameState)

      result shouldBe PlayCardAction(card1)
    }

    "use attackerIndex when currentAttackerIndex is None in attack phase" in {
      val card1 = TestHelper.Card(Suit.Hearts, Rank.Six)
      val player1 = TestHelper.Player("Alice", List(card1))
      val player2 = TestHelper.Player(
        "Bob",
        List(TestHelper.Card(Suit.Diamonds, Rank.Seven))
      )
      val trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true)

      val gameState = TestHelper.GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = StubGamePhases.attackPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )

      val handler = PlayCardHandler(None, new StubGamePhasesImpl())
      val result = handler.handleRequest("play 0", gameState)

      result shouldBe PlayCardAction(card1)
    }

    "handle negative index gracefully" in {
      val player1 =
        TestHelper.Player("Alice", List(TestHelper.Card(Suit.Hearts, Rank.Six)))
      val player2 = TestHelper.Player(
        "Bob",
        List(TestHelper.Card(Suit.Diamonds, Rank.Seven))
      )
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
        currentAttackerIndex = Some(0),
        lastAttackerIndex = None
      )

      val handler = PlayCardHandler(None, new StubGamePhasesImpl())
      val result = handler.handleRequest("play -1", gameState)

      result shouldBe InvalidAction
    }
  }
}
