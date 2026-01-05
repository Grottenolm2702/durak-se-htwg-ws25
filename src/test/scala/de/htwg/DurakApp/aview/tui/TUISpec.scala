package de.htwg.DurakApp.aview.tui

import de.htwg.DurakApp.testutil.TestHelpers._
import de.htwg.DurakApp.testutil.TestGamePhases
import de.htwg.DurakApp.model.state.GamePhases

object TestGamePhases extends GamePhases {
  def setupPhase = de.htwg.DurakApp.testutil.TestGamePhases.setupPhase
  def askPlayerCountPhase =
    de.htwg.DurakApp.testutil.TestGamePhases.askPlayerCountPhase
  def askPlayerNamesPhase =
    de.htwg.DurakApp.testutil.TestGamePhases.askPlayerNamesPhase
  def askDeckSizePhase =
    de.htwg.DurakApp.testutil.TestGamePhases.askDeckSizePhase
  def askPlayAgainPhase =
    de.htwg.DurakApp.testutil.TestGamePhases.askPlayAgainPhase
  def gameStartPhase = de.htwg.DurakApp.testutil.TestGamePhases.gameStartPhase
  def attackPhase = de.htwg.DurakApp.testutil.TestGamePhases.attackPhase
  def defensePhase = de.htwg.DurakApp.testutil.TestGamePhases.defensePhase
  def drawPhase = de.htwg.DurakApp.testutil.TestGamePhases.drawPhase
  def roundPhase = de.htwg.DurakApp.testutil.TestGamePhases.roundPhase
  def endPhase = de.htwg.DurakApp.testutil.TestGamePhases.endPhase
}

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Player, Suit, Rank, GameState}
import de.htwg.DurakApp.model.state._
import de.htwg.DurakApp.testutil.{
  TestHelper,
  StubGameSetup,
  StubUndoRedoManager,
  SpyController
}
import de.htwg.DurakApp.controller.Controller
import com.google.inject.Guice
import java.io.{PrintStream, OutputStream}

class TUISpec extends AnyWordSpec with Matchers {

  // Null output stream to suppress console output in tests
  val nullOutputStream = new PrintStream(new OutputStream {
    override def write(b: Int): Unit = ()
  })

  "A TUI" should {

    "be created with a controller" in {
      val initialState = TestHelper.createTestGameState()
      val controller = new SpyController(
        initialState,
        new StubUndoRedoManager(),
        new StubGameSetup()
      )
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)

      tui should not be null
    }

    "build status string for attack phase" in {
      val attacker = Player("Angreifer", List.empty)
      val defender = Player("Verteidiger", List.empty)
      val game = TestHelper.createTestGameState(
        players = List(attacker, defender),
        gamePhase = TestGamePhases.setupPhase,
        lastEvent = Some(GameEvent.Attack(Card(Suit.Spades, Rank.Six)))
      )

      val controller =
        new SpyController(game, new StubUndoRedoManager(), new StubGameSetup())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val statusString = tui.buildStatusString(game)

      statusString should not be empty
      statusString should include("Angriff")
    }

    "respond to update notification" in {
      val initialState = TestHelper.createTestGameState()
      val controller = new SpyController(
        initialState,
        new StubUndoRedoManager(),
        new StubGameSetup()
      )

      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      noException should be thrownBy tui.update
    }

    "show correct description for TestGamePhases.setupPhase" in {
      val gameState =
        TestHelper.createTestGameState(gamePhase = TestGamePhases.setupPhase)
      val controller = new SpyController(
        gameState,
        new StubUndoRedoManager(),
        new StubGameSetup()
      )
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)

      val desc = tui.description(gameState)

