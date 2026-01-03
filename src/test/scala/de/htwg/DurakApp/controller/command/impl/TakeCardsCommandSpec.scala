package de.htwg.DurakApp.controller.command.impl

import de.htwg.DurakApp.testutil.TestHelpers._

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Suit, Rank, GameState, Player}
import de.htwg.DurakApp.model.state.{GameEvent, DefensePhase}

class TakeCardsCommandSpec extends AnyWordSpec with Matchers {

  "A TakeCardsCommand" should {
    "execute takeCards for defender" in {
      val player1 = Player("Alice", List(Card(Suit.Hearts, Rank.Six)))
      val player2 = Player("Bob", List(Card(Suit.Diamonds, Rank.Seven)))
      val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val table = Map(Card(Suit.Hearts, Rank.Eight) -> None)
      
      val gameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = table,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = DefensePhase,
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
      val player1 = Player("Alice", List(Card(Suit.Hearts, Rank.Six)))
      val player2 = Player("Bob", List(Card(Suit.Diamonds, Rank.Seven)))
      val player3 = Player("Charlie", List(Card(Suit.Spades, Rank.Nine)))
      val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val table = Map(Card(Suit.Hearts, Rank.Eight) -> None)
      
      val gameState = GameState(
        players = List(player1, player2, player3),
        deck = List.empty,
        table = table,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 2,
        gamePhase = DefensePhase,
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
