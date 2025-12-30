package de.htwg.DurakApp.model.state.impl

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Suit, Rank, GameState, Player}
import de.htwg.DurakApp.model.state.{AttackPhase, GameEvent}

class RoundPhaseImplSpec extends AnyWordSpec with Matchers {

  "RoundPhaseImpl" should {
    "have correct string representation" in {
      RoundPhaseImpl.toString shouldBe "RoundPhase"
    }
    
    "clear table when round won" in {
      val card1 = Card(Suit.Hearts, Rank.Six)
      val card2 = Card(Suit.Hearts, Rank.Seven)
      val player1 = Player("Alice", List(Card(Suit.Hearts, Rank.Eight)))
      val player2 = Player("Bob", List(Card(Suit.Diamonds, Rank.Nine)))
      val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val table = Map(card1 -> Some(card2))
      
      val gameState = GameState(
        players = List(player1, player2),
        deck = List(Card(Suit.Spades, Rank.Ten)),
        table = table,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = RoundPhaseImpl,
        lastEvent = None,
        passedPlayers = Set(0),
        roundWinner = Some(1),
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      
      val result = RoundPhaseImpl.handle(gameState)
      result.table shouldBe empty
      result.discardPile.size shouldBe 2
      result.passedPlayers shouldBe empty
      result.roundWinner shouldBe None
      result.gamePhase shouldBe AttackPhase
      result.lastEvent shouldBe Some(GameEvent.RoundEnd(cleared = true))
    }
    
    "not clear table when round lost" in {
      val player1 = Player("Alice", List(Card(Suit.Hearts, Rank.Eight)))
      val player2 = Player("Bob", List(Card(Suit.Diamonds, Rank.Nine)))
      val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)
      
      val gameState = GameState(
        players = List(player1, player2),
        deck = List(Card(Suit.Spades, Rank.Ten)),
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = RoundPhaseImpl,
        lastEvent = None,
        passedPlayers = Set(0),
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      
      val result = RoundPhaseImpl.handle(gameState)
      result.discardPile shouldBe empty
      result.lastEvent shouldBe Some(GameEvent.RoundEnd(cleared = false))
    }
    
    "transition to end phase when game over" in {
      val player1 = Player("Alice", List(Card(Suit.Hearts, Rank.Eight)))
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
        gamePhase = RoundPhaseImpl,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = Some(1),
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      
      val result = RoundPhaseImpl.handle(gameState)
      result.gamePhase shouldBe AskPlayAgainPhaseImpl
    }
    
    "continue game when multiple players have cards" in {
      val player1 = Player("Alice", List(Card(Suit.Hearts, Rank.Eight)))
      val player2 = Player("Bob", List(Card(Suit.Diamonds, Rank.Nine)))
      val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)
      
      val gameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = RoundPhaseImpl,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = Some(1),
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      
      val result = RoundPhaseImpl.handle(gameState)
      result.gamePhase shouldBe AttackPhase
    }
  }
}
