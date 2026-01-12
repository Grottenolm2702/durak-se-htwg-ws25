package de.htwg.DurakApp.model.state
import de.htwg.DurakApp.testutil._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Player, GameState, Suit, Rank}
class GamePhaseSpec extends AnyWordSpec with Matchers {
  "GameEvent" should {
    "have correct InvalidMove event" in {
      GameEvent.InvalidMove shouldBe GameEvent.InvalidMove
    }
    "have correct NotYourTurn event" in {
      GameEvent.NotYourTurn shouldBe GameEvent.NotYourTurn
    }
    "have correct Attack event with card" in {
      val card = TestHelper.Card(Suit.Clubs, Rank.Ace)
      val attackEvent = GameEvent.Attack(card)
      attackEvent.card shouldBe card
    }
    "have correct Defend event with card" in {
      val card = TestHelper.Card(Suit.Diamonds, Rank.King)
      val defendEvent = GameEvent.Defend(card)
      defendEvent.defenseCard shouldBe card
    }
    "have correct Pass event" in {
      GameEvent.Pass shouldBe GameEvent.Pass
    }
    "have correct Take event" in {
      GameEvent.Take shouldBe GameEvent.Take
    }
    "have correct Draw event" in {
      GameEvent.Draw shouldBe GameEvent.Draw
    }
    "have correct RoundEnd event with cleared status" in {
      val roundEndTrue = GameEvent.RoundEnd(true)
      roundEndTrue.cleared shouldBe true
      val roundEndFalse = GameEvent.RoundEnd(false)
      roundEndFalse.cleared shouldBe false
    }
    "have correct GameOver event with winner and optional loser" in {
      val winner = TestHelper.Player("Winner")
      val loser = TestHelper.Player("Loser")
      val gameOverWithLoser = GameEvent.GameOver(winner, Some(loser))
      gameOverWithLoser.winner shouldBe winner
      gameOverWithLoser.loser shouldBe Some(loser)
      val gameOverNoLoser = GameEvent.GameOver(winner, None)
      gameOverNoLoser.winner shouldBe winner
      gameOverNoLoser.loser shouldBe None
    }
  }
  "GamePhase trait's default methods" should {
    val testGamePhase = new GamePhase {
      override def handle(gameState: GameState): GameState = gameState
    }
    val initialGameState = TestHelper.GameState(
      players = List(TestHelper.Player("P1", List.empty)),
      deck = List.empty,
      table = Map.empty,
      discardPile = List.empty,
      trumpCard = TestHelper.Card(Suit.Hearts, Rank.Six),
      mainAttackerIndex = 0,
      defenderIndex = 0,
      gamePhase = testGamePhase,
      lastEvent = None,
      passedPlayers = Set.empty,
      roundWinner = None,
      setupPlayerCount = None,
      setupPlayerNames = List.empty,
      setupDeckSize = None,
      currentAttackerIndex = None,
      lastAttackerIndex = None
    )
    val testCard = TestHelper.Card(Suit.Spades, Rank.Seven)
    "return InvalidMove for default playCard" in {
      val resultState = testGamePhase.playCard(testCard, 0, initialGameState)
      resultState.lastEvent shouldBe Some(GameEvent.InvalidMove)
      resultState shouldBe initialGameState.copy(lastEvent =
        Some(GameEvent.InvalidMove)
      )
    }
    "return InvalidMove for default pass" in {
      val resultState = testGamePhase.pass(0, initialGameState)
      resultState.lastEvent shouldBe Some(GameEvent.InvalidMove)
      resultState shouldBe initialGameState.copy(lastEvent =
        Some(GameEvent.InvalidMove)
      )
    }
    "return InvalidMove for default takeCards" in {
      val resultState = testGamePhase.takeCards(0, initialGameState)
      resultState.lastEvent shouldBe Some(GameEvent.InvalidMove)
      resultState shouldBe initialGameState.copy(lastEvent =
        Some(GameEvent.InvalidMove)
      )
    }
  }
}
