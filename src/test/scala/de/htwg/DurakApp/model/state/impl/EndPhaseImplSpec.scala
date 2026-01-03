package de.htwg.DurakApp.model.state.impl

import de.htwg.DurakApp.testutil.TestHelpers._
import de.htwg.DurakApp.testutil.TestGamePhases

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Suit, Rank, GameState, Player}
import de.htwg.DurakApp.model.state.GameEvent

class EndPhaseImplSpec extends AnyWordSpec with Matchers {

  "EndPhaseImpl" should {
    "have correct string representation" in {
      EndPhaseImpl.toString shouldBe "EndPhase"
    }
    
    "handle game over with winner and loser" in {
      val player1 = Player("Alice", List.empty)
      val player2 = Player("Bob", List(Card(Suit.Hearts, Rank.Six)))
      val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)
      
      val gameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = EndPhaseImpl,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      
      val result = EndPhaseImpl.handle(gameState)
      result.gamePhase shouldBe AskPlayAgainPhaseImpl
      result.lastEvent match {
        case Some(GameEvent.GameOver(winner, Some(loser))) =>
          winner.name shouldBe "Alice"
          loser.name shouldBe "Bob"
        case _ => fail("Expected GameOver event with winner and loser")
      }
    }
    
    "handle game over when all players have no cards" in {
      val player1 = Player("Alice", List.empty)
      val player2 = Player("Bob", List.empty)
      val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)
      
      val gameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = EndPhaseImpl,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      
      val result = EndPhaseImpl.handle(gameState)
      result.gamePhase shouldBe AskPlayAgainPhaseImpl
      result.lastEvent match {
        case Some(GameEvent.GameOver(winner, None)) =>
          winner.name shouldBe "Alice"
        case _ => fail("Expected GameOver event with winner only")
      }
    }
    
    "identify correct winner when multiple players" in {
      val player1 = Player("Alice", List(Card(Suit.Hearts, Rank.Six)))
      val player2 = Player("Bob", List.empty)
      val player3 = Player("Charlie", List(Card(Suit.Diamonds, Rank.Seven)))
      val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)
      
      val gameState = GameState(
        players = List(player1, player2, player3),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = EndPhaseImpl,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      
      val result = EndPhaseImpl.handle(gameState)
      result.lastEvent match {
        case Some(GameEvent.GameOver(winner, Some(loser))) =>
          winner.name shouldBe "Bob"
        case _ => fail("Expected GameOver event")
      }
    }
  }
}
