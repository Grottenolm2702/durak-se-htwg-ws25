package de.htwg.DurakApp.controller.command.impl
import de.htwg.DurakApp.testutil._
import de.htwg.DurakApp.testutil.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Suit, Rank, GameState, Player}
import de.htwg.DurakApp.model.state.{GameEvent}
class TakeCardsCommandSpec extends AnyWordSpec with Matchers {
  "A TakeCardsCommand" should {
    "execute takeCards for defender" in {
      val player1 =
        TestHelper.Player("Alice", List(TestHelper.Card(Suit.Hearts, Rank.Six)))
      val player2 = TestHelper.Player(
        "Bob",
        List(TestHelper.Card(Suit.Diamonds, Rank.Seven))
      )
      val trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val table = Map(TestHelper.Card(Suit.Hearts, Rank.Eight) -> None)
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
      val command = TakeCardsCommand()
      val result = command.execute(gameState)
      result.lastEvent should not be None
    }
    "use defenderIndex from game state" in {
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
      val table = Map(TestHelper.Card(Suit.Hearts, Rank.Eight) -> None)
      val gameState = TestHelper.GameState(
        players = List(player1, player2, player3),
        deck = List.empty,
        table = table,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 2,
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
      val command = TakeCardsCommand()
      val result = command.execute(gameState)
      result.defenderIndex shouldBe 2
    }
  }
}
