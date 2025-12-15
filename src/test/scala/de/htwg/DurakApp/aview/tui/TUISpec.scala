package de.htwg.DurakApp.aview.tui

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.ModelInterface.*
import de.htwg.DurakApp.controller.ControllerInterface.*
import de.htwg.DurakApp.aview.ViewInterface.*
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import de.htwg.DurakApp.util.{Observable, UndoRedoManager}

class TUISpec extends AnyWordSpec with Matchers {

  val GREEN = "\u001b[32m"
  val RESET = "\u001b[0m"

  val defaultTrumpCard: Card = Card(Suit.Clubs, Rank.Six, isTrump = true)
  val defaultTable: Map[Card, Option[Card]] = Map.empty
  val defaultDiscardPile: List[Card] = List.empty
  val defaultAttackerIndex: Int = 0
  val defaultDefenderIndex: Int = 1
  val defaultGamePhase: GamePhase = SetupPhase
  val defaultLastEvent: Option[GameEvent] = None
  val defaultPassedPlayers: Set[Int] = Set.empty
  val defaultRoundWinner: Option[Int] = None
  val defaultSetupPlayerCount: Option[Int] = None
  val defaultSetupPlayerNames: List[String] = List.empty
  val defaultSetupDeckSize: Option[Int] = None

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
      setupPlayerCount: Option[Int] = defaultSetupPlayerCount,
      setupPlayerNames: List[String] = defaultSetupPlayerNames,
      setupDeckSize: Option[Int] = defaultSetupDeckSize
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

  val heartAce = Card(Suit.Hearts, Rank.Ace, isTrump = false)
  val spadeSix = Card(Suit.Spades, Rank.Six, isTrump = false)
  val diamondTen = Card(Suit.Diamonds, Rank.Ten, isTrump = false)
  val clubKing = Card(Suit.Clubs, Rank.King, isTrump = false)
  val spadeTen = Card(Suit.Spades, Rank.Ten, isTrump = false)

