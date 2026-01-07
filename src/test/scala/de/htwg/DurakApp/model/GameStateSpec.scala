package de.htwg.DurakApp.model
import de.htwg.DurakApp.testutil._
import de.htwg.DurakApp.testutil.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.state.GameEvent
class GameStateSpec extends AnyWordSpec with Matchers {
  "A Game" should {
    "store its players, deck, table and trump correctly" in {
      val players = List(
        TestHelper.Player("Lucifer", List.empty, isDone = false),
        TestHelper.Player("Michael", List.empty, isDone = false)
      )
      val deck = List(
        TestHelper.Card(Suit.Spades, Rank.Ace, isTrump = false),
        TestHelper.Card(Suit.Diamonds, Rank.Ten, isTrump = true)
      )
      val trumpSuit = Suit.Hearts
      val trumpCard = TestHelper.Card(trumpSuit, Rank.Six, isTrump = true)
      val gameState = TestHelper.GameState(
        players = players,
        deck = deck,
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
      gameState.players.shouldBe(players)
      gameState.deck.shouldBe(deck)
      gameState.trumpCard.shouldBe(trumpCard)
    }
    "support copy operations" in {
      val players = List(TestHelper.Player("P1"), TestHelper.Player("P2"))
      val trumpCard = TestHelper.Card(Suit.Hearts, Rank.Ace)
      val gameState = TestHelper.GameState(
        players = players,
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
      val newDeck = List(TestHelper.Card(Suit.Clubs, Rank.Six))
      val copied = gameState.copy(deck = newDeck)
      copied.deck.shouldBe(newDeck)
      copied.players.shouldBe(players)
    }
    "use default parameters when not explicitly provided" in {
      val players = List(TestHelper.Player("P1"), TestHelper.Player("P2"))
      val trumpCard = TestHelper.Card(Suit.Hearts, Rank.Ace)
      val gameState = TestHelper.GameState(
        players = players,
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
      val players = List(TestHelper.Player("P1"), TestHelper.Player("P2"))
      val gameState = TestHelper.GameState(
        players = players,
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = TestHelper.Card(Suit.Hearts, Rank.Ace),
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
      gameState.lastEvent shouldBe None
    }
    "use default parameter for passedPlayers" in {
      val players = List(TestHelper.Player("P1"), TestHelper.Player("P2"))
      val gameState = TestHelper.GameState(
        players = players,
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = TestHelper.Card(Suit.Hearts, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = StubGamePhases.setupPhase,
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
      val players = List(TestHelper.Player("P1"), TestHelper.Player("P2"))
      val gameState = TestHelper.GameState(
        players = players,
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = TestHelper.Card(Suit.Hearts, Rank.Ace),
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
      gameState.roundWinner shouldBe None
    }
    "use default parameter for setupPlayerCount" in {
      val players = List(TestHelper.Player("P1"), TestHelper.Player("P2"))
      val gameState = TestHelper.GameState(
        players = players,
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = TestHelper.Card(Suit.Hearts, Rank.Ace),
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
      gameState.setupPlayerCount shouldBe None
    }
    "use default parameter for setupPlayerNames" in {
      val players = List(TestHelper.Player("P1"), TestHelper.Player("P2"))
      val gameState = TestHelper.GameState(
        players = players,
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = TestHelper.Card(Suit.Hearts, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = StubGamePhases.setupPhase,
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
      val players = List(TestHelper.Player("P1"), TestHelper.Player("P2"))
      val gameState = TestHelper.GameState(
        players = players,
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = TestHelper.Card(Suit.Hearts, Rank.Ace),
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
      gameState.setupDeckSize shouldBe None
    }
    "use default parameter for currentAttackerIndex" in {
      val players = List(TestHelper.Player("P1"), TestHelper.Player("P2"))
      val gameState = TestHelper.GameState(
        players = players,
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = TestHelper.Card(Suit.Hearts, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = StubGamePhases.setupPhase,
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
      val players = List(TestHelper.Player("P1"), TestHelper.Player("P2"))
      val gameState = TestHelper.GameState(
        players = players,
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = TestHelper.Card(Suit.Hearts, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = StubGamePhases.setupPhase,
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
  "GameState copy method" should {
    val player1 = TestHelper.Player("P1")
    val player2 = TestHelper.Player("P2")
    val card1 = TestHelper.Card(Suit.Hearts, Rank.Ace)
    val card2 = TestHelper.Card(Suit.Diamonds, Rank.King)
    val trumpCard = TestHelper.Card(Suit.Clubs, Rank.Six, isTrump = true)
    val originalState = TestHelper.createTestGameState(
      players = List(player1, player2),
      deck = List(card1),
      table = Map(card2 -> None),
      discardPile = List.empty,
      trumpCard = trumpCard,
      attackerIndex = 0,
      defenderIndex = 1,
      gamePhase = StubGamePhases.attackPhase,
      lastEvent = Some(GameEvent.Pass),
      passedPlayers = Set(0),
      roundWinner = Some(1),
      setupPlayerCount = Some(2),
      setupPlayerNames = List("P1", "P2"),
      setupDeckSize = Some(36),
      currentAttackerIndex = Some(0),
      lastAttackerIndex = Some(1)
    )
    "copy with only players changed" in {
      val newPlayers = List(TestHelper.Player("P3"))
      val copied = originalState.copy(players = newPlayers)
      copied.players shouldBe newPlayers
      copied.deck shouldBe originalState.deck
      copied.table shouldBe originalState.table
    }
    "copy with only deck changed" in {
      val newDeck = List(card2)
      val copied = originalState.copy(deck = newDeck)
      copied.deck shouldBe newDeck
      copied.players shouldBe originalState.players
    }
    "copy with only table changed" in {
      val newTable = Map(card1 -> Some(card2))
      val copied = originalState.copy(table = newTable)
      copied.table shouldBe newTable
      copied.players shouldBe originalState.players
    }
    "copy with only discardPile changed" in {
      val newDiscard = List(card1, card2)
      val copied = originalState.copy(discardPile = newDiscard)
      copied.discardPile shouldBe newDiscard
      copied.players shouldBe originalState.players
    }
    "copy with only trumpCard changed" in {
      val newTrump = TestHelper.Card(Suit.Spades, Rank.Seven, isTrump = true)
      val copied = originalState.copy(trumpCard = newTrump)
      copied.trumpCard shouldBe newTrump
      copied.players shouldBe originalState.players
    }
    "copy with only attackerIndex changed" in {
      val copied = originalState.copy(attackerIndex = 1)
      copied.attackerIndex shouldBe 1
      copied.defenderIndex shouldBe originalState.defenderIndex
    }
    "copy with only defenderIndex changed" in {
      val copied = originalState.copy(defenderIndex = 0)
      copied.defenderIndex shouldBe 0
      copied.attackerIndex shouldBe originalState.attackerIndex
    }
    "copy with only gamePhase changed" in {
      val copied = originalState.copy(gamePhase = StubGamePhases.defensePhase)
      copied.gamePhase shouldBe StubGamePhases.defensePhase
      copied.players shouldBe originalState.players
    }
    "copy with only lastEvent changed" in {
      val copied = originalState.copy(lastEvent = Some(GameEvent.InvalidMove))
      copied.lastEvent shouldBe Some(GameEvent.InvalidMove)
      copied.players shouldBe originalState.players
    }
    "copy with only passedPlayers changed" in {
      val copied = originalState.copy(passedPlayers = Set(0, 1))
      copied.passedPlayers shouldBe Set(0, 1)
      copied.players shouldBe originalState.players
    }
    "copy with only roundWinner changed" in {
      val copied = originalState.copy(roundWinner = Some(0))
      copied.roundWinner shouldBe Some(0)
      copied.players shouldBe originalState.players
    }
    "copy with only setupPlayerCount changed" in {
      val copied = originalState.copy(setupPlayerCount = Some(3))
      copied.setupPlayerCount shouldBe Some(3)
      copied.players shouldBe originalState.players
    }
    "copy with only setupPlayerNames changed" in {
      val copied = originalState.copy(setupPlayerNames = List("A", "B", "C"))
      copied.setupPlayerNames shouldBe List("A", "B", "C")
      copied.players shouldBe originalState.players
    }
    "copy with only setupDeckSize changed" in {
      val copied = originalState.copy(setupDeckSize = Some(20))
      copied.setupDeckSize shouldBe Some(20)
      copied.players shouldBe originalState.players
    }
    "copy with only currentAttackerIndex changed" in {
      val copied = originalState.copy(currentAttackerIndex = Some(1))
      copied.currentAttackerIndex shouldBe Some(1)
      copied.players shouldBe originalState.players
    }
    "copy with only lastAttackerIndex changed" in {
      val copied = originalState.copy(lastAttackerIndex = Some(0))
      copied.lastAttackerIndex shouldBe Some(0)
      copied.players shouldBe originalState.players
    }
    "copy with multiple parameters changed" in {
      val newPlayers = List(TestHelper.Player("P3"), TestHelper.Player("P4"))
      val newDeck = List(card2)
      val copied = originalState.copy(
        players = newPlayers,
        deck = newDeck,
        attackerIndex = 1,
        defenderIndex = 0
      )
      copied.players shouldBe newPlayers
      copied.deck shouldBe newDeck
      copied.attackerIndex shouldBe 1
      copied.defenderIndex shouldBe 0
      copied.table shouldBe originalState.table
    }
    "copy with all parameters changed" in {
      val newPlayers = List(TestHelper.Player("New"))
      val newDeck = List(card2)
      val newTable = Map.empty[Card, Option[Card]]
      val newDiscard = List(card1)
      val newTrump = TestHelper.Card(Suit.Spades, Rank.Ten, isTrump = true)
      val copied = originalState.copy(
        players = newPlayers,
        deck = newDeck,
        table = newTable,
        discardPile = newDiscard,
        trumpCard = newTrump,
        attackerIndex = 1,
        defenderIndex = 0,
        gamePhase = StubGamePhases.drawPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      copied.players shouldBe newPlayers
      copied.deck shouldBe newDeck
      copied.table shouldBe newTable
      copied.discardPile shouldBe newDiscard
      copied.trumpCard shouldBe newTrump
      copied.attackerIndex shouldBe 1
      copied.defenderIndex shouldBe 0
      copied.gamePhase shouldBe StubGamePhases.drawPhase
      copied.lastEvent shouldBe None
      copied.passedPlayers shouldBe Set.empty
      copied.roundWinner shouldBe None
      copied.setupPlayerCount shouldBe None
      copied.setupPlayerNames shouldBe List.empty
      copied.setupDeckSize shouldBe None
      copied.currentAttackerIndex shouldBe None
      copied.lastAttackerIndex shouldBe None
    }
    "copy with no parameters (returns same values)" in {
      val copied = originalState.copy()
      copied.players shouldBe originalState.players
      copied.deck shouldBe originalState.deck
      copied.table shouldBe originalState.table
      copied.discardPile shouldBe originalState.discardPile
      copied.trumpCard shouldBe originalState.trumpCard
      copied.attackerIndex shouldBe originalState.attackerIndex
      copied.defenderIndex shouldBe originalState.defenderIndex
      copied.gamePhase shouldBe originalState.gamePhase
      copied.lastEvent shouldBe originalState.lastEvent
      copied.passedPlayers shouldBe originalState.passedPlayers
      copied.roundWinner shouldBe originalState.roundWinner
      copied.setupPlayerCount shouldBe originalState.setupPlayerCount
      copied.setupPlayerNames shouldBe originalState.setupPlayerNames
      copied.setupDeckSize shouldBe originalState.setupDeckSize
      copied.currentAttackerIndex shouldBe originalState.currentAttackerIndex
      copied.lastAttackerIndex shouldBe originalState.lastAttackerIndex
    }
  }
}
