package de.htwg.DurakApp.controller.command.impl

import de.htwg.DurakApp.testutil._

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Suit, Rank, GameState, Player}
import de.htwg.DurakApp.model.state.{GameEvent}
import com.google.inject.Guice

class PassCommandSpec extends AnyWordSpec with Matchers {

  val injector = Guice.createInjector(new de.htwg.DurakApp.DurakModule)
  val gamePhases = injector.getInstance(classOf[de.htwg.DurakApp.model.state.GamePhases])

  "A PassCommand" should {
    "execute pass in attack phase with currentAttackerIndex" in {
      val player1 = TestHelper.Player("Alice", List(TestHelper.Card(Suit.Hearts, Rank.Six)))
      val player2 = TestHelper.Player("Bob", List(TestHelper.Card(Suit.Diamonds, Rank.Seven)))
      val trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val table = Map(TestHelper.Card(Suit.Hearts, Rank.Six) -> None)

      val gameState = TestHelper.GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = table,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = gamePhases.attackPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = Some(0),
        lastAttackerIndex = None
      )

      val command = PassCommand(gamePhases)
      val result = command.execute(gameState)

      result.lastEvent should not be None
    }

    "execute pass in defense phase using defenderIndex" in {
      val card1 = TestHelper.Card(Suit.Hearts, Rank.Six)
      val card2 = TestHelper.Card(Suit.Diamonds, Rank.Seven)
      val player1 = TestHelper.Player("Alice", List(card1))
      val player2 = TestHelper.Player("Bob", List(card2))
      val trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val table = Map(card1 -> None)

      val gameState = TestHelper.GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = table,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = gamePhases.defensePhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )

      val command = PassCommand(gamePhases)
      val result = command.execute(gameState)

      result.lastEvent should not be None
    }

    "use default currentAttackerIndex when None" in {
      val player1 = TestHelper.Player("Alice", List(TestHelper.Card(Suit.Hearts, Rank.Six)))
      val player2 = TestHelper.Player("Bob", List(TestHelper.Card(Suit.Diamonds, Rank.Seven)))
      val trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val table = Map(TestHelper.Card(Suit.Hearts, Rank.Six) -> None)

      val gameState = TestHelper.GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = table,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = gamePhases.attackPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )

      val command = PassCommand(gamePhases)
      val result = command.execute(gameState)

      result.lastEvent should not be None
    }
  }
}
