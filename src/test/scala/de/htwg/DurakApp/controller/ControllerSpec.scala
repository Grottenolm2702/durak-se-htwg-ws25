package de.htwg.DurakApp.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model._
import de.htwg.DurakApp.model.state._ // Import all state phases
import de.htwg.DurakApp.controller.command.{Command, CommandFactory}

import scala.util.Random

class ControllerSpec extends AnyWordSpec with Matchers {

  // Define some cards for convenience, not necessarily trumps by default
  val heartAce = Card(Suit.Hearts, Rank.Ace, isTrump = false)
  val spadeSix = Card(Suit.Spades, Rank.Six, isTrump = false)
  val diamondTen = Card(Suit.Diamonds, Rank.Ten, isTrump = false)
  val clubKing = Card(Suit.Clubs, Rank.King, isTrump = false)

  // Default values for GameState instantiation
  val defaultTrumpCard: Card = Card(Suit.Clubs, Rank.Six, isTrump = true) // A default trump card
  val defaultTable: Map[Card, Option[Card]] = Map.empty
  val defaultDiscardPile: List[Card] = List.empty
  val defaultAttackerIndex: Int = 0
  val defaultDefenderIndex: Int = 1
  val defaultGamePhase: GamePhase = SetupPhase // Default starting phase
  val defaultLastEvent: Option[GameEvent] = None
  val defaultPassedPlayers: Set[Int] = Set.empty
  val defaultRoundWinner: Option[Int] = None

  // Helper function to create GameState with defaults
  def createGameState(
      players: List[Player],
      deck: List[Card] = List.empty,
      table: Map[Card, Option[Card]] = defaultTable,
      discardPile: List[Card] = defaultDiscardPile,
      trumpCard: Card = defaultTrumpCard,
      attackerIndex: Int = defaultAttackerIndex,
      defenderIndex: Int = defaultDefenderIndex,
      gamePhase: GamePhase = defaultGamePhase,
      lastEvent: Option[GameEvent] = defaultLastEvent,
      passedPlayers: Set[Int] = defaultPassedPlayers,
      roundWinner: Option[Int] = defaultRoundWinner
  ): GameState = {
    GameState(players, deck, table, discardPile, trumpCard, attackerIndex, defenderIndex, gamePhase, lastEvent, passedPlayers, roundWinner)
  }

  "A Controller" should {

    "be initialized with a given GameState" in {
      val initialPlayers = List(Player("TestPlayer", List.empty))
      val initialGameState = createGameState(players = initialPlayers)
      val controller = new Controller(initialGameState)

      controller.gameState.players.head.name.shouldBe("TestPlayer")
    }

    "process player action for playing a card" in {
      val player1 = Player("P1", List(spadeSix, heartAce))
      val player2 = Player("P2", List(diamondTen))
      val initialGameState = createGameState(
        players = List(player1, player2),
        trumpCard = heartAce.copy(isTrump = true), // Ensure trump is set for game logic
        gamePhase = AttackPhase,
        attackerIndex = 0,
        defenderIndex = 1
      )
      val controller = new Controller(initialGameState)

      controller.processPlayerAction(PlayCardAction("0")) // Play spade six (first card)

      val updatedGameState = controller.gameState
      updatedGameState.players.head.hand.size.shouldBe(1) // P1 played a card
      updatedGameState.table.keys.head.shouldBe(spadeSix) // Card should be on the table
    }

    "process player action for passing" in {
      val player1 = Player("P1", List(spadeSix))
      val player2 = Player("P2", List(diamondTen))
      val initialGameState = createGameState(
        players = List(player1, player2),
        trumpCard = heartAce.copy(isTrump = true),
        gamePhase = AttackPhase,
        table = Map(clubKing -> None), // Attacker attacks, defender passes
        attackerIndex = 0,
        defenderIndex = 1
      )
      val controller = new Controller(initialGameState)

      controller.processPlayerAction(PassAction)

      val updatedGameState = controller.gameState
      // Defender should now have passed, and game phase should transition if all passes are done or round ends
      updatedGameState.gamePhase.shouldBe(AttackPhase) // Attacker passed, so should transition through DrawPhase and RoundPhase to AttackPhase
      updatedGameState.roundWinner.isDefined.shouldBe(true) // Attacker passed, defender wins round
    }

    "process player action for taking cards" in {
      val player1 = Player("P1", List(spadeSix))
      val player2 = Player("P2", List(diamondTen))
      val initialGameState = createGameState(
        players = List(player1, player2),
        trumpCard = heartAce.copy(isTrump = true),
        gamePhase = DefensePhase,
        table = Map(clubKing -> None), // Attacker played, defender takes
        attackerIndex = 0,
        defenderIndex = 1
      )
      val controller = new Controller(initialGameState)

      controller.processPlayerAction(TakeCardsAction)

      val updatedGameState = controller.gameState
      updatedGameState.players(1).hand.should(contain(clubKing)) // Defender took the card
      updatedGameState.gamePhase.shouldBe(AttackPhase) // After taking cards and redrawing, it should be AttackPhase
    }

    "return a status string based on the current game phase" in {
      val initialGameState = createGameState(
        players = List(Player("Test", List.empty)),
        gamePhase = AttackPhase
      )
      val controller = new Controller(initialGameState)
      controller.getStatusString().shouldBe("AttackPhase")
    }
  }
}