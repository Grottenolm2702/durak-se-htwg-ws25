package de.htwg.DurakApp
package aview

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.controller.Controller
import de.htwg.DurakApp.model._
import de.htwg.DurakApp.model.state._ // Import all state phases and GameEvent
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import de.htwg.DurakApp.util.Observable

class TUISpec extends AnyWordSpec with Matchers {

  // Default values for GameState instantiation
  val defaultTrumpCard: Card = Card(Suit.Clubs, Rank.Six, isTrump = true)
  val defaultTable: Map[Card, Option[Card]] = Map.empty
  val defaultDiscardPile: List[Card] = List.empty
  val defaultAttackerIndex: Int = 0
  val defaultDefenderIndex: Int = 1
  val defaultGamePhase: GamePhase = SetupPhase // Default starting phase
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
      roundWinner: Option[Int] = defaultRoundWinner
  ): GameState = {
    GameState(players, deck, table, discardPile, trumpCard, attackerIndex, defenderIndex, gamePhase, lastEvent, passedPlayers, roundWinner)
  }

  // Define some cards for convenience
  val heartAce = Card(Suit.Hearts, Rank.Ace, isTrump = false)
  val spadeSix = Card(Suit.Spades, Rank.Six, isTrump = false)
  val diamondTen = Card(Suit.Diamonds, Rank.Ten, isTrump = false)
  val clubKing = Card(Suit.Clubs, Rank.King, isTrump = false)

  "A TUI" should {

    "buildStatusString - SetupPhase (Welcome)" in {
      val game = createGameState(players = List.empty, gamePhase = SetupPhase, lastEvent = None) // Initial state for welcome
      val controller = new Controller(game)
      val tui = new TUI(controller)
      tui.buildStatusString(game).shouldBe("Willkommen bei Durak!")
    }

    "buildStatusString - SetupPhase (Player Setup)" in {
      val game = createGameState(players = List(Player("TestPlayer", List.empty)), gamePhase = SetupPhase, lastEvent = None)
      val controller = new Controller(game)
      val tui = new TUI(controller)
      tui.buildStatusString(game).shouldBe("Spieler werden eingerichtet.")
    }

    "buildStatusString - AttackPhase shows attacker name" in {
      val attacker = Player("Angreifer", List.empty)
      val defender = Player("Verteidiger", List.empty)
      val game = createGameState(
        players = List(attacker, defender),
        gamePhase = AttackPhase,
        attackerIndex = 0,
        lastEvent = Some(GameEvent.Attack(spadeSix))
      )
      val controller = new Controller(game)
      val tui = new TUI(controller)
      tui.buildStatusString(game).should(include ("Angriff mit Six Spades."))
    }

    "buildStatusString - DefensePhase shows defender name" in {
      val a = Player("A", List.empty)
      val d = Player("D", List.empty)
      val game = createGameState(
        players = List(a, d),
        gamePhase = DefensePhase,
        defenderIndex = 1,
        lastEvent = Some(GameEvent.Defend(diamondTen))
      )
      val controller = new Controller(game)
      val tui = new TUI(controller)
      tui.buildStatusString(game).should(include ("Verteidigung mit Ten Diamonds."))
    }

    "buildStatusString - Take shows who takes" in {
      val p = Player("P", List.empty)
      val game = createGameState(
        players = List(p),
        gamePhase = DrawPhase, // After taking cards, it's typically DrawPhase
        lastEvent = Some(GameEvent.Take)
      )
      val controller = new Controller(game)
      val tui = new TUI(controller)
      tui.buildStatusString(game).should(include ("Karten aufgenommen."))
    }

    "buildStatusString - Pass shows who passed" in {
      val p = Player("P", List.empty)
      val game = createGameState(
        players = List(p),
        gamePhase = DrawPhase, // After passing, it's typically DrawPhase
        lastEvent = Some(GameEvent.Pass)
      )
      val controller = new Controller(game)
      val tui = new TUI(controller)
      tui.buildStatusString(game).should(include ("Passen."))
    }

    "buildStatusString - InvalidMove" in {
      val game = createGameState(players = List.empty, lastEvent = Some(GameEvent.InvalidMove))
      val controller = new Controller(game)
      val tui = new TUI(controller)
      tui.buildStatusString(game).should(include("Ungültiger Zug!"))
    }

    "buildStatusString - GameOver with loser" in {
      val donePlayer = Player("Done", List.empty, isDone = true)
      val loser = Player("Loser", List(Card(Suit.Clubs, Rank.Six)), isDone = false)
      val game = createGameState(
        players = List(donePlayer, loser),
        gamePhase = EndPhase,
        lastEvent = Some(GameEvent.GameOver(donePlayer, Some(loser))) // Provide actual players for GameOver
      )
      val controller = new Controller(game)
      val tui = new TUI(controller)
      tui.buildStatusString(game).should(include ("Spiel beendet! Loser ist der Durak!"))
    }

    "buildStatusString - GameOver draw (no loser)" in {
      val p1 = Player("P1", List.empty, isDone = true)
      val p2 = Player("P2", List.empty, isDone = true)
      val game = createGameState(
        players = List(p1, p2),
        gamePhase = EndPhase,
        lastEvent = Some(GameEvent.GameOver(p1, None)) // No loser
      )
      val controller = new Controller(game)
      val tui = new TUI(controller)
      tui.buildStatusString(game).shouldBe("Spiel beendet! Es gibt keinen Durak (Unentschieden oder alle gewonnen)!")
    }

    "buildStatusString - Quit (simulated by GameOver)" in {
      val game = createGameState(players = List.empty, gamePhase = EndPhase, lastEvent = Some(GameEvent.GameOver(Player("Quit", List.empty), None)))
      val controller = new Controller(game)
      val tui = new TUI(controller)
      tui.buildStatusString(game).shouldBe("Spiel beendet.")
    }

    "ask for deck size and use default" in {
      val controller = new Controller(createGameState(List.empty)) // Controller needs initial state
      val tui = new TUI(controller)
      val input = "\n" // Empty input means default
      val inStream = new ByteArrayInputStream(input.getBytes)
      Console.withIn(inStream) {
        val result = tui.askForDeckSize()
        result.shouldBe(36)
      }
    }

    "askForDeckSize uses provided value" in {
      val controller = new Controller(createGameState(List.empty))
      val tui = new TUI(controller)
      val input = "52\n"
      val inStream = new ByteArrayInputStream(input.getBytes)
      Console.withIn(inStream) {
        val result = tui.askForDeckSize()
        result.shouldBe(52)
      }
    }

    "ask for player count and use default" in {
      val controller = new Controller(createGameState(List.empty))
      val tui = new TUI(controller)
      val input = "\n"
      val inStream = new ByteArrayInputStream(input.getBytes)
      Console.withIn(inStream) {
        val result = tui.askForPlayerCount()
        result.shouldBe(2)
      }
    }

    "askForPlayerCount uses provided value" in {
      val controller = new Controller(createGameState(List.empty))
      val tui = new TUI(controller)
      val input = "3\n"
      val inStream = new ByteArrayInputStream(input.getBytes)
      Console.withIn(inStream) {
        val result = tui.askForPlayerCount()
        result.shouldBe(3)
      }
    }

    "ask for player count and handle minimum" in {
      val controller = new Controller(createGameState(List.empty))
      val tui = new TUI(controller)
      val input = "1\n"
      val inStream = new ByteArrayInputStream(input.getBytes)
      Console.withIn(inStream) {
        val result = tui.askForPlayerCount()
        result.shouldBe(2)
      }
    }

    "ask for player names and use provided names" in {
      val controller = new Controller(createGameState(List.empty))
      val tui = new TUI(controller)
      val input = "Alice\nBob\n"
      val inStream = new ByteArrayInputStream(input.getBytes)
      Console.withIn(inStream) {
        val names = tui.askForPlayerNames(2)
        names.shouldBe(List("Alice", "Bob"))
      }
    }

    "ask for player names and use default names for empty input" in {
      val controller = new Controller(createGameState(List.empty))
      val tui = new TUI(controller)
      val input = "\n\n"
      val inStream = new ByteArrayInputStream(input.getBytes)
      Console.withIn(inStream) {
        val names = tui.askForPlayerNames(2)
        names.shouldBe(List("Player1", "Player2"))
      }
    }

    "clearScreen returns ANSI escape sequence" in {
      val controller = new Controller(createGameState(List.empty))
      val tui = new TUI(controller)
      tui.clearScreen().shouldBe("\u001b[2J\u001b[H")
    }

    "update method should print to console" in {
      val controller = new Controller(createGameState(List.empty, trumpCard = defaultTrumpCard))
      val tui = new TUI(controller)
      controller.add(tui)

      val stream = new ByteArrayOutputStream()
      Console.withOut(stream) {
        controller.notifyObservers
      }
      val output = stream.toString()
      output.should(include ("Status: Willkommen bei Durak!"))
      output.should(include (defaultTrumpCard.suit.toString))
    }
  }
}
