package de.htwg.DurakApp.controller.command.impl
import de.htwg.DurakApp.testutil._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Suit, Rank, GameState, Player}
import de.htwg.DurakApp.testutil.*
class PhaseChangeCommandSpec extends AnyWordSpec with Matchers {
  "A PhaseChangeCommand" should {
    "execute and return same game state" in {
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
        mainAttackerIndex = 0,
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
      val command = PhaseChangeCommand()
      val result = command.execute(gameState)
      result shouldBe gameState
    }
    "undo and return previous game state" in {
      val player1 =
        TestHelper.Player("Alice", List(TestHelper.Card(Suit.Hearts, Rank.Six)))
      val player2 = TestHelper.Player(
        "Bob",
        List(TestHelper.Card(Suit.Diamonds, Rank.Seven))
      )
      val trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val previousGameState = TestHelper.GameState(
        players = List(player1, player2),
        deck = List(TestHelper.Card(Suit.Hearts, Rank.Eight)),
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        mainAttackerIndex = 0,
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
      val currentGameState = previousGameState.copy(deck = List.empty)
      val command = PhaseChangeCommand()
      val result = command.undo(currentGameState, previousGameState)
      result shouldBe previousGameState
      result.deck.size shouldBe 1
    }
  }
}
