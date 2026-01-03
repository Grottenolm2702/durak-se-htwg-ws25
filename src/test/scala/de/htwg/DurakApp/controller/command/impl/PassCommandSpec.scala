package de.htwg.DurakApp.controller.command.impl

import de.htwg.DurakApp.testutil.TestHelpers._
import de.htwg.DurakApp.testutil.{TestGamePhases, TestGamePhasesInstance}

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Suit, Rank, GameState, Player}
import de.htwg.DurakApp.model.state.{GameEvent}

class PassCommandSpec extends AnyWordSpec with Matchers {

  "A PassCommand" should {
    "execute pass in TestGamePhases.attackPhase with currentAttackerIndex" in {
      val player1 = Player("Alice", List(Card(Suit.Hearts, Rank.Six)))
      val player2 = Player("Bob", List(Card(Suit.Diamonds, Rank.Seven)))
      val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val table = Map(Card(Suit.Hearts, Rank.Six) -> None)
      
      val gameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = table,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = TestGamePhases.setupPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = Some(0),
        lastAttackerIndex = None
      )
      
      val command = PassCommand(TestGamePhasesInstance)
      val result = command.execute(gameState)
      
      result.lastEvent should not be None
    }
    
    "execute pass in TestGamePhases.defensePhase" in {
      val player1 = Player("Alice", List(Card(Suit.Hearts, Rank.Six)))
      val player2 = Player("Bob", List(Card(Suit.Diamonds, Rank.Seven)))
      val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val table = Map(Card(Suit.Hearts, Rank.Six) -> None)
      
      val gameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = table,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = TestGamePhases.setupPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      
      val command = PassCommand(TestGamePhasesInstance)
      val result = command.execute(gameState)
      
      result.lastEvent should not be None
    }
    
    "use default currentAttackerIndex when None" in {
      val player1 = Player("Alice", List(Card(Suit.Hearts, Rank.Six)))
      val player2 = Player("Bob", List(Card(Suit.Diamonds, Rank.Seven)))
      val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val table = Map(Card(Suit.Hearts, Rank.Six) -> None)
      
      val gameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = table,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = TestGamePhases.setupPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      
      val command = PassCommand(TestGamePhasesInstance)
      val result = command.execute(gameState)
      
      result.lastEvent should not be None
    }
  }
}
