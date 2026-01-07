package de.htwg.DurakApp.model.impl

import de.htwg.DurakApp.testutil._

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{GameState, Player, Card, Suit, Rank}
import de.htwg.DurakApp.model.state.GameEvent
import de.htwg.DurakApp.testutil.*

class GameStateImplSpec extends AnyWordSpec with Matchers {
  "GameStateImpl" should {
    "be created through GameState factory" in {
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace)
      val player = TestHelper.Player("Alice", List(card))
      val gameState = TestHelper.GameState(
        players = List(player),
        deck = List(),
        table = Map(),
        discardPile = List(),
        trumpCard = card,
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

      gameState.players should have size 1
      gameState.deck shouldBe empty
    }

    "support toBuilder conversion" in {
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace)
      val player = TestHelper.Player("Alice", List(card))
      val gameState = TestHelper.GameState(
        players = List(player),
        deck = List(),
        table = Map(),
        discardPile = List(),
        trumpCard = card,
        attackerIndex = 0,
        defenderIndex = 0,
        gamePhase = StubGamePhases.attackPhase,
        lastEvent = Some(GameEvent.Attack(card)),
        passedPlayers = Set(1),
        roundWinner = Some(0),
        setupPlayerCount = Some(2),
        setupPlayerNames = List("Alice", "Bob"),
        setupDeckSize = Some(20),
        currentAttackerIndex = Some(0),
        lastAttackerIndex = Some(1)
      )

      val builder = gameState.toBuilder
      val rebuilt = builder.build()

      rebuilt.players shouldBe gameState.players
      rebuilt.deck shouldBe gameState.deck
      rebuilt.table shouldBe gameState.table
      rebuilt.discardPile shouldBe gameState.discardPile
      rebuilt.trumpCard shouldBe gameState.trumpCard
      rebuilt.attackerIndex shouldBe gameState.attackerIndex
      rebuilt.defenderIndex shouldBe gameState.defenderIndex
      rebuilt.gamePhase shouldBe gameState.gamePhase
      rebuilt.lastEvent shouldBe gameState.lastEvent
      rebuilt.passedPlayers shouldBe gameState.passedPlayers
      rebuilt.roundWinner shouldBe gameState.roundWinner
      rebuilt.setupPlayerCount shouldBe gameState.setupPlayerCount
      rebuilt.setupPlayerNames shouldBe gameState.setupPlayerNames
      rebuilt.setupDeckSize shouldBe gameState.setupDeckSize
      rebuilt.currentAttackerIndex shouldBe gameState.currentAttackerIndex
      rebuilt.lastAttackerIndex shouldBe gameState.lastAttackerIndex
    }

    "implement equals correctly" in {
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace)
      val player = TestHelper.Player("Alice", List(card))
      
      val gameState1 = TestHelper.GameState(
        players = List(player),
        deck = List(),
        table = Map(),
        discardPile = List(),
        trumpCard = card,
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

      val gameState2 = TestHelper.GameState(
        players = List(player),
        deck = List(),
        table = Map(),
        discardPile = List(),
        trumpCard = card,
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

      gameState1 shouldBe gameState2
    }

    "implement equals correctly for different states" in {
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace)
      val player1 = TestHelper.Player("Alice", List(card))
      val player2 = TestHelper.Player("Bob", List(card))
      
      val gameState1 = TestHelper.GameState(
        players = List(player1),
        deck = List(),
        table = Map(),
        discardPile = List(),
        trumpCard = card,
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

      val gameState2 = TestHelper.GameState(
        players = List(player2),
        deck = List(),
        table = Map(),
        discardPile = List(),
        trumpCard = card,
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

      gameState1 should not be gameState2
    }

    "implement equals correctly for non-GameState objects" in {
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace)
      val player = TestHelper.Player("Alice", List(card))
      
      val gameState = TestHelper.GameState(
        players = List(player),
        deck = List(),
        table = Map(),
        discardPile = List(),
        trumpCard = card,
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

      gameState should not be "not a game state"
    }

    "implement hashCode correctly" in {
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace)
      val player = TestHelper.Player("Alice", List(card))
      
      val gameState1 = TestHelper.GameState(
        players = List(player),
        deck = List(),
        table = Map(),
        discardPile = List(),
        trumpCard = card,
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

      val gameState2 = TestHelper.GameState(
        players = List(player),
        deck = List(),
        table = Map(),
        discardPile = List(),
        trumpCard = card,
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

      gameState1.hashCode() shouldBe gameState2.hashCode()
    }
  }
}
