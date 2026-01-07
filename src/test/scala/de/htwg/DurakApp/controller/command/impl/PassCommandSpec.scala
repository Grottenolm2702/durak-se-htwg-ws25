package de.htwg.DurakApp.controller.command.impl

import de.htwg.DurakApp.testutil._

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Suit, Rank, GameState, Player}
import de.htwg.DurakApp.model.state.{GameEvent}
import de.htwg.DurakApp.model.state.impl._
import de.htwg.DurakApp.model.impl._

class PassCommandSpec extends AnyWordSpec with Matchers {

  val cardFactory = new CardFactoryImpl
  val playerFactory = new PlayerFactoryImpl

  val gamePhases = new GamePhasesImpl(
    SetupPhaseImpl,
    AskPlayerCountPhaseImpl,
    AskPlayerNamesPhaseImpl,
    AskDeckSizePhaseImpl,
    AskPlayAgainPhaseImpl,
    GameStartPhaseImpl,
    AttackPhaseImpl,
    DefensePhaseImpl,
    DrawPhaseImpl,
    RoundPhaseImpl,
    EndPhaseImpl
  )

  val gameStateFactory =
    new GameStateFactoryImpl(gamePhases, cardFactory, playerFactory)

  "A PassCommand" should {
    "execute pass in attack phase with currentAttackerIndex" in {
      val card1 = cardFactory(Suit.Hearts, Rank.Six)
      val card2 = cardFactory(Suit.Diamonds, Rank.Seven)
      val player1 = playerFactory("Alice", List(card1))
      val player2 = playerFactory("Bob", List(card2))
      val trumpCard = cardFactory(Suit.Clubs, Rank.Ace, isTrump = true)
      val table = Map(card1 -> None)

      val gameState = gameStateFactory(
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
      val card1 = cardFactory(Suit.Hearts, Rank.Six)
      val card2 = cardFactory(Suit.Diamonds, Rank.Seven)
      val player1 = playerFactory("Alice", List.empty)
      val player2 = playerFactory("Bob", List(card2))
      val trumpCard = cardFactory(Suit.Clubs, Rank.Ace, isTrump = true)
      val attackCard = cardFactory(Suit.Hearts, Rank.Six)
      val table = Map(attackCard -> None)

      val gameState = gameStateFactory(
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
      val card1 = cardFactory(Suit.Hearts, Rank.Six)
      val card2 = cardFactory(Suit.Diamonds, Rank.Seven)
      val player1 = playerFactory("Alice", List(card1))
      val player2 = playerFactory("Bob", List(card2))
      val trumpCard = cardFactory(Suit.Clubs, Rank.Ace, isTrump = true)
      val table = Map(card1 -> None)

      val gameState = gameStateFactory(
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
