package de.htwg.DurakApp.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model._
import de.htwg.DurakApp.model.state._
import de.htwg.DurakApp.controller.command.{GameCommand, CommandFactory}
import de.htwg.DurakApp.util.UndoRedoManager

import scala.util.Random

class ControllerSpec extends AnyWordSpec with Matchers {

  val heartAce = Card(Suit.Hearts, Rank.Ace, isTrump = false)
  val spadeSix = Card(Suit.Spades, Rank.Six, isTrump = false)
  val diamondTen = Card(Suit.Diamonds, Rank.Ten, isTrump = false)
  val clubKing = Card(Suit.Clubs, Rank.King, isTrump = false)

  val defaultTrumpCard: Card = Card(Suit.Clubs, Rank.Six, isTrump = true)
  val defaultTable: Map[Card, Option[Card]] = Map.empty
  val defaultDiscardPile: List[Card] = List.empty
  val defaultAttackerIndex: Int = 0
  val defaultDefenderIndex: Int = 1
  val defaultGamePhase: GamePhase = SetupPhase
  val defaultLastEvent: Option[GameEvent] = None
  val defaultPassedPlayers: Set[Int] = Set.empty
  val defaultRoundWinner: Option[Int] = None

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
      roundWinner: Option[Int] = defaultRoundWinner,
      setupPlayerCount: Option[Int] = None,
      setupPlayerNames: List[String] = List.empty,
      setupDeckSize: Option[Int] = None
  ): GameState = {
    GameState(
      players,
      deck,
      table,
      discardPile,
      trumpCard,
      attackerIndex,
      defenderIndex,
      gamePhase,
      lastEvent,
      passedPlayers,
      roundWinner,
      setupPlayerCount,
      setupPlayerNames,
      setupDeckSize
    )
  }

  "A Controller" should {

    "be initialized with a given GameState" in {
      val initialPlayers = List(Player("TestPlayer", List.empty))
      val initialGameState = createGameState(
        players = initialPlayers,
        roundWinner = defaultRoundWinner
      )
      val controller = new Controller(
        initialGameState,
        UndoRedoManager()
      )

      controller.gameState.players.head.name.shouldBe("TestPlayer")
    }

    "process player action for playing a card" in {
      val player1 = Player("P1", List(spadeSix, heartAce))
      val player2 = Player("P2", List(diamondTen))
      val initialGameState = createGameState(
        players = List(player1, player2),
        trumpCard = heartAce.copy(isTrump = true),
        gamePhase = AttackPhase,
        attackerIndex = 0,
        defenderIndex = 1
      )
      val controller = new Controller(
        initialGameState,
        UndoRedoManager()
      )

      controller.processPlayerAction(PlayCardAction(spadeSix))

      val updatedGameState = controller.gameState
      updatedGameState.players.head.hand.size.shouldBe(1)
      updatedGameState.table.keys.head.shouldBe(spadeSix)
    }

    "process player action for passing" in {
      val player1 = Player("P1", List(spadeSix))
      val player2 = Player("P2", List(diamondTen))
      val initialGameState = createGameState(
        players = List(player1, player2),
        trumpCard = heartAce.copy(isTrump = true),
        gamePhase = AttackPhase,
        table = Map(clubKing -> None),
        attackerIndex = 0,
        defenderIndex = 1
      )
      val controller = new Controller(
        initialGameState,
        UndoRedoManager()
      )

      controller.processPlayerAction(PassAction)

      val updatedGameState = controller.gameState

      updatedGameState.gamePhase.shouldBe(AttackPhase)
      updatedGameState.roundWinner.isDefined.shouldBe(false)
    }

    "process player action for taking cards" in {
      val player1 = Player("P1", List(spadeSix))
      val player2 = Player("P2", List(diamondTen))
      val initialGameState = createGameState(
        players = List(player1, player2),
        trumpCard = heartAce.copy(isTrump = true),
        gamePhase = DefensePhase,
        table = Map(clubKing -> None),
        attackerIndex = 0,
        defenderIndex = 1
      )
      val controller = new Controller(
        initialGameState,
        UndoRedoManager()
      )

      controller.processPlayerAction(TakeCardsAction)

      val updatedGameState = controller.gameState
      updatedGameState.players(1).hand.should(contain(clubKing))
      updatedGameState.gamePhase.shouldBe(AttackPhase)
    }

    "return a status string based on the current game phase" in {
      val initialGameState = createGameState(
        players = List(Player("Test", List.empty)),
        gamePhase = AttackPhase
      )
      val controller = new Controller(
        initialGameState,
        UndoRedoManager()
      )
      controller.getStatusString().shouldBe("AttackPhase")
    }

    "update lastEvent when an InvalidAction is processed" in {
      val player1 = Player("P1", List(spadeSix))
      val initialGameState = createGameState(
        players = List(player1),
        gamePhase = AttackPhase
      )
      val controller = new Controller(
        initialGameState,
        UndoRedoManager()
      )

      controller.processPlayerAction(InvalidAction)

      controller.gameState.lastEvent should be(Some(GameEvent.InvalidMove))
    }

    "handle undo correctly" in {
      val player1 = Player("P1", List(spadeSix))
      val player2 = Player("P2", List(diamondTen))
      val initialGameState = createGameState(
        players = List(player1, player2),
        trumpCard = heartAce.copy(isTrump = true),
        gamePhase = AttackPhase,
        attackerIndex = 0,
        defenderIndex = 1
      )
      val controller = new Controller(
        initialGameState,
        UndoRedoManager()
      )

      controller.processPlayerAction(PlayCardAction(spadeSix))
      val gameStateAfterPlay = controller.gameState

      controller.undo()
      val gameStateAfterUndo = controller.gameState

      gameStateAfterUndo shouldBe initialGameState
    }

    "handle redo correctly" in {
      val player1 = Player("P1", List(spadeSix))
      val player2 = Player("P2", List(diamondTen))
      val initialGameState = createGameState(
        players = List(player1, player2),
        trumpCard = heartAce.copy(isTrump = true),
        gamePhase = AttackPhase,
        attackerIndex = 0,
        defenderIndex = 1
      )
      val controller = new Controller(
        initialGameState,
        UndoRedoManager()
      )

      controller.processPlayerAction(PlayCardAction(spadeSix))
      val gameStateAfterPlay = controller.gameState

      controller.undo()
      val gameStateAfterUndo = controller.gameState
      gameStateAfterUndo shouldBe initialGameState

      controller.redo()
      val gameStateAfterRedo = controller.gameState

      gameStateAfterRedo shouldBe gameStateAfterPlay
    }

    "not undo when no actions have been performed (initial state)" in {
      val player1 = Player("P1", List(spadeSix))
      val initialGameState = createGameState(
        players = List(player1),
        gamePhase = AttackPhase,
        trumpCard = defaultTrumpCard
      )
      val controller = new Controller(
        initialGameState,
        UndoRedoManager()
      )

      val gameStateBeforeUndo = controller.gameState
      controller.undo()
      controller.gameState shouldBe gameStateBeforeUndo.copy(lastEvent =
        Some(GameEvent.CannotUndo)
      )
    }

    "not redo when no actions have been undone" in {
      val player1 = Player("P1", List(spadeSix))
      val initialGameState = createGameState(
        players = List(player1),
        gamePhase = AttackPhase,
        trumpCard = defaultTrumpCard
      )
      val controller = new Controller(
        initialGameState,
        UndoRedoManager()
      )

      val gameStateBeforeRedo = controller.gameState
      controller.redo()
      controller.gameState shouldBe gameStateBeforeRedo.copy(lastEvent =
        Some(GameEvent.CannotRedo)
      )
    }

    "handle PlayAgainAction by resetting the game state" in {
      val player1 = Player("P1", List.empty, isDone = true)
      val player2 = Player("P2", List.empty, isDone = true)
      val initialGameState = createGameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = Card(Suit.Diamonds, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = AskPlayAgainPhase,
        setupPlayerNames = List("P1", "P2"),
        setupDeckSize = Some(36)
      )
      val controller = new Controller(
        initialGameState,
        UndoRedoManager()
      )

      controller.processPlayerAction(PlayAgainAction)
      val updatedGameState = controller.gameState

      updatedGameState.gamePhase should not be AskPlayAgainPhase
      updatedGameState.lastEvent shouldBe Some(GameEvent.GameSetupComplete)
      updatedGameState.players.size shouldBe 2
      updatedGameState.players.forall(_.hand.nonEmpty) shouldBe true
      updatedGameState.deck.nonEmpty shouldBe true
      updatedGameState.table shouldBe Map.empty
      updatedGameState.discardPile shouldBe List.empty
    }

    "handle ExitGameAction by setting GameEvent.ExitApplication" in {
      val initialGameState = createGameState(
        players = List(Player("P1", List.empty)),
        gamePhase = AskPlayAgainPhase
      )
      val controller = new Controller(
        initialGameState,
        UndoRedoManager()
      )

      controller.processPlayerAction(ExitGameAction)
      val updatedGameState = controller.gameState

      updatedGameState.lastEvent shouldBe Some(GameEvent.ExitApplication)
    }
  }
}