      desc should include("Spieleranzahl")
    }

    "show correct description for TestGamePhases.askPlayerCountPhase" in {
      val gameState = TestHelper.createTestGameState(gamePhase =
        TestGamePhases.askPlayerCountPhase
      )
      val controller = new SpyController(
        gameState,
        new StubUndoRedoManager(),
        new StubGameSetup()
      )
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)

      val desc = tui.description(gameState)

      desc should include("Spieleranzahl")
    }

    "show correct description for TestGamePhases.askPlayerNamesPhase" in {
      val gameState = TestHelper.createTestGameState(
        gamePhase = TestGamePhases.askPlayerNamesPhase,
        setupPlayerNames = List("Alice")
      )
      val controller = new SpyController(
        gameState,
        new StubUndoRedoManager(),
        new StubGameSetup()
      )
      val tui = new TUI(
        controller,
        de.htwg.DurakApp.testutil.TestGamePhasesInstance,
        nullOutputStream
      )

      val desc = tui.description(gameState)

      desc should include("Spielername")
      desc should include("2")
    }

    "show correct description for TestGamePhases.askDeckSizePhase" in {
      val gameState = TestHelper.createTestGameState(
        gamePhase = TestGamePhases.askDeckSizePhase,
        setupPlayerNames = List("Alice", "Bob")
      )
      val controller = new SpyController(
        gameState,
        new StubUndoRedoManager(),
        new StubGameSetup()
      )
      val tui = new TUI(
        controller,
        de.htwg.DurakApp.testutil.TestGamePhasesInstance,
        nullOutputStream
      )

      val desc = tui.description(gameState)

      desc should include("Deckgröße")
      desc should include("2")
    }

    "show correct description for TestGamePhases.askPlayAgainPhase" in {
      val gameState = TestHelper.createTestGameState(gamePhase =
        TestGamePhases.askPlayAgainPhase
      )
      val controller = new SpyController(
        gameState,
        new StubUndoRedoManager(),
        new StubGameSetup()
      )
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)

      val desc = tui.description(gameState)

      desc should include("neue Runde")
    }

    "show correct description for TestGamePhases.attackPhase" in {
      val gameState =
        TestHelper.createTestGameState(gamePhase = TestGamePhases.attackPhase)
      val controller = new SpyController(
        gameState,
        new StubUndoRedoManager(),
        new StubGameSetup()
      )
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)

      val desc = tui.description(gameState)

      desc should include("AttackPhase")
    }

    "build status string contains important information" in {
      val player1 = Player("Alice", List(Card(Suit.Hearts, Rank.Six)))
      val player2 = Player("Bob", List(Card(Suit.Diamonds, Rank.Seven)))
      val gameState = TestHelper.createTestGameState(
        players = List(player1, player2),
        gamePhase = TestGamePhases.attackPhase
      )
      val controller = new SpyController(
        gameState,
        new StubUndoRedoManager(),
        new StubGameSetup()
      )
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)

      val statusString = tui.buildStatusString(gameState)

      statusString should not be empty
      statusString.length should be > 0
    }

    "build status string for TestGamePhases.defensePhase" in {
      val player1 = Player("Alice", List(Card(Suit.Hearts, Rank.Six)))
      val player2 = Player("Bob", List(Card(Suit.Diamonds, Rank.Seven)))
      val gameState = TestHelper.createTestGameState(
        players = List(player1, player2),
        gamePhase = TestGamePhases.setupPhase,
        lastEvent = Some(GameEvent.Defend(Card(Suit.Hearts, Rank.Seven)))
      )
      val controller = new SpyController(
        gameState,
        new StubUndoRedoManager(),
        new StubGameSetup()
      )
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)

      val statusString = tui.buildStatusString(gameState)

      statusString should not be empty
      statusString should include("Verteidigung")
    }

    "build status string for GameOver event contains end message" in {
      val player1 = Player("Alice", List.empty)
      val player2 = Player("Bob", List(Card(Suit.Hearts, Rank.Six)))
      val gameState = TestHelper.createTestGameState(
        players = List(player1, player2),
        gamePhase = TestGamePhases.setupPhase,
        lastEvent = Some(GameEvent.GameOver(player1, Some(player2)))
      )
      val controller = new SpyController(
        gameState,
        new StubUndoRedoManager(),
        new StubGameSetup()
      )
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)

      val statusString = tui.buildStatusString(gameState)

      statusString should include("Spiel")
    }
  }
}