  "A TUI" should {

    "buildStatusString - AttackPhase shows attacker name" in {
      val attacker = Player("Angreifer", List.empty)
      val defender = Player("Verteidiger", List.empty)
      val game = createGameState(
        players = List(attacker, defender),
        gamePhase = AttackPhase,
        attackerIndex = 0,
        lastEvent = Some(GameEvent.Attack(spadeSix))
      )
      val controller =
        new Controller(game, UndoRedoManager())
      val tui = new TUI(controller)
      tui.buildStatusString(game).should(include("Angriff mit Six Spades."))
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
      val controller =
        new Controller(game, UndoRedoManager())
      val tui = new TUI(controller)
      tui
        .buildStatusString(game)
        .should(include("Verteidigung mit Ten Diamonds."))
    }

    "buildStatusString - Take shows who takes" in {
      val p = Player("P", List.empty)
      val game = createGameState(
        players = List(p),
        gamePhase = DrawPhase,
        lastEvent = Some(GameEvent.Take)
      )
      val controller =
        new Controller(game, UndoRedoManager())
      val tui = new TUI(controller)
      tui.buildStatusString(game).should(include("Karten aufgenommen."))
    }

    "buildStatusString - Pass shows who passed" in {
      val p = Player("P", List.empty)
      val game = createGameState(
        players = List(p),
        gamePhase = DrawPhase,
        lastEvent = Some(GameEvent.Pass)
      )
      val controller =
        new Controller(game, UndoRedoManager())
      val tui = new TUI(controller)
      tui.buildStatusString(game).should(include("Passen."))
    }

    "buildStatusString - InvalidMove" in {
      val game = createGameState(
        players = List.empty,
        lastEvent = Some(GameEvent.InvalidMove)
      )
      val controller =
        new Controller(game, UndoRedoManager())
      val tui = new TUI(controller)
      tui.buildStatusString(game).should(include("Ungültiger Zug!"))
    }

    "buildStatusString - GameOver with loser" in {
      val donePlayer = Player("Done", List.empty, isDone = true)
      val loser =
        Player("Loser", List(Card(Suit.Clubs, Rank.Six)), isDone = false)
      val game = createGameState(
        players = List(donePlayer, loser),
        gamePhase = EndPhase,
        lastEvent = Some(GameEvent.GameOver(donePlayer, Some(loser)))
      )
      val controller =
        new Controller(game, UndoRedoManager())
      val tui = new TUI(controller)
      tui
        .buildStatusString(game)
        .should(include("Spiel beendet! Loser ist der Durak!"))
    }

    "buildStatusString - GameOver draw (no loser)" in {
      val p1 = Player("P1", List.empty, isDone = true)
      val p2 = Player("P2", List.empty, isDone = true)
      val game = createGameState(
        players = List(p1, p2),
        gamePhase = EndPhase,
        lastEvent = Some(GameEvent.GameOver(p1, None))
      )
      val controller =
        new Controller(game, UndoRedoManager())
      val tui = new TUI(controller)
      tui
        .buildStatusString(game)
        .shouldBe(
          "Spiel beendet! Es gibt keinen Durak (Unentschieden oder alle gewonnen)!"
        )
    }

    "buildStatusString - Quit (simulated by GameOver)" in {
      val game = createGameState(
        players = List.empty,
        gamePhase = EndPhase,
        lastEvent = Some(GameEvent.GameOver(Player("Quit", List.empty), None))
      )
      val controller =
        new Controller(game, UndoRedoManager())
      val tui = new TUI(controller)
      tui.buildStatusString(game).shouldBe("Spiel beendet.")
    }

    "buildStatusString - CannotUndo" in {
      val game = createGameState(
        players = List.empty,
        lastEvent = Some(GameEvent.CannotUndo)
      )
      val controller =
        new Controller(game, UndoRedoManager())
      val tui = new TUI(controller)
      tui
        .buildStatusString(game)
        .should(include("Nichts zum Rückgängigmachen!"))
    }

    "buildStatusString - CannotRedo" in {
      val game = createGameState(
        players = List.empty,
        lastEvent = Some(GameEvent.CannotRedo)
      )
      val controller =
        new Controller(game, UndoRedoManager())
      val tui = new TUI(controller)
      tui
        .buildStatusString(game)
        .should(include("Nichts zum Wiederherstellen!"))
    }

    "clearScreen returns ANSI escape sequence" in {
      val controller = new Controller(
        createGameState(List.empty),
        UndoRedoManager()
      )
      val tui = new TUI(controller)
      tui.clearScreen().shouldBe("\u001b[2J\u001b[H")
    }

    "update method should print to console with active player highlighted" in {
      val player = Player("TestPlayer", List(heartAce))
      val game = createGameState(
        players = List(player),
        trumpCard = defaultTrumpCard,
        gamePhase = AttackPhase,
        attackerIndex = 0
      )
      val controller =
        new Controller(game, UndoRedoManager())
      val tui = new TUI(controller)
      controller.add(tui)

      val stream = new ByteArrayOutputStream()
      Console.withOut(stream) {
        controller.notifyObservers
      }
      val output = stream.toString()
      output should include("Status: AttackPhase")
      output should include(defaultTrumpCard.suit.toString)
      output should include(s"$GREEN${player.name}$RESET (Karten: 1)")
    }

    "renderCard" should {
      "contain rank and suit for a red card" in {
        val controller = new Controller(
          createGameState(List.empty),
          UndoRedoManager()
        )
        val tui = new TUI(controller)
        val card = Card(Suit.Hearts, Rank.Ace)
        val renderedCard = tui.renderCard(card).mkString
        renderedCard should include("A")
        renderedCard should include("\u2665")
      }

      "contain rank and suit for a black card" in {
        val controller = new Controller(
          createGameState(List.empty),
          UndoRedoManager()
        )
        val tui = new TUI(controller)
        val card = Card(Suit.Spades, Rank.King)
        val renderedCard = tui.renderCard(card).mkString
        renderedCard should include("K")
        renderedCard should include("\u2660")
      }
    }

    "renderHandWithIndices" should {
      "return 'Leere Hand' for an empty hand" in {
        val controller = new Controller(
          createGameState(List.empty),
          UndoRedoManager()
        )
        val tui = new TUI(controller)
        tui.renderHandWithIndices(List.empty) should be("Leere Hand")
      }

      "render a single card with its index" in {
        val controller = new Controller(
          createGameState(List.empty),
          UndoRedoManager()
        )
        val tui = new TUI(controller)
        val hand = List(Card(Suit.Diamonds, Rank.Ten))
        val renderedHand = tui.renderHandWithIndices(hand)
        renderedHand should include("10")
        renderedHand should include("\u2666")
        renderedHand should endWith("\n   0   ")
      }

      "render multiple cards with their indices" in {
        val controller = new Controller(
          createGameState(List.empty),
          UndoRedoManager()
        )
        val tui = new TUI(controller)
        val hand =
          List(Card(Suit.Diamonds, Rank.Ten), Card(Suit.Clubs, Rank.Jack))
        val renderedHand = tui.renderHandWithIndices(hand)

        renderedHand should include("10")
        renderedHand should include("\u2666")
        renderedHand should include("J")
        renderedHand should include("\u2663")

        renderedHand should include regex "\\n\\s*0\\s+1\\s*"
      }
    }

    "show the correct prompt for the attacker in AttackPhase" in {
      val attacker = Player("Alice", List(spadeSix))
      val defender = Player("Bob", List(heartAce))
      val game = createGameState(
        players = List(attacker, defender),
        gamePhase = AttackPhase,
        attackerIndex = 0
      )
      val controller =
        new Controller(game, UndoRedoManager())
      val tui = new TUI(controller)
      val input = "q\n"
      val inStream = new ByteArrayInputStream(input.getBytes)
      val outStream = new ByteArrayOutputStream()

      Console.withIn(inStream) {
        Console.withOut(outStream) {
          tui.run()
        }
      }

      val output = outStream.toString()
      output should include(
        s"$GREEN${attacker.name}$RESET, dein Zug ('play index', 'pass', 'u', 'r'):"
      )
    }

    "show the correct prompt for the defender in DefensePhase" in {
      val attacker = Player("Alice", List(spadeSix))
      val defender = Player("Bob", List(heartAce))
      val game = createGameState(
        players = List(attacker, defender),
        gamePhase = DefensePhase,
        attackerIndex = 0,
        defenderIndex = 1
      )
      val controller =
        new Controller(game, UndoRedoManager())
      val tui = new TUI(controller)
      val input = "q\n"
      val inStream = new ByteArrayInputStream(input.getBytes)
      val outStream = new ByteArrayOutputStream()

      Console.withIn(inStream) {
        Console.withOut(outStream) {
          tui.run()
        }
      }

      val output = outStream.toString()
      output should include(
        s"$GREEN${defender.name}$RESET, dein Zug ('play index', 'take', 'u', 'r'):"
      )
    }

    "parseTuiInput (Chain of Responsibility)" should {
      val attacker = Player("Alice", List(spadeSix))
      val defender = Player("Bob", List(heartAce))
      val gameAttackPhase = createGameState(
        players = List(attacker, defender),
        gamePhase = AttackPhase,
        attackerIndex = 0,
        defenderIndex = 1
      )
      val gameDefensePhase = gameAttackPhase.copy(gamePhase = DefensePhase)
      val controller =
        new Controller(gameAttackPhase, UndoRedoManager())
      val tui = new TUI(controller)

      "handle 'play 0' during AttackPhase" in {
        val action = tui.inputHandler.handleRequest("play 0", gameAttackPhase)
        action should be(de.htwg.DurakApp.controller.PlayCardAction(spadeSix))
      }

      "handle 'play 0' during DefensePhase" in {
        val action = tui.inputHandler.handleRequest("play 0", gameDefensePhase)
        action should be(de.htwg.DurakApp.controller.PlayCardAction(heartAce))
      }

      "handle 'pass'" in {
        val action = tui.inputHandler.handleRequest("pass", gameAttackPhase)
        action should be(de.htwg.DurakApp.controller.PassAction)
      }

      "handle 'take'" in {
        val action = tui.inputHandler.handleRequest("take", gameDefensePhase)
        action should be(de.htwg.DurakApp.controller.TakeCardsAction)
      }

      "handle 'undo'" in {
        val action = tui.inputHandler.handleRequest("undo", gameAttackPhase)
        action should be(de.htwg.DurakApp.controller.UndoAction)
      }

      "handle 'z' as undo" in {
        val action = tui.inputHandler.handleRequest("z", gameAttackPhase)
        action should be(de.htwg.DurakApp.controller.UndoAction)
      }

      "handle 'redo'" in {
        val action = tui.inputHandler.handleRequest("redo", gameAttackPhase)
        action should be(de.htwg.DurakApp.controller.RedoAction)
      }

      "handle 'y' as redo" in {
        val action = tui.inputHandler.handleRequest("y", gameAttackPhase)
        action should be(de.htwg.DurakApp.controller.RedoAction)
      }

      "handle invalid command" in {
        val action = tui.inputHandler.handleRequest("foo", gameAttackPhase)
        action should be(de.htwg.DurakApp.controller.InvalidAction)
      }

      "handle 'play' without index" in {
        val action = tui.inputHandler.handleRequest("play", gameAttackPhase)
        action should be(de.htwg.DurakApp.controller.InvalidAction)
      }

      "handle 'play' with non-numeric index" in {
        val action = tui.inputHandler.handleRequest("play foo", gameAttackPhase)
        action should be(de.htwg.DurakApp.controller.InvalidAction)
      }

      "handle 'play' with out-of-bounds index" in {
        val action = tui.inputHandler.handleRequest("play 5", gameAttackPhase)
        action should be(de.htwg.DurakApp.controller.InvalidAction)
      }

      "handle case-insensitivity and whitespace" in {
        val action =
          tui.inputHandler.handleRequest("  PLaY 0  ", gameAttackPhase)
        action should be(de.htwg.DurakApp.controller.PlayCardAction(spadeSix))
      }

      "handle non-numeric input in SetupPhase resulting in InvalidAction" in {
        val initialGame =
          createGameState(players = List.empty, gamePhase = SetupPhase)
        val controller = new Controller(initialGame, UndoRedoManager())
        val tui = new TUI(controller)

        val action =
          tui.inputHandler.handleRequest("not_a_number", controller.gameState)
        action should be(de.htwg.DurakApp.controller.InvalidAction)
      }

      "handle non-numeric input in AskDeckSizePhase resulting in InvalidAction" in {
        val initialGame =
          createGameState(players = List.empty, gamePhase = AskDeckSizePhase)
        val controller = new Controller(initialGame, UndoRedoManager())
        val tui = new TUI(controller)

        val action =
          tui.inputHandler.handleRequest("not_a_number", controller.gameState)
        action should be(de.htwg.DurakApp.controller.InvalidAction)
      }

      "handle non-numeric input in AskPlayerCountPhase resulting in InvalidAction" in {
        val initialGame =
          createGameState(players = List.empty, gamePhase = AskPlayerCountPhase)
        val controller = new Controller(initialGame, UndoRedoManager())
        val tui = new TUI(controller)

        val action =
          tui.inputHandler.handleRequest("not_a_number", controller.gameState)
        action should be(de.htwg.DurakApp.controller.InvalidAction)
      }
    }

    "terminate on 'quit' input" in {
      val game = createGameState(
        players = List(Player("Alice", List.empty)),
        gamePhase = AttackPhase,
        attackerIndex = 0
      )
      val controller =
        new Controller(game, UndoRedoManager())
      val tui = new TUI(controller)
      val input = "quit\n"
      val inStream = new ByteArrayInputStream(input.getBytes)
      val outStream = new ByteArrayOutputStream()

      Console.withIn(inStream) {
        Console.withOut(outStream) {
          tui.run()
        }
      }

      val output = outStream.toString()
      output should include("Spiel beendet.")
    }

    "gameLoop terminates when GameEvent.ExitApplication is set in AskPlayAgainPhase" in {
      val game = createGameState(
        players = List(Player("Alice", List.empty)),
        gamePhase = AskPlayAgainPhase
      )
      val controller = new Controller(game, UndoRedoManager())
      val tui = new TUI(controller)

      val input = "no\n"
      val inStream = new ByteArrayInputStream(input.getBytes)
      val outStream = new ByteArrayOutputStream()

      Console.withIn(inStream) {
        Console.withOut(outStream) {
          tui.run()
        }
      }

      val output = outStream.toString()
      output should include("Spiel beendet.")
    }

    "print default prompt" in {
      val attacker = Player("RoundAttacker", List(spadeSix))
      val defender = Player("RoundDefender", List(heartAce))
      val game = createGameState(
        players = List(attacker, defender),
        gamePhase = RoundPhase,
        attackerIndex = 0
      )
      val controller =
        new Controller(game, UndoRedoManager())
      val tui = new TUI(controller)
      val input = "q\n"
      val inStream = new ByteArrayInputStream(input.getBytes)
      val outStream = new ByteArrayOutputStream()

      Console.withIn(inStream) {
        Console.withOut(outStream) {
          tui.run()
        }
      }

      val output = outStream.toString()
      output should include(
        s">"
      )
    }

    "renderCard includes Nine and Queen correctly" in {
      val controller = new Controller(
        createGameState(List.empty),
        UndoRedoManager()
      )
      val tui = new TUI(controller)

      val nineCard = Card(Suit.Clubs, Rank.Nine)
      val queenCard = Card(Suit.Hearts, Rank.Queen)

      val rNine = tui.renderCard(nineCard).mkString
      rNine should include("9")
      rNine should include("\u2663")

      val rQueen = tui.renderCard(queenCard).mkString
      rQueen should include("Q")
      rQueen should include("\u2665")
    }

    "renderTable shows defending cards (calls combineCardLines for defenses)" in {
      val attack = Card(Suit.Spades, Rank.Six)
      val defend = Card(Suit.Diamonds, Rank.Ten)
      val pA = Player("A", List(attack))
      val pD = Player("D", List(defend))
      val game = createGameState(
        players = List(pA, pD),
        table = Map(attack -> Some(defend)),
        gamePhase = AttackPhase
      )
      val controller =
        new Controller(game, UndoRedoManager())
      val tui = new TUI(controller)

      val tableStr = tui.renderTable(game)
      tableStr should include("Angriff (1)")
      tableStr should include("Verteidigung (1)")
      tableStr should include(
        "\u2666"
      )
      tableStr should include("10")
    }

    "buildStatusString - NotYourTurn, Draw and RoundEnd cases" in {
      val controller = new Controller(
        createGameState(List.empty),
        UndoRedoManager()
      )
      val tui = new TUI(controller)

      val g1 = createGameState(
        players = List(Player("P", List.empty)),
        lastEvent = Some(GameEvent.NotYourTurn)
      )
      tui.buildStatusString(g1).should(include("Du bist nicht am Zug!"))

      val g2 = createGameState(
        players = List(Player("P", List.empty)),
        lastEvent = Some(GameEvent.Draw)
      )
      tui.buildStatusString(g2).should(include("Karten werden gezogen."))

      val g3 = createGameState(
        players = List(Player("P", List.empty)),
        lastEvent = Some(GameEvent.RoundEnd(cleared = true))
      )
      tui.buildStatusString(g3).should(include("Runde vorbei, Tisch geleert."))

      val g4 = createGameState(
        players = List(Player("P", List.empty)),
        lastEvent = Some(GameEvent.RoundEnd(cleared = false))
      )
      tui
        .buildStatusString(g4)
        .should(include("Runde vorbei, Karten aufgenommen."))
    }

    "combineCardLines returns empty string for empty list" in {
      val controller = new Controller(
        createGameState(List.empty),
        UndoRedoManager()
      )
      val tui = new TUI(controller)

      tui.combineCardLines(List.empty) shouldBe ""
    }

    "renderCard includes Seven and Eight correctly" in {
      val controller = new Controller(
        createGameState(List.empty),
        UndoRedoManager()
      )
      val tui = new TUI(controller)

      val seven = Card(Suit.Diamonds, Rank.Seven)
      val eight = Card(Suit.Spades, Rank.Eight)

      val rSeven = tui.renderCard(seven).mkString
      rSeven should include("7")
      rSeven should include("\u2666")

      val rEight = tui.renderCard(eight).mkString
      rEight should include("8")
      rEight should include("\u2660")
    }

    "gameLoop continues when no GameOver (case _ => gameLoop()) and handles Undo/Redo without calling processPlayerAction" in {
      val attacker = Player("A", List(spadeSix))
      val defender = Player("D", List(heartAce))
      val initial = createGameState(
        players = List(attacker, defender),
        gamePhase = AttackPhase
      )

      class TestController(
          gs: GameState,
          undoManager: UndoRedoManager
      ) extends Controller(gs, undoManager) {
        val calls = new java.util.concurrent.atomic.AtomicInteger(0)
        override def processPlayerAction(
            action: de.htwg.DurakApp.controller.PlayerAction
        ): de.htwg.DurakApp.model.GameState = {
          calls.incrementAndGet()
          this.gameState = this.gameState.copy(lastEvent = Some(GameEvent.Pass))
          this.gameState
        }
      }

      val controller =
        new TestController(initial, UndoRedoManager())

      val tui = new TUI(controller)

      val input =
        "pass\nundo\nq\n"

      val inStream = new ByteArrayInputStream(input.getBytes)

      val outStream = new ByteArrayOutputStream()

      Console.withIn(inStream) {

        Console.withOut(outStream) {

          tui.run()

        }

      }

      val output = outStream.toString()

      controller.calls.get() shouldBe 1
      output should include("Spiel beendet.")
    }

    "parseTuiInput handles SetPlayerCountAction" in {
      val initialGame =
        createGameState(players = List.empty, gamePhase = SetupPhase)
      val controller = new Controller(initialGame, UndoRedoManager())
      val tui = new TUI(controller)

      val action = tui.inputHandler.handleRequest("2", controller.gameState)
      controller.processPlayerAction(action)
      controller.gameState.setupPlayerCount should be(Some(2))
      controller.gameState.gamePhase should be(AskPlayerNamesPhase)
      controller.gameState.lastEvent should be(Some(GameEvent.AskPlayerNames))
    }

    "parseTuiInput handles AddPlayerNameAction" in {
      val gameAfterPlayerCount = createGameState(
        players = List.empty,
        gamePhase = AskPlayerNamesPhase,
        setupPlayerCount = Some(2)
      )
      val controller = new Controller(gameAfterPlayerCount, UndoRedoManager())
      val tui = new TUI(controller)

      val action = tui.inputHandler.handleRequest("Alice", controller.gameState)
      controller.processPlayerAction(action)
      controller.gameState.setupPlayerNames should be(List("Alice"))
      controller.gameState.lastEvent should be(Some(GameEvent.AskPlayerNames))

      val action2 = tui.inputHandler.handleRequest("Bob", controller.gameState)
      controller.processPlayerAction(action2)
      controller.gameState.setupPlayerNames should be(List("Alice", "Bob"))
      controller.gameState.gamePhase should be(AskDeckSizePhase)
      controller.gameState.lastEvent should be(Some(GameEvent.AskDeckSize))
    }

    "parseTuiInput handles SetDeckSizeAction" in {
      val gameAfterPlayerNames = createGameState(
        players = List.empty,
        gamePhase = AskDeckSizePhase,
        setupPlayerCount = Some(2),
        setupPlayerNames = List("Alice", "Bob")
      )
      val controller = new Controller(gameAfterPlayerNames, UndoRedoManager())
      val tui = new TUI(controller)

      val action = tui.inputHandler.handleRequest("36", controller.gameState)
      controller.processPlayerAction(action)
      controller.gameState.setupDeckSize should be(Some(36))
      controller.gameState.gamePhase should be(AttackPhase)
      controller.gameState.players.head.hand.size should be > 0
      controller.gameState.deck.size should be > 0
    }

    "parseTuiInput handles invalid player count" in {
      val initialGame =
        createGameState(players = List.empty, gamePhase = SetupPhase)
      val controller = new Controller(initialGame, UndoRedoManager())
      val tui = new TUI(controller)

      val action = tui.inputHandler.handleRequest("1", controller.gameState)
      controller.processPlayerAction(action)
      controller.gameState.setupPlayerCount should be(None)
      controller.gameState.gamePhase should be(SetupPhase)
      controller.gameState.lastEvent should be(Some(GameEvent.SetupError))
    }

    "parseTuiInput handles invalid deck size" in {
      val gameAfterPlayerNames = createGameState(
        players = List.empty,
        gamePhase = AskDeckSizePhase,
        setupPlayerCount = Some(2),
        setupPlayerNames = List("Alice", "Bob")
      )
      val controller = new Controller(gameAfterPlayerNames, UndoRedoManager())
      val tui = new TUI(controller)

      val action = tui.inputHandler.handleRequest("37", controller.gameState)
      controller.processPlayerAction(action)
      controller.gameState.setupDeckSize should be(None)
      controller.gameState.gamePhase should be(AskDeckSizePhase)
      controller.gameState.lastEvent should be(Some(GameEvent.SetupError))
    }

    "parseTuiInput handles ExitGameAction in AskPlayAgainPhase" in {
      val game = createGameState(
        players = List.empty,
        gamePhase = AskPlayAgainPhase
      )
      val controller = new Controller(game, UndoRedoManager())
      val tui = new TUI(controller)

      val action = tui.inputHandler.handleRequest("no", game)
      action should be(de.htwg.DurakApp.controller.ExitGameAction)
    }

    "parseTuiInput handles PlayAgainAction in AskPlayAgainPhase" in {
      val game = createGameState(
        players = List.empty,
        gamePhase = AskPlayAgainPhase
      )
      val controller = new Controller(game, UndoRedoManager())
      val tui = new TUI(controller)

      val action = tui.inputHandler.handleRequest("yes", game)
      action should be(de.htwg.DurakApp.controller.PlayAgainAction)
    }

    "handle invalid input in AskPlayAgainPhase resulting in InvalidAction" in {
      val game = createGameState(
        players = List.empty,
        gamePhase = AskPlayAgainPhase
      )
      val controller = new Controller(game, UndoRedoManager())
      val tui = new TUI(controller)

      val action = tui.inputHandler.handleRequest("maybe", game)
      action should be(de.htwg.DurakApp.controller.InvalidAction)
    }
  }

