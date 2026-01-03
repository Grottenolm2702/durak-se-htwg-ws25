package de.htwg.DurakApp.model

import de.htwg.DurakApp.testutil.TestHelpers._
import de.htwg.DurakApp.testutil.TestGamePhases

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.state.GameEvent

class GameStateSpec extends AnyWordSpec with Matchers {

  "A Game" should {
    "store its players, deck, table and trump correctly" in {
      val players = List(
        Player("Lucifer", List.empty, isDone = false),
        Player("Michael", List.empty, isDone = false)
      )
      val deck = List(
        Card(Suit.Spades, Rank.Ace, isTrump = false),
        Card(Suit.Diamonds, Rank.Ten, isTrump = true)
      )
      val trumpSuit = Suit.Hearts
      val trumpCard = Card(trumpSuit, Rank.Six, isTrump = true)

      val gameState = GameState(
        players = players,
        deck = deck,
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
      gameState.players.shouldBe(players)
      gameState.deck.shouldBe(deck)
      gameState.trumpCard.shouldBe(trumpCard)
    }

    "support copy operations" in {
      val players = List(Player("P1"), Player("P2"))
      val trumpCard = Card(Suit.Hearts, Rank.Ace)
      val gameState = GameState(
        players = players,
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

      val newDeck = List(Card(Suit.Clubs, Rank.Six))
      val copied = gameState.copy(deck = newDeck)

      copied.deck.shouldBe(newDeck)
      copied.players.shouldBe(players)
    }

    "use default parameters when not explicitly provided" in {
      val players = List(Player("P1"), Player("P2"))
      val trumpCard = Card(Suit.Hearts, Rank.Ace)

      val gameState = GameState(
        players = players,
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

      gameState.lastEvent shouldBe None
      gameState.passedPlayers shouldBe Set.empty
      gameState.roundWinner shouldBe None
      gameState.setupPlayerCount shouldBe None
      gameState.setupPlayerNames shouldBe List.empty
      gameState.setupDeckSize shouldBe None
      gameState.currentAttackerIndex shouldBe None
      gameState.lastAttackerIndex shouldBe None
    }

    "use default parameter for lastEvent" in {
      val players = List(Player("P1"), Player("P2"))
      val gameState = GameState(
        players = players,
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = Card(Suit.Hearts, Rank.Ace),
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
      gameState.lastEvent shouldBe None
    }

    "use default parameter for passedPlayers" in {
      val players = List(Player("P1"), Player("P2"))
      val gameState = GameState(
        players = players,
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = Card(Suit.Hearts, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = TestGamePhases.setupPhase,
        lastEvent = Some(GameEvent.Pass),
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      gameState.passedPlayers shouldBe Set.empty
    }

    "use default parameter for roundWinner" in {
      val players = List(Player("P1"), Player("P2"))
      val gameState = GameState(
        players = players,
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = Card(Suit.Hearts, Rank.Ace),
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
      gameState.roundWinner shouldBe None
    }

    "use default parameter for setupPlayerCount" in {
      val players = List(Player("P1"), Player("P2"))
      val gameState = GameState(
        players = players,
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = Card(Suit.Hearts, Rank.Ace),
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
      gameState.setupPlayerCount shouldBe None
    }

    "use default parameter for setupPlayerNames" in {
      val players = List(Player("P1"), Player("P2"))
      val gameState = GameState(
        players = players,
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = Card(Suit.Hearts, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = TestGamePhases.setupPhase,
        passedPlayers = Set.empty,
        roundWinner = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None,
        lastEvent = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None
      )
      gameState.setupPlayerNames shouldBe List.empty
    }

    "use default parameter for setupDeckSize" in {
      val players = List(Player("P1"), Player("P2"))
      val gameState = GameState(
        players = players,
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = Card(Suit.Hearts, Rank.Ace),
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
      gameState.setupDeckSize shouldBe None
    }

    "use default parameter for currentAttackerIndex" in {
      val players = List(Player("P1"), Player("P2"))
      val gameState = GameState(
        players = players,
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = Card(Suit.Hearts, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = TestGamePhases.setupPhase,
        passedPlayers = Set.empty,
        roundWinner = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None,
        lastEvent = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None
      )
      gameState.currentAttackerIndex shouldBe None
    }

    "use default parameter for lastAttackerIndex" in {
      val players = List(Player("P1"), Player("P2"))
      val gameState = GameState(
        players = players,
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = Card(Suit.Hearts, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = TestGamePhases.setupPhase,
        passedPlayers = Set.empty,
        roundWinner = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None,
        lastEvent = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None
      )
      gameState.lastAttackerIndex shouldBe None
    }

  }
}
