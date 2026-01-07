package de.htwg.DurakApp.model.state.impl

import de.htwg.DurakApp.testutil._
import de.htwg.DurakApp.testutil.*

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Suit, Rank, GameState, Player}
import de.htwg.DurakApp.model.state.{GameEvent}

class DefensePhaseImplSpec extends AnyWordSpec with Matchers {

  "DefensePhaseImpl" should {
    "have correct string representation" in {
      DefensePhaseImpl.toString shouldBe "DefensePhase"
    }

    "handle returns same state" in {
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
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )

      val result = DefensePhaseImpl.handle(gameState)
      result shouldBe gameState
    }

    "defend successfully with higher same suit card" in {
      val attackCard = TestHelper.Card(Suit.Hearts, Rank.Six)
      val defenseCard = TestHelper.Card(Suit.Hearts, Rank.Seven)
      val player1 = TestHelper.Player("Alice", List.empty)
      val player2 = TestHelper.Player("Bob", List(defenseCard))
      val trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val table = Map(attackCard -> None)

      val gameState = TestHelper.GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = table,
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
        lastAttackerIndex = Some(0)
      )

      val result = DefensePhaseImpl.playCard(defenseCard, 1, gameState)
      result.table.get(attackCard) shouldBe Some(Some(defenseCard))
      result.lastEvent shouldBe Some(GameEvent.Defend(defenseCard))
      result.gamePhase shouldBe AttackPhaseImpl
    }

    "defend successfully with trump against non-trump" in {
      val attackCard = TestHelper.Card(Suit.Hearts, Rank.Ace)
      val defenseCard = TestHelper.Card(Suit.Clubs, Rank.Six, isTrump = true)
      val player1 = TestHelper.Player("Alice", List.empty)
      val player2 = TestHelper.Player("Bob", List(defenseCard))
      val trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val table = Map(attackCard -> None)

      val gameState = TestHelper.GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = table,
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
        lastAttackerIndex = Some(0)
      )

      val result = DefensePhaseImpl.playCard(defenseCard, 1, gameState)
      result.table.get(attackCard) shouldBe Some(Some(defenseCard))
      result.lastEvent shouldBe Some(GameEvent.Defend(defenseCard))
    }

    "return NotYourTurn when attacker tries to play" in {
      val attackCard = TestHelper.Card(Suit.Hearts, Rank.Six)
      val defenseCard = TestHelper.Card(Suit.Hearts, Rank.Seven)
      val player1 = TestHelper.Player("Alice", List(defenseCard))
      val player2 = TestHelper.Player(
        "Bob",
        List(TestHelper.Card(Suit.Diamonds, Rank.Seven))
      )
      val trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val table = Map(attackCard -> None)

      val gameState = TestHelper.GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = table,
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

      val result = DefensePhaseImpl.playCard(defenseCard, 0, gameState)
      result.lastEvent shouldBe Some(GameEvent.NotYourTurn)
    }

    "return InvalidMove when card not in hand" in {
      val attackCard = TestHelper.Card(Suit.Hearts, Rank.Six)
      val defenseCard = TestHelper.Card(Suit.Hearts, Rank.Seven)
      val player1 = TestHelper.Player("Alice", List.empty)
      val player2 = TestHelper.Player(
        "Bob",
        List(TestHelper.Card(Suit.Diamonds, Rank.Seven))
      )
      val trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val table = Map(attackCard -> None)

      val gameState = TestHelper.GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = table,
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

      val result = DefensePhaseImpl.playCard(defenseCard, 1, gameState)
      result.lastEvent shouldBe Some(GameEvent.InvalidMove)
    }

    "return InvalidMove when all attacks defended" in {
      val attackCard = TestHelper.Card(Suit.Hearts, Rank.Six)
      val defenseCard = TestHelper.Card(Suit.Hearts, Rank.Seven)
      val anotherCard = TestHelper.Card(Suit.Hearts, Rank.Eight)
      val player1 = TestHelper.Player("Alice", List.empty)
      val player2 = TestHelper.Player("Bob", List(anotherCard))
      val trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val table = Map(attackCard -> Some(defenseCard))

      val gameState = TestHelper.GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = table,
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

      val result = DefensePhaseImpl.playCard(anotherCard, 1, gameState)
      result.lastEvent shouldBe Some(GameEvent.InvalidMove)
    }

    "return InvalidMove when defense card too weak" in {
      val attackCard = TestHelper.Card(Suit.Hearts, Rank.Seven)
      val defenseCard = TestHelper.Card(Suit.Hearts, Rank.Six)
      val player1 = TestHelper.Player("Alice", List.empty)
      val player2 = TestHelper.Player("Bob", List(defenseCard))
      val trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val table = Map(attackCard -> None)

      val gameState = TestHelper.GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = table,
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

      val result = DefensePhaseImpl.playCard(defenseCard, 1, gameState)
      result.lastEvent shouldBe Some(GameEvent.InvalidMove)
    }

    "take cards successfully" in {
      val attackCard = TestHelper.Card(Suit.Hearts, Rank.Six)
      val defenseCard = TestHelper.Card(Suit.Hearts, Rank.Seven)
      val player1 = TestHelper.Player("Alice", List.empty)
      val player2 = TestHelper.Player(
        "Bob",
        List(TestHelper.Card(Suit.Diamonds, Rank.Nine))
      )
      val trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val table = Map(attackCard -> Some(defenseCard))

      val gameState = TestHelper.GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = table,
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

      val result = DefensePhaseImpl.takeCards(1, gameState)
      result.table shouldBe empty
      result.lastEvent shouldBe Some(GameEvent.Take)
      result.gamePhase shouldBe DrawPhaseImpl
      result.players(1).hand.size shouldBe 3
    }

    "return NotYourTurn when attacker tries to take cards" in {
      val attackCard = TestHelper.Card(Suit.Hearts, Rank.Six)
      val player1 = TestHelper.Player("Alice", List.empty)
      val player2 = TestHelper.Player(
        "Bob",
        List(TestHelper.Card(Suit.Diamonds, Rank.Seven))
      )
      val trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val table = Map(attackCard -> None)

      val gameState = TestHelper.GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = table,
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

      val result = DefensePhaseImpl.takeCards(0, gameState)
      result.lastEvent shouldBe Some(GameEvent.NotYourTurn)
    }

    "remain in DefensePhase when not all attacks defended" in {
      val attackCard1 = TestHelper.Card(Suit.Hearts, Rank.Six)
      val attackCard2 = TestHelper.Card(Suit.Diamonds, Rank.Seven)
      val defenseCard = TestHelper.Card(Suit.Hearts, Rank.Eight)
      val player1 = TestHelper.Player("Alice", List.empty)
      val player2 = TestHelper.Player("Bob", List(defenseCard))
      val trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val table = Map(attackCard1 -> None, attackCard2 -> None)

      val gameState = TestHelper.GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = table,
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
        lastAttackerIndex = Some(0)
      )

      val result = DefensePhaseImpl.playCard(defenseCard, 1, gameState)
      result.gamePhase shouldBe DefensePhaseImpl
      result.currentAttackerIndex shouldBe None
    }

    "set next attacker when all attacks defended with multiple players" in {
      val attackCard = TestHelper.Card(Suit.Hearts, Rank.Six)
      val defenseCard = TestHelper.Card(Suit.Hearts, Rank.Seven)
      val player1 = TestHelper.Player("Alice", List.empty)
      val player2 = TestHelper.Player("Bob", List(defenseCard))
      val player3 = TestHelper.Player("Charlie", List.empty)
      val trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val table = Map(attackCard -> None)

      val gameState = TestHelper.GameState(
        players = List(player1, player2, player3),
        deck = List.empty,
        table = table,
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
        lastAttackerIndex = Some(0)
      )

      val result = DefensePhaseImpl.playCard(defenseCard, 1, gameState)
      result.currentAttackerIndex shouldBe Some(2)
    }

    "skip passed players when finding next attacker" in {
      val attackCard = TestHelper.Card(Suit.Hearts, Rank.Six)
      val defenseCard = TestHelper.Card(Suit.Hearts, Rank.Seven)
      val player1 = TestHelper.Player("Alice", List.empty)
      val player2 = TestHelper.Player("Bob", List(defenseCard))
      val player3 = TestHelper.Player("Charlie", List.empty)
      val player4 = TestHelper.Player("David", List.empty)
      val trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val table = Map(attackCard -> None)

      val gameState = TestHelper.GameState(
        players = List(player1, player2, player3, player4),
        deck = List.empty,
        table = table,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = StubGamePhases.setupPhase,
        lastEvent = None,
        passedPlayers = Set(2),
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = Some(0)
      )

      val result = DefensePhaseImpl.playCard(defenseCard, 1, gameState)
      result.currentAttackerIndex shouldBe Some(3)
    }

    "return main attacker when no other players available" in {
      val attackCard = TestHelper.Card(Suit.Hearts, Rank.Six)
      val defenseCard = TestHelper.Card(Suit.Hearts, Rank.Seven)
      val player1 = TestHelper.Player("Alice", List.empty)
      val player2 = TestHelper.Player("Bob", List(defenseCard))
      val player3 = TestHelper.Player("Charlie", List.empty)
      val trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val table = Map(attackCard -> None)

      val gameState = TestHelper.GameState(
        players = List(player1, player2, player3),
        deck = List.empty,
        table = table,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = StubGamePhases.setupPhase,
        lastEvent = None,
        passedPlayers = Set(2),
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = Some(2)
      )

      val result = DefensePhaseImpl.playCard(defenseCard, 1, gameState)
      result.currentAttackerIndex shouldBe Some(0)
    }

    "return None when main attacker is defender" in {
      val attackCard = TestHelper.Card(Suit.Hearts, Rank.Six)
      val defenseCard = TestHelper.Card(Suit.Hearts, Rank.Seven)
      val player1 = TestHelper.Player("Alice", List.empty)
      val player2 = TestHelper.Player("Bob", List(defenseCard))
      val trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val table = Map(attackCard -> None)

      val gameState = TestHelper.GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = table,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 1,
        defenderIndex = 1,
        gamePhase = StubGamePhases.setupPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = Some(0)
      )

      val result = DefensePhaseImpl.playCard(defenseCard, 1, gameState)
      result.currentAttackerIndex shouldBe None
    }

    "return None when main attacker has passed" in {
      val attackCard = TestHelper.Card(Suit.Hearts, Rank.Six)
      val defenseCard = TestHelper.Card(Suit.Hearts, Rank.Seven)
      val player1 = TestHelper.Player("Alice", List.empty)
      val player2 = TestHelper.Player("Bob", List(defenseCard))
      val player3 = TestHelper.Player("Charlie", List.empty)
      val trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val table = Map(attackCard -> None)

      val gameState = TestHelper.GameState(
        players = List(player1, player2, player3),
        deck = List.empty,
        table = table,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = StubGamePhases.setupPhase,
        lastEvent = None,
        passedPlayers = Set(0, 2),
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = Some(2)
      )

      val result = DefensePhaseImpl.playCard(defenseCard, 1, gameState)
      result.currentAttackerIndex shouldBe None
    }
  }
}
