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
      val rebuilt = builder.build().get
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
    "support copy method" in {
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace)
      val card2 = TestHelper.Card(Suit.Diamonds, Rank.King)
      val player = TestHelper.Player("Alice", List(card))
      val player2 = TestHelper.Player("Bob", List(card2))
      val gameState = TestHelper.GameState(
        players = List(player),
        deck = List(card),
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
      val copied = gameState.copy(
        players = List(player2),
        attackerIndex = 1
      )
      copied.players shouldBe List(player2)
      copied.attackerIndex shouldBe 1
      copied.deck shouldBe gameState.deck
      copied.trumpCard shouldBe gameState.trumpCard
    }
    "handle equals with different deck" in {
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace)
      val card2 = TestHelper.Card(Suit.Diamonds, Rank.King)
      val player = TestHelper.Player("Alice", List(card))
      val gameState1 = TestHelper.GameState(
        players = List(player),
        deck = List(card),
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
      val gameState2 = gameState1.copy(deck = List(card2))
      gameState1 should not be gameState2
    }
    "handle equals with different table" in {
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace)
      val card2 = TestHelper.Card(Suit.Diamonds, Rank.King)
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
      val gameState2 = gameState1.copy(table = Map(card -> Some(card2)))
      gameState1 should not be gameState2
    }
    "handle equals with different discardPile" in {
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
      val gameState2 = gameState1.copy(discardPile = List(card))
      gameState1 should not be gameState2
    }
    "handle equals with different trumpCard" in {
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace)
      val card2 = TestHelper.Card(Suit.Diamonds, Rank.King)
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
      val gameState2 = gameState1.copy(trumpCard = card2)
      gameState1 should not be gameState2
    }
    "handle equals with different attackerIndex" in {
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
      val gameState2 = gameState1.copy(attackerIndex = 1)
      gameState1 should not be gameState2
    }
    "handle equals with different defenderIndex" in {
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
      val gameState2 = gameState1.copy(defenderIndex = 1)
      gameState1 should not be gameState2
    }
    "handle equals with different gamePhase" in {
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
      val gameState2 = gameState1.copy(gamePhase = StubGamePhases.attackPhase)
      gameState1 should not be gameState2
    }
    "handle equals with different lastEvent" in {
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
      val gameState2 = gameState1.copy(lastEvent = Some(GameEvent.Attack(card)))
      gameState1 should not be gameState2
    }
    "handle equals with different passedPlayers" in {
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
      val gameState2 = gameState1.copy(passedPlayers = Set(1))
      gameState1 should not be gameState2
    }
    "handle equals with different roundWinner" in {
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
      val gameState2 = gameState1.copy(roundWinner = Some(0))
      gameState1 should not be gameState2
    }
    "handle equals with different setupPlayerCount" in {
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
      val gameState2 = gameState1.copy(setupPlayerCount = Some(2))
      gameState1 should not be gameState2
    }
    "handle equals with different setupPlayerNames" in {
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
      val gameState2 = gameState1.copy(setupPlayerNames = List("Alice"))
      gameState1 should not be gameState2
    }
    "handle equals with different setupDeckSize" in {
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
      val gameState2 = gameState1.copy(setupDeckSize = Some(20))
      gameState1 should not be gameState2
    }
    "handle equals with different currentAttackerIndex" in {
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
      val gameState2 = gameState1.copy(currentAttackerIndex = Some(1))
      gameState1 should not be gameState2
    }
    "handle equals with different lastAttackerIndex" in {
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
      val gameState2 = gameState1.copy(lastAttackerIndex = Some(1))
      gameState1 should not be gameState2
    }
    "test toBuilder with direct GameStateImpl instantiation" in {
      val cardFactory = new StubCardFactory()
      val playerFactory = new StubPlayerFactory()
      val gamePhases = new StubGamePhasesImpl()
      val gameStateBuilder = de.htwg.DurakApp.model.builder.impl
        .GameStateBuilder(cardFactory, gamePhases)
      val card = cardFactory(Suit.Hearts, Rank.Ace, isTrump = false)
      val player = playerFactory("Alice", List(card))
      val gameState = new GameStateImpl(
        players = List(player),
        deck = List(card),
        table = Map(card -> None),
        discardPile = List(),
        trumpCard = card,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = gamePhases.attackPhase,
        lastEvent = Some(GameEvent.Attack(card)),
        passedPlayers = Set(1),
        roundWinner = Some(0),
        setupPlayerCount = Some(2),
        setupPlayerNames = List("Alice", "Bob"),
        setupDeckSize = Some(20),
        currentAttackerIndex = Some(0),
        lastAttackerIndex = Some(1),
        gamePhases = gamePhases,
        cardFactory = cardFactory,
        playerFactory = playerFactory,
        gameStateBuilder = gameStateBuilder
      )
      val builder = gameState.toBuilder
      val rebuilt = builder.build().get
      rebuilt.players shouldBe gameState.players
      rebuilt.deck shouldBe gameState.deck
      rebuilt.table shouldBe gameState.table
      rebuilt.discardPile shouldBe gameState.discardPile
      rebuilt.trumpCard shouldBe gameState.trumpCard
      rebuilt.attackerIndex shouldBe gameState.attackerIndex
      rebuilt.defenderIndex shouldBe gameState.defenderIndex
      rebuilt.lastEvent shouldBe gameState.lastEvent
      rebuilt.passedPlayers shouldBe gameState.passedPlayers
      rebuilt.roundWinner shouldBe gameState.roundWinner
      rebuilt.setupPlayerCount shouldBe gameState.setupPlayerCount
      rebuilt.setupPlayerNames shouldBe gameState.setupPlayerNames
      rebuilt.setupDeckSize shouldBe gameState.setupDeckSize
      rebuilt.currentAttackerIndex shouldBe gameState.currentAttackerIndex
      rebuilt.lastAttackerIndex shouldBe gameState.lastAttackerIndex
    }
    "test toBuilder can modify properties through builder" in {
      val cardFactory = new StubCardFactory()
      val playerFactory = new StubPlayerFactory()
      val gamePhases = new StubGamePhasesImpl()
      val gameStateBuilder = de.htwg.DurakApp.model.builder.impl
        .GameStateBuilder(cardFactory, gamePhases)
      val card = cardFactory(Suit.Hearts, Rank.Ace, isTrump = false)
      val card2 = cardFactory(Suit.Diamonds, Rank.King, isTrump = false)
      val player = playerFactory("Alice", List(card))
      val player2 = playerFactory("Bob", List(card2))
      val gameState = new GameStateImpl(
        players = List(player),
        deck = List(),
        table = Map(),
        discardPile = List(),
        trumpCard = card,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = gamePhases.setupPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None,
        gamePhases = gamePhases,
        cardFactory = cardFactory,
        playerFactory = playerFactory,
        gameStateBuilder = gameStateBuilder
      )
      val modified = gameState.toBuilder
        .withPlayers(List(player, player2))
        .withDeck(List(card2))
        .withAttackerIndex(1)
        .build()
        .get
      modified.players should have size 2
      modified.deck shouldBe List(card2)
      modified.attackerIndex shouldBe 1
    }
    "test copy creates new GameStateImpl instance" in {
      val cardFactory = new StubCardFactory()
      val playerFactory = new StubPlayerFactory()
      val gamePhases = new StubGamePhasesImpl()
      val gameStateBuilder = de.htwg.DurakApp.model.builder.impl
        .GameStateBuilder(cardFactory, gamePhases)
      val card = cardFactory(Suit.Hearts, Rank.Ace, isTrump = false)
      val player = playerFactory("Alice", List(card))
      val gameState = new GameStateImpl(
        players = List(player),
        deck = List(),
        table = Map(),
        discardPile = List(),
        trumpCard = card,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = gamePhases.setupPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None,
        gamePhases = gamePhases,
        cardFactory = cardFactory,
        playerFactory = playerFactory,
        gameStateBuilder = gameStateBuilder
      )
      val copied = gameState.copy(
        attackerIndex = 1,
        defenderIndex = 0,
        deck = List(card)
      )
      copied.attackerIndex shouldBe 1
      copied.defenderIndex shouldBe 0
      copied.deck shouldBe List(card)
      copied.players shouldBe gameState.players
      copied shouldBe a[GameStateImpl]
    }
    "test direct GameStateImpl equals" in {
      val cardFactory = new StubCardFactory()
      val playerFactory = new StubPlayerFactory()
      val gamePhases = new StubGamePhasesImpl()
      val gameStateBuilder = de.htwg.DurakApp.model.builder.impl
        .GameStateBuilder(cardFactory, gamePhases)
      val card = cardFactory(Suit.Hearts, Rank.Ace, isTrump = false)
      val player = playerFactory("Alice", List(card))
      val gameState1 = new GameStateImpl(
        players = List(player),
        deck = List(),
        table = Map(),
        discardPile = List(),
        trumpCard = card,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = gamePhases.setupPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None,
        gamePhases = gamePhases,
        cardFactory = cardFactory,
        playerFactory = playerFactory,
        gameStateBuilder = gameStateBuilder
      )
      val gameState2 = new GameStateImpl(
        players = List(player),
        deck = List(),
        table = Map(),
        discardPile = List(),
        trumpCard = card,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = gamePhases.setupPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None,
        gamePhases = gamePhases,
        cardFactory = cardFactory,
        playerFactory = playerFactory,
        gameStateBuilder = gameStateBuilder
      )
      gameState1 shouldBe gameState2
      gameState1.hashCode() shouldBe gameState2.hashCode()
    }
    "test GameStateImpl not equal to non-GameStateImpl" in {
      val cardFactory = new StubCardFactory()
      val playerFactory = new StubPlayerFactory()
      val gamePhases = new StubGamePhasesImpl()
      val gameStateBuilder = de.htwg.DurakApp.model.builder.impl
        .GameStateBuilder(cardFactory, gamePhases)
      val card = cardFactory(Suit.Hearts, Rank.Ace, isTrump = false)
      val player = playerFactory("Alice", List(card))
      val gameState = new GameStateImpl(
        players = List(player),
        deck = List(),
        table = Map(),
        discardPile = List(),
        trumpCard = card,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = gamePhases.setupPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None,
        gamePhases = gamePhases,
        cardFactory = cardFactory,
        playerFactory = playerFactory,
        gameStateBuilder = gameStateBuilder
      )
      gameState should not be "not a GameStateImpl"
      gameState should not be 42
    }
  }
}