  "update method should print correct prompt for AskDeckSizePhase" in {
    val game = createGameState(
      players = List.empty,
      gamePhase = AskDeckSizePhase,
      lastEvent = None
    ).copy(setupPlayerNames = List("Alice", "Bob"))
    val controller = new Controller(game, UndoRedoManager())
    val tui = new TUI(controller)
    controller.add(tui)

    val stream = new ByteArrayOutputStream()
    Console.withOut(stream) {
      controller.notifyObservers
    }
    val output = stream.toString()
    output should include("Deckgröße eingeben (2-36):")
  }

  "TUI buildStatusString specific cases" should {
    val RED = "\u001b[31m"
    val GREEN = "\u001b[32m"
    val RESET = "\u001b[0m"

    "buildStatusString - GameEvent.SetupError path" in {
      val game = createGameState(
        players = List.empty,
        lastEvent = Some(GameEvent.SetupError),
        gamePhase = SetupPhase
      )
      val controller = new Controller(game, UndoRedoManager())
      val tui = new TUI(controller)
      tui.buildStatusString(
        game
      ) shouldBe s"${RED}Setup-Fehler: Spieleranzahl eingeben (2-6):$RESET"
    }

    "buildStatusString - GameEvent.GameSetupComplete path" in {
      val game = createGameState(
        players = List.empty,
        lastEvent = Some(GameEvent.GameSetupComplete)
      )
      val controller = new Controller(game, UndoRedoManager())
      val tui = new TUI(controller)
      tui.buildStatusString(game) should include(
        s"${GREEN}Setup abgeschlossen! Starte Spiel...$RESET"
      )
    }

    "buildStatusString - GameEvent.AskPlayAgain path" in {
      val game = createGameState(
        players = List.empty,
        lastEvent = Some(GameEvent.AskPlayAgain)
      )
      val controller = new Controller(game, UndoRedoManager())
      val tui = new TUI(controller)
      tui.buildStatusString(game) should include(
        s"${GREEN}Möchten Sie eine neue Runde spielen? (yes/no)$RESET"
      )
    }

    "buildStatusString - GameEvent.ExitApplication path" in {
      val game = createGameState(
        players = List.empty,
        lastEvent = Some(GameEvent.ExitApplication)
      )
      val controller = new Controller(game, UndoRedoManager())
      val tui = new TUI(controller)
      tui.buildStatusString(game) should include(
        s"${GREEN}Anwendung wird beendet...$RESET"
      )
    }

    "buildStatusString - GameEvent.AskPlayerCount path returns empty" in {
      val game = createGameState(
        players = List.empty,
        lastEvent = Some(GameEvent.AskPlayerCount)
      )
      val controller = new Controller(game, UndoRedoManager())
      val tui = new TUI(controller)
      tui.buildStatusString(game) shouldBe ""
    }

    "buildStatusString - GameEvent.AskPlayerNames path returns empty" in {
      val game = createGameState(
        players = List.empty,
        lastEvent = Some(GameEvent.AskPlayerNames)
      )
      val controller = new Controller(game, UndoRedoManager())
      val tui = new TUI(controller)
      tui.buildStatusString(game) shouldBe ""
    }

    "buildStatusString - GameEvent.AskDeckSize path returns empty" in {
      val game = createGameState(
        players = List.empty,
        lastEvent = Some(GameEvent.AskDeckSize)
      )
      val controller = new Controller(game, UndoRedoManager())
      val tui = new TUI(controller)
      tui.buildStatusString(game) shouldBe ""
    }

    "buildStatusString - empty string for setup phases when lastEvent is None" in {
      val phases = List(
        SetupPhase,
        AskPlayerCountPhase,
        AskPlayerNamesPhase,
        AskDeckSizePhase
      )
      phases.foreach { phase =>
        val game = createGameState(
          players = List.empty,
          gamePhase = phase,
          lastEvent = None
        )
        val controller = new Controller(game, UndoRedoManager())
        val tui = new TUI(controller)
        tui.buildStatusString(game) shouldBe ""
      }
    }
  }

