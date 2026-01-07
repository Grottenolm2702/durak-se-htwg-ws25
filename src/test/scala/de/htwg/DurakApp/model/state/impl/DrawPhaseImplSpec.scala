package de.htwg.DurakApp.model.state.impl
import de.htwg.DurakApp.testutil._
import de.htwg.DurakApp.testutil.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Suit, Rank, GameState, Player}
import de.htwg.DurakApp.model.state.GameEvent
class DrawPhaseImplSpec extends AnyWordSpec with Matchers {
  "DrawPhaseImpl" should {
    "have correct string representation" in {
      DrawPhaseImpl.toString shouldBe "DrawPhase"
    }
    "draw cards for attacker first" in {
      val player1 =
        TestHelper.Player("Alice", List(TestHelper.Card(Suit.Hearts, Rank.Six)))
      val player2 = TestHelper.Player(
        "Bob",
        List(TestHelper.Card(Suit.Diamonds, Rank.Seven))
      )
      val trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val deck = List.fill(10)(TestHelper.Card(Suit.Spades, Rank.Eight))
      val gameState = TestHelper.GameState(
        players = List(player1, player2),
        deck = deck,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = DrawPhaseImpl,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = Some(1),
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      val result = DrawPhaseImpl.handle(gameState)
      result.players(0).hand.size shouldBe 6
      result.players(1).hand.size shouldBe 6
      result.lastEvent shouldBe Some(GameEvent.Draw)
      result.gamePhase shouldBe RoundPhaseImpl
    }
    "set correct next attacker and defender when round won" in {
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
        gamePhase = DrawPhaseImpl,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = Some(1),
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      val result = DrawPhaseImpl.handle(gameState)
      result.attackerIndex shouldBe 1
      result.defenderIndex shouldBe 0
    }
    "set correct next attacker and defender when round lost" in {
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
        gamePhase = DrawPhaseImpl,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      val result = DrawPhaseImpl.handle(gameState)
      result.attackerIndex shouldBe 0
      result.defenderIndex shouldBe 1
    }
    "not draw more cards than needed to reach 6" in {
      val player1 = TestHelper.Player(
        "Alice",
        List.fill(4)(TestHelper.Card(Suit.Hearts, Rank.Six))
      )
      val player2 = TestHelper.Player(
        "Bob",
        List.fill(5)(TestHelper.Card(Suit.Diamonds, Rank.Seven))
      )
      val trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val deck = List.fill(10)(TestHelper.Card(Suit.Spades, Rank.Eight))
      val gameState = TestHelper.GameState(
        players = List(player1, player2),
        deck = deck,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = DrawPhaseImpl,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = Some(1),
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      val result = DrawPhaseImpl.handle(gameState)
      result.players(0).hand.size shouldBe 6
      result.players(1).hand.size shouldBe 6
      result.deck.size shouldBe 7
    }
    "handle empty deck" in {
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
        gamePhase = DrawPhaseImpl,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = Some(1),
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      val result = DrawPhaseImpl.handle(gameState)
      result.players(0).hand.size shouldBe 1
      result.players(1).hand.size shouldBe 1
    }
    "draw for other attackers before defender" in {
      val player1 =
        TestHelper.Player("Alice", List(TestHelper.Card(Suit.Hearts, Rank.Six)))
      val player2 = TestHelper.Player(
        "Bob",
        List(TestHelper.Card(Suit.Diamonds, Rank.Seven))
      )
      val player3 = TestHelper.Player(
        "Charlie",
        List(TestHelper.Card(Suit.Spades, Rank.Nine))
      )
      val trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val deck = List.fill(15)(TestHelper.Card(Suit.Hearts, Rank.Ten))
      val gameState = TestHelper.GameState(
        players = List(player1, player2, player3),
        deck = deck,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = DrawPhaseImpl,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = Some(1),
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      val result = DrawPhaseImpl.handle(gameState)
      result.players(0).hand.size shouldBe 6
      result.players(1).hand.size shouldBe 6
      result.players(2).hand.size shouldBe 6
    }
    "not draw cards when player already has 6 or more cards" in {
      val player1 = TestHelper.Player(
        "Alice",
        List.fill(6)(TestHelper.Card(Suit.Hearts, Rank.Six))
      )
      val player2 = TestHelper.Player(
        "Bob",
        List.fill(7)(TestHelper.Card(Suit.Diamonds, Rank.Seven))
      )
      val trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val deck = List.fill(10)(TestHelper.Card(Suit.Spades, Rank.Eight))
      val gameState = TestHelper.GameState(
        players = List(player1, player2),
        deck = deck,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = DrawPhaseImpl,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = Some(1),
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      val result = DrawPhaseImpl.handle(gameState)
      result.players(0).hand.size shouldBe 6
      result.players(1).hand.size shouldBe 7
      result.deck.size shouldBe 10
    }
    "handle mixed scenario with some players needing cards and others not" in {
      val player1 = TestHelper.Player(
        "Alice",
        List.fill(2)(TestHelper.Card(Suit.Hearts, Rank.Six))
      )
      val player2 = TestHelper.Player(
        "Bob",
        List.fill(6)(TestHelper.Card(Suit.Diamonds, Rank.Seven))
      )
      val player3 = TestHelper.Player(
        "Charlie",
        List.fill(3)(TestHelper.Card(Suit.Spades, Rank.Nine))
      )
      val trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val deck = List.fill(10)(TestHelper.Card(Suit.Hearts, Rank.Ten))
      val gameState = TestHelper.GameState(
        players = List(player1, player2, player3),
        deck = deck,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = DrawPhaseImpl,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      val result = DrawPhaseImpl.handle(gameState)
      result.players(0).hand.size shouldBe 6
      result.players(1).hand.size shouldBe 6
      result.players(2).hand.size shouldBe 6
      result.deck.size shouldBe 3
    }
  }
}
