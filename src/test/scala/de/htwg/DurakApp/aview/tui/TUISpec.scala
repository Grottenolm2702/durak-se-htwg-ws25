package de.htwg.DurakApp.aview.tui

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.controller.Controller
import de.htwg.DurakApp.model._
import de.htwg.DurakApp.model.state._
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import de.htwg.DurakApp.aview.tui.TUI
import de.htwg.DurakApp.util.Observable

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
      roundWinner
    )
  }

  val heartAce = Card(Suit.Hearts, Rank.Ace, isTrump = false)
  val spadeSix = Card(Suit.Spades, Rank.Six, isTrump = false)
  val diamondTen = Card(Suit.Diamonds, Rank.Ten, isTrump = false)
  val clubKing = Card(Suit.Clubs, Rank.King, isTrump = false)
  val spadeTen = Card(Suit.Spades, Rank.Ten, isTrump = false)

  "A TUI" should {

    "buildStatusString - SetupPhase (Welcome)" in {
      val game = createGameState(
        players = List.empty,
        gamePhase = SetupPhase,
        lastEvent = None
      )
      val controller = new Controller(game)
      val tui = new TUI(controller)
      tui.buildStatusString(game).shouldBe("Willkommen bei Durak!")
    }

    "buildStatusString - SetupPhase (Player Setup)" in {
      val game = createGameState(
        players = List(Player("TestPlayer", List.empty)),
        gamePhase = SetupPhase,
        lastEvent = None
      )
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
      val controller = new Controller(game)
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
      val controller = new Controller(game)
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
      val controller = new Controller(game)
      val tui = new TUI(controller)
      tui.buildStatusString(game).should(include("Passen."))
    }

    "buildStatusString - InvalidMove" in {
      val game = createGameState(
        players = List.empty,
        lastEvent = Some(GameEvent.InvalidMove)
      )
      val controller = new Controller(game)
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
      val controller = new Controller(game)
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
      val controller = new Controller(game)
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
      val controller = new Controller(game)
      val tui = new TUI(controller)
      tui.buildStatusString(game).shouldBe("Spiel beendet.")
    }

    "ask for deck size and use default" in {
      val controller = new Controller(createGameState(List.empty))
      val tui = new TUI(controller)
      val input = "\n"
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

    "update method should print to console with active player highlighted" in {
      val player = Player("TestPlayer", List(heartAce))
      val game = createGameState(
        players = List(player),
        trumpCard = defaultTrumpCard,
        gamePhase = AttackPhase,
        attackerIndex = 0
      )
      val controller = new Controller(game)
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
        val controller = new Controller(createGameState(List.empty))
        val tui = new TUI(controller)
        val card = Card(Suit.Hearts, Rank.Ace)
        val renderedCard = tui.renderCard(card).mkString
        renderedCard should include("A")
        renderedCard should include("\u2665")
      }

      "contain rank and suit for a black card" in {
        val controller = new Controller(createGameState(List.empty))
        val tui = new TUI(controller)
        val card = Card(Suit.Spades, Rank.King)
        val renderedCard = tui.renderCard(card).mkString
        renderedCard should include("K")
        renderedCard should include("\u2660")
      }
    }

    "renderHandWithIndices" should {
      "return 'Leere Hand' for an empty hand" in {
        val controller = new Controller(createGameState(List.empty))
        val tui = new TUI(controller)
        tui.renderHandWithIndices(List.empty) should be("Leere Hand")
      }

      "render a single card with its index" in {
        val controller = new Controller(createGameState(List.empty))
        val tui = new TUI(controller)
        val hand = List(Card(Suit.Diamonds, Rank.Ten))
        val renderedHand = tui.renderHandWithIndices(hand)
        renderedHand should include("10")
        renderedHand should include("\u2666")
        renderedHand should endWith("\n   0   ")
      }

      "render multiple cards with their indices" in {
        val controller = new Controller(createGameState(List.empty))
        val tui = new TUI(controller)
        val hand =
          List(Card(Suit.Diamonds, Rank.Ten), Card(Suit.Clubs, Rank.Jack))
        val renderedHand = tui.renderHandWithIndices(hand)

        renderedHand should include("10")
        renderedHand should include("\u2666")
        renderedHand should include("J")
        renderedHand should include("\u2663")

        renderedHand should include regex "\n\\s*0\\s+1\\s*"
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
      val controller = new Controller(game)
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
      output should include(s"${GREEN}Alice$RESET, dein Zug ('play index', 'pass'):")
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
      val controller = new Controller(game)
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
      output should include(s"${GREEN}Bob$RESET, dein Zug ('play index', 'take'):")
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
      val controller = new Controller(gameAttackPhase)
      val tui = new TUI(controller)

      "handle 'play 0' during AttackPhase" in {
        val action = tui.parseTuiInput("play 0", gameAttackPhase)
        action should be(de.htwg.DurakApp.controller.PlayCardAction(spadeSix))
      }

      "handle 'play 0' during DefensePhase" in {
        val action = tui.parseTuiInput("play 0", gameDefensePhase)
        action should be(de.htwg.DurakApp.controller.PlayCardAction(heartAce))
      }

      "handle 'pass'" in {
        val action = tui.parseTuiInput("pass", gameAttackPhase)
        action should be(de.htwg.DurakApp.controller.PassAction)
      }

      "handle 'take'" in {
        val action = tui.parseTuiInput("take", gameDefensePhase)
        action should be(de.htwg.DurakApp.controller.TakeCardsAction)
      }

      "handle invalid command" in {
        val action = tui.parseTuiInput("foo", gameAttackPhase)
        action should be(de.htwg.DurakApp.controller.InvalidAction)
      }

      "handle 'play' without index" in {
        val action = tui.parseTuiInput("play", gameAttackPhase)
        action should be(de.htwg.DurakApp.controller.InvalidAction)
      }

      "handle 'play' with non-numeric index" in {
        val action = tui.parseTuiInput("play foo", gameAttackPhase)
        action should be(de.htwg.DurakApp.controller.InvalidAction)
      }

      "handle 'play' with out-of-bounds index" in {
        val action = tui.parseTuiInput("play 5", gameAttackPhase)
        action should be(de.htwg.DurakApp.controller.InvalidAction)
      }

      "handle case-insensitivity and whitespace" in {
        val action = tui.parseTuiInput("  PLaY 0  ", gameAttackPhase)
        action should be(de.htwg.DurakApp.controller.PlayCardAction(spadeSix))
      }
    }

    "terminate on 'quit' input" in {
      val game = createGameState(
        players = List(Player("Alice", List.empty)),
        gamePhase = AttackPhase,
        attackerIndex = 0
      )
      val controller = new Controller(game)
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

    "stop the gameLoop when controller sets GameOver after an action" in {
      val attacker = Player("A", List(spadeSix))
      val defender = Player("D", List(heartAce))
      val initial = createGameState(
        players = List(attacker, defender),
        gamePhase = AttackPhase
      )

      class TestController(gs: GameState) extends Controller(gs) {
        override def processPlayerAction(
            action: de.htwg.DurakApp.controller.PlayerAction
        ): Unit = {
          this.gameState = this.gameState.copy(lastEvent =
            Some(GameEvent.GameOver(attacker, Some(defender)))
          )
        }
      }

      val controller = new TestController(initial)
      val tui = new TUI(controller)

      val input =
        "unknown\n"
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

    "print the attacker prompt for non-attack/defense phases (default case)" in {
      val attacker = Player("RoundAttacker", List(spadeSix))
      val defender = Player("RoundDefender", List(heartAce))
      val game = createGameState(
        players = List(attacker, defender),
        gamePhase = RoundPhase,
        attackerIndex = 0
      )
      val controller = new Controller(game)
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
        s"${GREEN}RoundAttacker$RESET, dein Zug ('play index', 'pass', 'take'):"
      )
    }

    "print the correct prompt for DrawPhase (default case)" in {
      val player = Player("DrawPlayer", List(spadeSix))
      val game = createGameState(
        players = List(player),
        gamePhase = DrawPhase,
        attackerIndex = 0
      )
      val controller = new Controller(game)
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
        s"${GREEN}DrawPlayer$RESET, dein Zug ('play index', 'pass', 'take'):"
      )
    }

    "renderCard includes Nine and Queen correctly" in {
      val controller = new Controller(createGameState(List.empty))
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
      val controller = new Controller(game)
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
      val controller = new Controller(createGameState(List.empty))
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
      val controller = new Controller(createGameState(List.empty))
      val tui = new TUI(controller)

      tui.combineCardLines(List.empty) shouldBe ""
    }

    "renderCard includes Seven and Eight correctly" in {
      val controller = new Controller(createGameState(List.empty))
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

    "gameLoop continues when no GameOver (case _ => gameLoop())" in {
      val attacker = Player("A", List(spadeSix))
      val defender = Player("D", List(heartAce))
      val initial = createGameState(
        players = List(attacker, defender),
        gamePhase = AttackPhase
      )

      class TestController(gs: GameState) extends Controller(gs) {
        var calls = 0
        override def processPlayerAction(
            action: de.htwg.DurakApp.controller.PlayerAction
        ): Unit = {
          calls += 1
          this.gameState = this.gameState.copy(lastEvent = Some(GameEvent.Pass))
        }
      }

      val controller = new TestController(initial)
      val tui = new TUI(controller)

      val input = "pass\nq\n"
      val inStream = new ByteArrayInputStream(input.getBytes)
      val outStream = new ByteArrayOutputStream()

      Console.withIn(inStream) {
        Console.withOut(outStream) {
          tui.run()
        }
      }

      val output = outStream.toString()
      controller.calls shouldBe 1
      output should include("Spiel beendet.")
    }

  }
}
