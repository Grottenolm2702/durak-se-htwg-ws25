package de.htwg.DurakApp.model.state.impl
import de.htwg.DurakApp.testutil._
import de.htwg.DurakApp.testutil.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Suit, Rank, GameState, Player}
import de.htwg.DurakApp.model.state.GameEvent
class GameStartPhaseImplSpec extends AnyWordSpec with Matchers {
  "GameStartPhaseImpl" should {
    "have correct string representation" in {
      GameStartPhaseImpl.toString shouldBe "GameStartPhase"
    }
    "handle sets GameSetupComplete event" in {
      val player1 =
        TestHelper.Player("Alice", List(TestHelper.Card(Suit.Hearts, Rank.Six)))
      val player2 = TestHelper.Player(
        "Bob",
        List(TestHelper.Card(Suit.Diamonds, Rank.Seven))
      )
      val trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val gameState = TestHelper.GameState(
        players = List(player1, player2),
        deck = List(TestHelper.Card(Suit.Spades, Rank.Eight)),
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        mainAttackerIndex = 0,
        defenderIndex = 1,
        gamePhase = GameStartPhaseImpl,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = Some(2),
        setupPlayerNames = List("Alice", "Bob"),
        setupDeckSize = Some(36),
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      val result = GameStartPhaseImpl.handle(gameState)
      result.lastEvent shouldBe Some(GameEvent.GameSetupComplete)
    }
  }
}