  "TUI description(game) method" should {
    "return correct string for SetupPhase" in {
      val game = createGameState(players = List.empty, gamePhase = SetupPhase)
      val controller = new Controller(game, UndoRedoManager())
      val tui = new TUI(controller)
      tui.description(game) shouldBe "Spieleranzahl eingeben (2-6):"
    }

    "return correct string for AskPlayerCountPhase" in {
      val game =
        createGameState(players = List.empty, gamePhase = AskPlayerCountPhase)
      val controller = new Controller(game, UndoRedoManager())
      val tui = new TUI(controller)
      tui.description(game) shouldBe "Spieleranzahl eingeben (2-6):"
    }

    "return correct string for AskPlayerNamesPhase" in {
      val game = createGameState(
        players = List.empty,
        gamePhase = AskPlayerNamesPhase,
        setupPlayerNames = List("Alice")
      )
      val controller = new Controller(game, UndoRedoManager())
      val tui = new TUI(controller)
      tui.description(game) shouldBe "Spielername 2:"
    }

    "return correct string for AskDeckSizePhase" in {
      val game =
        createGameState(players = List.empty, gamePhase = AskDeckSizePhase)
          .copy(setupPlayerNames = List("Alice", "Bob"))
      val controller = new Controller(game, UndoRedoManager())
      val tui = new TUI(controller)
      tui.description(game) shouldBe "Deckgröße eingeben (2-36):"
    }

    "return correct string for AskPlayAgainPhase" in {
      val game =
        createGameState(players = List.empty, gamePhase = AskPlayAgainPhase)
      val controller = new Controller(game, UndoRedoManager())
      val tui = new TUI(controller)
      tui.description(
        game
      ) shouldBe "Möchten Sie eine neue Runde spielen? (yes/no):"
    }

    "return gamePhase.toString for other phases" in {
      val game = createGameState(players = List.empty, gamePhase = AttackPhase)
      val controller = new Controller(game, UndoRedoManager())
      val tui = new TUI(controller)
      tui.description(game) shouldBe "AttackPhase"
    }

    "cover printPrompt corner cases" in {
      val player = Player("TestPlayer", List(heartAce))
      val game = createGameState(
        players = List(player),
        gamePhase = DrawPhase
      )
      val controller = new Controller(game, UndoRedoManager())
      val tui = new TUI(controller)

      val stream = new ByteArrayOutputStream()
      Console.withOut(stream) {
        tui.update
      }

      val output = stream.toString()

      output should include("Error: No active player. DrawPhase")
    }
  }
}
