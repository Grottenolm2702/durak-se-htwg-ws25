package de.htwg.DurakApp.aview.tui
import de.htwg.DurakApp.testutil.TestHelper._
import de.htwg.DurakApp.testutil.StubGamePhases
import de.htwg.DurakApp.model.state.GamePhases
object TestGamePhases extends GamePhases {
  def setupPhase = StubGamePhases.setupPhase
  def askPlayerCountPhase = StubGamePhases.askPlayerCountPhase
  def askPlayerNamesPhase = StubGamePhases.askPlayerNamesPhase
  def askDeckSizePhase = StubGamePhases.askDeckSizePhase
  def askPlayAgainPhase = StubGamePhases.askPlayAgainPhase
  def gameStartPhase = StubGamePhases.gameStartPhase
  def attackPhase = StubGamePhases.attackPhase
  def defensePhase = StubGamePhases.defensePhase
  def drawPhase = StubGamePhases.drawPhase
  def roundPhase = StubGamePhases.roundPhase
  def endPhase = StubGamePhases.endPhase
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
import java.io.{PrintStream, OutputStream}
class TUISpec extends AnyWordSpec with Matchers {
  val nullOutputStream = new PrintStream(new OutputStream {
    override def write(b: Int): Unit = ()
  })
  "A TUI" should {
    "be created with a controller" in {
      val initialState = TestHelper.createTestGameState()
      val controller = new SpyController(
        initialState,
        new StubUndoRedoManager()
      )
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      tui.should(not(be(null)))
    }
    "build status string for attack phase" in {
      val attacker = TestHelper.Player("Angreifer", List.empty)
      val defender = TestHelper.Player("Verteidiger", List.empty)
      val game = TestHelper.createTestGameState(
        players = List(attacker, defender),
        gamePhase = TestGamePhases.setupPhase,
        lastEvent =
          Some(GameEvent.Attack(TestHelper.Card(Suit.Spades, Rank.Six)))
      )
      val controller =
        new SpyController(game, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val statusString = tui.buildStatusString(game)
      statusString.should(not(be(empty)))
      statusString.should(include("Angriff"))
    }
    "respond to update notification" in {
      val initialState = TestHelper.createTestGameState()
      val controller = new SpyController(
        initialState,
        new StubUndoRedoManager()
      )
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      noException shouldBe thrownBy { tui.update }
    }
    "show correct description for TestGamePhases.setupPhase" in {
      val gameState =
        TestHelper.createTestGameState(gamePhase = TestGamePhases.setupPhase)
      val controller = new SpyController(
        gameState,
        new StubUndoRedoManager()
      )
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val desc = tui.description(gameState)
      desc.should(include("Spieleranzahl"))
    }
    "show correct description for TestGamePhases.askPlayerCountPhase" in {
      val gameState = TestHelper.createTestGameState(gamePhase =
        TestGamePhases.askPlayerCountPhase
      )
      val controller = new SpyController(
        gameState,
        new StubUndoRedoManager()
      )
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val desc = tui.description(gameState)
      desc.should(include("Spieleranzahl"))
    }
    "show correct description for TestGamePhases.askPlayerNamesPhase" in {
      val gameState = TestHelper.createTestGameState(
        gamePhase = TestGamePhases.askPlayerNamesPhase,
        setupPlayerNames = List("Alice")
      )
      val controller = new SpyController(
        gameState,
        new StubUndoRedoManager()
      )
      val tui = new TUI(
        controller,
        new de.htwg.DurakApp.testutil.StubGamePhasesImpl(),
        nullOutputStream
      )
      val desc = tui.description(gameState)
      desc.should(include("Spielername"))
      desc.should(include("2"))
    }
    "show correct description for TestGamePhases.askDeckSizePhase" in {
      val gameState = TestHelper.createTestGameState(
        gamePhase = TestGamePhases.askDeckSizePhase,
        setupPlayerNames = List("Alice", "Bob")
      )
      val controller = new SpyController(
        gameState,
        new StubUndoRedoManager()
      )
      val tui = new TUI(
        controller,
        new de.htwg.DurakApp.testutil.StubGamePhasesImpl(),
        nullOutputStream
      )
      val desc = tui.description(gameState)
      desc.should(include("Deckgröße"))
      desc.should(include("2"))
    }
    "show correct description for TestGamePhases.askPlayAgainPhase" in {
      val gameState = TestHelper.createTestGameState(gamePhase =
        TestGamePhases.askPlayAgainPhase
      )
      val controller = new SpyController(
        gameState,
        new StubUndoRedoManager()
      )
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val desc = tui.description(gameState)
      desc.should(include("neue Runde"))
    }
    "show correct description for TestGamePhases.attackPhase" in {
      val gameState =
        TestHelper.createTestGameState(gamePhase = TestGamePhases.attackPhase)
      val controller = new SpyController(
        gameState,
        new StubUndoRedoManager()
      )
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val desc = tui.description(gameState)
      desc.should(include("AttackPhase"))
    }
    "build status string contains important information" in {
      val player1 =
        TestHelper.Player("Alice", List(TestHelper.Card(Suit.Hearts, Rank.Six)))
      val player2 = TestHelper.Player(
        "Bob",
        List(TestHelper.Card(Suit.Diamonds, Rank.Seven))
      )
      val gameState = TestHelper.createTestGameState(
        players = List(player1, player2),
        gamePhase = TestGamePhases.attackPhase
      )
      val controller = new SpyController(
        gameState,
        new StubUndoRedoManager()
      )
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val statusString = tui.buildStatusString(gameState)
      statusString.should(not(be(empty)))
      statusString.length.should(be > 0)
    }
    "build status string for TestGamePhases.defensePhase" in {
      val player1 =
        TestHelper.Player("Alice", List(TestHelper.Card(Suit.Hearts, Rank.Six)))
      val player2 = TestHelper.Player(
        "Bob",
        List(TestHelper.Card(Suit.Diamonds, Rank.Seven))
      )
      val gameState = TestHelper.createTestGameState(
        players = List(player1, player2),
        gamePhase = TestGamePhases.setupPhase,
        lastEvent =
          Some(GameEvent.Defend(TestHelper.Card(Suit.Hearts, Rank.Seven)))
      )
      val controller = new SpyController(
        gameState,
        new StubUndoRedoManager()
      )
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val statusString = tui.buildStatusString(gameState)
      statusString.should(not(be(empty)))
      statusString.should(include("Verteidigung"))
    }
    "build status string for GameOver event contains end message" in {
      val player1 = TestHelper.Player("Alice", List.empty)
      val player2 =
        TestHelper.Player("Bob", List(TestHelper.Card(Suit.Hearts, Rank.Six)))
      val gameState = TestHelper.createTestGameState(
        players = List(player1, player2),
        gamePhase = TestGamePhases.setupPhase,
        lastEvent = Some(GameEvent.GameOver(player1, Some(player2)))
      )
      val controller = new SpyController(
        gameState,
        new StubUndoRedoManager()
      )
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val statusString = tui.buildStatusString(gameState)
      statusString.should(include("Spiel"))
    }
    "render card correctly" in {
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace)
      val tui = new TUI(
        new SpyController(
          TestHelper.createTestGameState(),
          new StubUndoRedoManager()
        ),
        TestGamePhases,
        nullOutputStream
      )
      val rendered = tui.renderCard(card)
      rendered should have length 5
      rendered.head should startWith("+")
    }
    "render hand with indices" in {
      val card1 = TestHelper.Card(Suit.Hearts, Rank.Six)
      val card2 = TestHelper.Card(Suit.Spades, Rank.King)
      val tui = new TUI(
        new SpyController(
          TestHelper.createTestGameState(),
          new StubUndoRedoManager()
        ),
        TestGamePhases,
        nullOutputStream
      )
      val rendered = tui.renderHandWithIndices(List(card1, card2))
      rendered should not be empty
      rendered should include("0")
      rendered should include("1")
    }
    "render empty hand" in {
      val tui = new TUI(
        new SpyController(
          TestHelper.createTestGameState(),
          new StubUndoRedoManager()
        ),
        TestGamePhases,
        nullOutputStream
      )
      val rendered = tui.renderHandWithIndices(List.empty)
      rendered shouldBe "Leere Hand"
    }
    "combine card lines correctly" in {
      val tui = new TUI(
        new SpyController(
          TestHelper.createTestGameState(),
          new StubUndoRedoManager()
        ),
        TestGamePhases,
        nullOutputStream
      )
      val line1 = List("a", "b")
      val line2 = List("c", "d")
      val combined = tui.combineCardLines(List(line1, line2))
      combined shouldBe "a c\nb d"
    }
    "combine empty card lines" in {
      val tui = new TUI(
        new SpyController(
          TestHelper.createTestGameState(),
          new StubUndoRedoManager()
        ),
        TestGamePhases,
        nullOutputStream
      )
      val combined = tui.combineCardLines(List.empty)
      combined shouldBe ""
    }
    "clear screen returns escape sequence" in {
      val tui = new TUI(
        new SpyController(
          TestHelper.createTestGameState(),
          new StubUndoRedoManager()
        ),
        TestGamePhases,
        nullOutputStream
      )
      tui.clearScreen() shouldBe "\u001b[2J\u001b[H"
    }
    "render game state for non-setup phase" in {
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace, isTrump = true)
      val player1 = TestHelper.Player("Alice", List(card))
      val player2 = TestHelper.Player("Bob", List(card))
      val gameState = TestHelper.createTestGameState(
        players = List(player1, player2),
        trumpCard = card,
        gamePhase = TestGamePhases.attackPhase
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      noException shouldBe thrownBy { tui.update }
    }
    "handle defense phase in update" in {
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace, isTrump = true)
      val player1 = TestHelper.Player("Alice", List(card))
      val player2 = TestHelper.Player("Bob", List(card))
      val gameState = TestHelper.createTestGameState(
        players = List(player1, player2),
        trumpCard = card,
        gamePhase = TestGamePhases.defensePhase
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      noException shouldBe thrownBy { tui.update }
    }
    "handle draw phase in update" in {
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace, isTrump = true)
      val player1 = TestHelper.Player("Alice", List(card))
      val player2 = TestHelper.Player("Bob", List(card))
      val gameState = TestHelper.createTestGameState(
        players = List(player1, player2),
        trumpCard = card,
        gamePhase = TestGamePhases.drawPhase
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      noException shouldBe thrownBy { tui.update }
    }
    "handle round phase in description" in {
      val gameState = TestHelper.createTestGameState(
        gamePhase = TestGamePhases.roundPhase
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val desc = tui.description(gameState)
      desc should not be empty
    }
    "handle end phase in description" in {
      val gameState = TestHelper.createTestGameState(
        gamePhase = TestGamePhases.endPhase
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val desc = tui.description(gameState)
      desc should not be empty
    }
    "build status string for GameSaved event" in {
      val game = TestHelper.createTestGameState(
        lastEvent = Some(GameEvent.GameSaved)
      )
      val controller = new SpyController(game, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val statusString = tui.buildStatusString(game)
      statusString should include("gespeichert")
    }
    "build status string for GameLoaded event" in {
      val game = TestHelper.createTestGameState(
        lastEvent = Some(GameEvent.GameLoaded)
      )
      val controller = new SpyController(game, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val statusString = tui.buildStatusString(game)
      statusString should include("geladen")
    }
    "build status string for SaveError event" in {
      val game = TestHelper.createTestGameState(
        lastEvent = Some(GameEvent.SaveError)
      )
      val controller = new SpyController(game, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val statusString = tui.buildStatusString(game)
      statusString should include("Fehler")
      statusString should include("Speichern")
    }
    "build status string for LoadError event" in {
      val game = TestHelper.createTestGameState(
        lastEvent = Some(GameEvent.LoadError)
      )
      val controller = new SpyController(game, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val statusString = tui.buildStatusString(game)
      statusString should include("Fehler")
      statusString should include("Laden")
    }
    "render table with cards" in {
      val attackCard = TestHelper.Card(Suit.Hearts, Rank.Six)
      val defenseCard = TestHelper.Card(Suit.Spades, Rank.Seven)
      val table = Map(attackCard -> Some(defenseCard))
      val gameState = TestHelper.createTestGameState(
        table = table,
        gamePhase = TestGamePhases.attackPhase
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      noException shouldBe thrownBy { tui.update }
    }
    "render table with undefended cards" in {
      val attackCard = TestHelper.Card(Suit.Hearts, Rank.Six)
      val table = Map(attackCard -> None)
      val gameState = TestHelper.createTestGameState(
        table = table,
        gamePhase = TestGamePhases.attackPhase
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      noException shouldBe thrownBy { tui.update }
    }
    "handle all card suits in cardColor" in {
      val tui = new TUI(
        new SpyController(
          TestHelper.createTestGameState(),
          new StubUndoRedoManager()
        ),
        TestGamePhases,
        nullOutputStream
      )
      noException shouldBe thrownBy {
        tui.renderCard(TestHelper.Card(Suit.Hearts, Rank.Ace))
        tui.renderCard(TestHelper.Card(Suit.Diamonds, Rank.Ace))
        tui.renderCard(TestHelper.Card(Suit.Clubs, Rank.Ace))
        tui.renderCard(TestHelper.Card(Suit.Spades, Rank.Ace))
      }
    }
    "handle all card ranks" in {
      val tui = new TUI(
        new SpyController(
          TestHelper.createTestGameState(),
          new StubUndoRedoManager()
        ),
        TestGamePhases,
        nullOutputStream
      )
      noException shouldBe thrownBy {
        tui.renderCard(TestHelper.Card(Suit.Hearts, Rank.Six))
        tui.renderCard(TestHelper.Card(Suit.Hearts, Rank.Seven))
        tui.renderCard(TestHelper.Card(Suit.Hearts, Rank.Eight))
        tui.renderCard(TestHelper.Card(Suit.Hearts, Rank.Nine))
        tui.renderCard(TestHelper.Card(Suit.Hearts, Rank.Ten))
        tui.renderCard(TestHelper.Card(Suit.Hearts, Rank.Jack))
        tui.renderCard(TestHelper.Card(Suit.Hearts, Rank.Queen))
        tui.renderCard(TestHelper.Card(Suit.Hearts, Rank.King))
        tui.renderCard(TestHelper.Card(Suit.Hearts, Rank.Ace))
      }
    }
    "render table with empty attack and defense" in {
      val gameState = TestHelper.createTestGameState(
        table = Map.empty,
        gamePhase = TestGamePhases.attackPhase
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val rendered = tui.renderTable(gameState)
      rendered should include("Angriff")
      rendered should include("Verteidigung")
      rendered should include("Leer")
    }
    "render screen with full game state" in {
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace, isTrump = true)
      val player1 = TestHelper.Player("Alice", List(card))
      val player2 = TestHelper.Player("Bob", List(card))
      val gameState = TestHelper.createTestGameState(
        players = List(player1, player2),
        trumpCard = card,
        deck = List(card),
        discardPile = List(card),
        gamePhase = TestGamePhases.attackPhase
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val rendered = tui.renderScreen(gameState, "Test Status")
      rendered should include("Trumpf")
      rendered should include("Deck")
      rendered should include("Ablagestapel")
      rendered should include("Alice")
      rendered should include("Bob")
      rendered should include("Test Status")
    }
    "build status string for InvalidMove" in {
      val gameState = TestHelper.createTestGameState(
        lastEvent = Some(GameEvent.InvalidMove)
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val status = tui.buildStatusString(gameState)
      status should include("Ungültiger Zug")
    }
    "build status string for NotYourTurn" in {
      val gameState = TestHelper.createTestGameState(
        lastEvent = Some(GameEvent.NotYourTurn)
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val status = tui.buildStatusString(gameState)
      status should include("nicht am Zug")
    }
    "build status string for Pass" in {
      val gameState = TestHelper.createTestGameState(
        lastEvent = Some(GameEvent.Pass)
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val status = tui.buildStatusString(gameState)
      status should include("Passen")
    }
    "build status string for Take" in {
      val gameState = TestHelper.createTestGameState(
        lastEvent = Some(GameEvent.Take)
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val status = tui.buildStatusString(gameState)
      status should include("Karten aufgenommen")
    }
    "build status string for Draw" in {
      val gameState = TestHelper.createTestGameState(
        lastEvent = Some(GameEvent.Draw)
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val status = tui.buildStatusString(gameState)
      status should include("Karten werden gezogen")
    }
    "build status string for RoundEnd cleared" in {
      val gameState = TestHelper.createTestGameState(
        lastEvent = Some(GameEvent.RoundEnd(cleared = true))
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val status = tui.buildStatusString(gameState)
      status should include("Tisch geleert")
    }
    "build status string for RoundEnd not cleared" in {
      val gameState = TestHelper.createTestGameState(
        lastEvent = Some(GameEvent.RoundEnd(cleared = false))
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val status = tui.buildStatusString(gameState)
      status should include("Karten aufgenommen")
    }
    "build status string for GameOver with loser" in {
      val winner = TestHelper.Player("Winner", List.empty)
      val loser = TestHelper.Player("Loser", List.empty)
      val gameState = TestHelper.createTestGameState(
        lastEvent = Some(GameEvent.GameOver(winner, Some(loser)))
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val status = tui.buildStatusString(gameState)
      status should include("Durak")
      status should include("Loser")
    }
    "build status string for GameOver without loser" in {
      val winner = TestHelper.Player("Winner", List.empty)
      val gameState = TestHelper.createTestGameState(
        lastEvent = Some(GameEvent.GameOver(winner, None))
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val status = tui.buildStatusString(gameState)
      status should include("Spiel beendet")
    }
    "build status string for GameOver with Quit winner" in {
      val winner = TestHelper.Player("Quit", List.empty)
      val gameState = TestHelper.createTestGameState(
        lastEvent = Some(GameEvent.GameOver(winner, None))
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val status = tui.buildStatusString(gameState)
      status should include("Spiel beendet")
    }
    "build status string for CannotUndo" in {
      val gameState = TestHelper.createTestGameState(
        lastEvent = Some(GameEvent.CannotUndo)
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val status = tui.buildStatusString(gameState)
      status should include("Nichts zum Rückgängigmachen")
    }
    "build status string for CannotRedo" in {
      val gameState = TestHelper.createTestGameState(
        lastEvent = Some(GameEvent.CannotRedo)
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val status = tui.buildStatusString(gameState)
      status should include("Nichts zum Wiederherstellen")
    }
    "build status string for SetupError" in {
      val gameState = TestHelper.createTestGameState(
        lastEvent = Some(GameEvent.SetupError),
        gamePhase = TestGamePhases.setupPhase
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val status = tui.buildStatusString(gameState)
      status should include("Setup-Fehler")
    }
    "build status string for GameSetupComplete" in {
      val gameState = TestHelper.createTestGameState(
        lastEvent = Some(GameEvent.GameSetupComplete)
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val status = tui.buildStatusString(gameState)
      status should include("Setup abgeschlossen")
    }
    "build status string for AskPlayAgain" in {
      val gameState = TestHelper.createTestGameState(
        lastEvent = Some(GameEvent.AskPlayAgain)
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val status = tui.buildStatusString(gameState)
      status should include("neue Runde")
    }
    "build status string for ExitApplication" in {
      val gameState = TestHelper.createTestGameState(
        lastEvent = Some(GameEvent.ExitApplication)
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val status = tui.buildStatusString(gameState)
      status should include("Anwendung wird beendet")
    }
    "build status string for AskPlayerCount" in {
      val gameState = TestHelper.createTestGameState(
        lastEvent = Some(GameEvent.AskPlayerCount)
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val status = tui.buildStatusString(gameState)
      status shouldBe ""
    }
    "build status string for AskPlayerNames" in {
      val gameState = TestHelper.createTestGameState(
        lastEvent = Some(GameEvent.AskPlayerNames)
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val status = tui.buildStatusString(gameState)
      status shouldBe ""
    }
    "build status string for AskDeckSize" in {
      val gameState = TestHelper.createTestGameState(
        lastEvent = Some(GameEvent.AskDeckSize)
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val status = tui.buildStatusString(gameState)
      status shouldBe ""
    }
    "build status string with no event in non-setup phase" in {
      val gameState = TestHelper.createTestGameState(
        lastEvent = None,
        gamePhase = TestGamePhases.attackPhase
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val status = tui.buildStatusString(gameState)
      status should not be empty
    }
    "build status string with no event in setup phase" in {
      val gameState = TestHelper.createTestGameState(
        lastEvent = None,
        gamePhase = TestGamePhases.setupPhase
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val status = tui.buildStatusString(gameState)
      status shouldBe ""
    }
    "use default Console.out when no PrintStream provided" in {
      val gameState = TestHelper.createTestGameState()
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases)
      tui should not be null
      noException shouldBe thrownBy { tui.clearScreen() }
    }
    "printPrompt should handle else case for non-game phases" in {
      val gameState = TestHelper.createTestGameState(
        gamePhase = TestGamePhases.roundPhase
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      noException shouldBe thrownBy { tui.update }
    }
    "printPrompt should handle else case for end phase" in {
      val gameState = TestHelper.createTestGameState(
        gamePhase = TestGamePhases.endPhase
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      noException shouldBe thrownBy { tui.update }
    }
    "printPrompt should handle else case for askPlayAgain phase" in {
      val gameState = TestHelper.createTestGameState(
        gamePhase = TestGamePhases.askPlayAgainPhase
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      noException shouldBe thrownBy { tui.update }
    }
    "printPrompt should handle else case for gameStart phase" in {
      val gameState = TestHelper.createTestGameState(
        gamePhase = TestGamePhases.gameStartPhase
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      noException shouldBe thrownBy { tui.update }
    }
    "printPrompt should handle drawPhase with no active player" in {
      val player1 = TestHelper.Player("Alice", List.empty)
      val player2 = TestHelper.Player("Bob", List.empty)
      val gameState = TestHelper.createTestGameState(
        players = List(player1, player2),
        gamePhase = TestGamePhases.drawPhase
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      noException shouldBe thrownBy { tui.update }
    }
    "renderScreen should handle defensePhase active player" in {
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace, isTrump = true)
      val player1 = TestHelper.Player("Alice", List(card))
      val player2 = TestHelper.Player("Bob", List(card))
      val gameState = TestHelper.createTestGameState(
        players = List(player1, player2),
        trumpCard = card,
        gamePhase = TestGamePhases.defensePhase,
        defenderIndex = 1
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val rendered = tui.renderScreen(gameState, "Test")
      rendered should include("Bob")
    }
    "renderScreen should handle other phases for active player fallback" in {
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace, isTrump = true)
      val player1 = TestHelper.Player("Alice", List(card))
      val player2 = TestHelper.Player("Bob", List(card))
      val gameState = TestHelper.createTestGameState(
        players = List(player1, player2),
        trumpCard = card,
        gamePhase = TestGamePhases.roundPhase,
        mainAttackerIndex = 0
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val tui = new TUI(controller, TestGamePhases, nullOutputStream)
      val rendered = tui.renderScreen(gameState, "Test")
      rendered should include("Alice")
    }
    "run method should print welcome and end messages" in {
      import java.io.{ByteArrayOutputStream, ByteArrayInputStream, PrintStream}
      val gameState = TestHelper.createTestGameState(
        lastEvent = Some(GameEvent.ExitApplication)
      )
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val outputCapture = new ByteArrayOutputStream()
      val printStream = new PrintStream(outputCapture)
      val inputStream = new ByteArrayInputStream("q\n".getBytes)
      Console.withIn(inputStream) {
        val tui = new TUI(controller, TestGamePhases, printStream)
        tui.run()
      }
      val output = outputCapture.toString
      output should include("Willkommen bei Durak!")
      output should include("Spiel beendet.")
    }
    "run method should add TUI as observer" in {
      import java.io.{ByteArrayOutputStream, ByteArrayInputStream, PrintStream}
      val gameState = TestHelper.createTestGameState()
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val outputCapture = new ByteArrayOutputStream()
      val printStream = new PrintStream(outputCapture)
      val inputStream = new ByteArrayInputStream("quit\n".getBytes)
      Console.withIn(inputStream) {
        val tui = new TUI(controller, TestGamePhases, printStream)
        controller.observers.length shouldBe 0
        tui.run()
        controller.observers should contain(tui)
      }
    }
    "run method should call update during initialization" in {
      import java.io.{ByteArrayOutputStream, ByteArrayInputStream, PrintStream}
      val gameState = TestHelper.createTestGameState()
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val outputCapture = new ByteArrayOutputStream()
      val printStream = new PrintStream(outputCapture)
      val inputStream = new ByteArrayInputStream("q\n".getBytes)
      Console.withIn(inputStream) {
        val tui = new TUI(controller, TestGamePhases, printStream)
        tui.run()
      }
      val output = outputCapture.toString
      output should not be empty
      output.length should be > 20
    }
    "gameLoop should exit on quit command" in {
      import java.io.{ByteArrayOutputStream, ByteArrayInputStream, PrintStream}
      val gameState = TestHelper.createTestGameState()
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val outputCapture = new ByteArrayOutputStream()
      val printStream = new PrintStream(outputCapture)
      val inputStream = new ByteArrayInputStream("quit\n".getBytes)
      Console.withIn(inputStream) {
        val tui = new TUI(controller, TestGamePhases, printStream)
        tui.run()
      }
      val output = outputCapture.toString
      output should include("Spiel beendet.")
    }
    "gameLoop should exit on q command" in {
      import java.io.{ByteArrayOutputStream, ByteArrayInputStream, PrintStream}
      val gameState = TestHelper.createTestGameState()
      val controller = new SpyController(gameState, new StubUndoRedoManager())
      val outputCapture = new ByteArrayOutputStream()
      val printStream = new PrintStream(outputCapture)
      val inputStream = new ByteArrayInputStream("q\n".getBytes)
      Console.withIn(inputStream) {
        val tui = new TUI(controller, TestGamePhases, printStream)
        tui.run()
      }
      val output = outputCapture.toString
      output should include("Spiel beendet.")
    }
    "gameLoop should exit on ExitApplication event" in {
      import java.io.{ByteArrayOutputStream, ByteArrayInputStream, PrintStream}
      val initialState = TestHelper.createTestGameState()
      val exitState = TestHelper.createTestGameState(
        lastEvent = Some(GameEvent.ExitApplication)
      )
      class ExitController
          extends SpyController(initialState, new StubUndoRedoManager()) {
        override def processPlayerAction(
            action: de.htwg.DurakApp.controller.PlayerAction
        ): de.htwg.DurakApp.model.GameState = {
          currentState = exitState
          notifyObservers
          currentState
        }
        override def gameState: de.htwg.DurakApp.model.GameState = currentState
      }
      val controller = new ExitController()
      val outputCapture = new ByteArrayOutputStream()
      val printStream = new PrintStream(outputCapture)
      val inputStream = new ByteArrayInputStream("2\n".getBytes)
      Console.withIn(inputStream) {
        val tui = new TUI(controller, TestGamePhases, printStream)
        tui.run()
      }
      val output = outputCapture.toString
      output should include("Spiel beendet.")
    }
    "gameLoop should handle UndoAction in match case and skip processPlayerAction" in {
      import java.io.{ByteArrayOutputStream, ByteArrayInputStream, PrintStream}
      val initialState = TestHelper.createTestGameState(
        gamePhase = TestGamePhases.attackPhase
      )
      class UndoTestController
          extends SpyController(initialState, new StubUndoRedoManager()) {
        override def processPlayerAction(
            action: de.htwg.DurakApp.controller.PlayerAction
        ): de.htwg.DurakApp.model.GameState = {
          processedActions = processedActions :+ action
          currentState =
            currentState.copy(lastEvent = Some(GameEvent.InvalidMove))
          notifyObservers
          currentState
        }
      }
      val controller = new UndoTestController()
      val outputCapture = new ByteArrayOutputStream()
      val printStream = new PrintStream(outputCapture)
      val inputStream = new ByteArrayInputStream("u\npass\nq\n".getBytes)
      Console.withIn(inputStream) {
        val tui = new TUI(controller, TestGamePhases, printStream)
        tui.run()
      }
      controller.processedActions.size shouldBe 1
      val output = outputCapture.toString
      output should include("Spiel beendet.")
    }
    "gameLoop should handle RedoAction in match case and skip processPlayerAction" in {
      import java.io.{ByteArrayOutputStream, ByteArrayInputStream, PrintStream}
      val initialState = TestHelper.createTestGameState(
        gamePhase = TestGamePhases.attackPhase
      )
      class RedoTestController
          extends SpyController(initialState, new StubUndoRedoManager()) {
        override def processPlayerAction(
            action: de.htwg.DurakApp.controller.PlayerAction
        ): de.htwg.DurakApp.model.GameState = {
          processedActions = processedActions :+ action
          currentState =
            currentState.copy(lastEvent = Some(GameEvent.InvalidMove))
          notifyObservers
          currentState
        }
      }
      val controller = new RedoTestController()
      val outputCapture = new ByteArrayOutputStream()
      val printStream = new PrintStream(outputCapture)
      val inputStream = new ByteArrayInputStream("r\npass\nquit\n".getBytes)
      Console.withIn(inputStream) {
        val tui = new TUI(controller, TestGamePhases, printStream)
        tui.run()
      }
      controller.processedActions.size shouldBe 1
      val output = outputCapture.toString
      output should include("Spiel beendet.")
    }
    "gameLoop should continue recursively when no exit event" in {
      import java.io.{ByteArrayOutputStream, ByteArrayInputStream, PrintStream}
      val initialState = TestHelper.createTestGameState(
        gamePhase = TestGamePhases.setupPhase
      )
      class RecursiveTestController
          extends SpyController(initialState, new StubUndoRedoManager()) {
        override def processPlayerAction(
            action: de.htwg.DurakApp.controller.PlayerAction
        ): de.htwg.DurakApp.model.GameState = {
          processedActions = processedActions :+ action
          currentState =
            currentState.copy(lastEvent = Some(GameEvent.InvalidMove))
          notifyObservers
          currentState
        }
      }
      val controller = new RecursiveTestController()
      val outputCapture = new ByteArrayOutputStream()
      val printStream = new PrintStream(outputCapture)
      val inputStream = new ByteArrayInputStream("2\n3\nq\n".getBytes)
      Console.withIn(inputStream) {
        val tui = new TUI(controller, TestGamePhases, printStream)
        tui.run()
      }
      controller.processedActions.size should be >= 2
      val output = outputCapture.toString
      output should include("Spiel beendet.")
    }
    "gameLoop should handle non-exit events and continue" in {
      import java.io.{ByteArrayOutputStream, ByteArrayInputStream, PrintStream}
      val initialState = TestHelper.createTestGameState()
      class ContinueTestController
          extends SpyController(initialState, new StubUndoRedoManager()) {
        override def processPlayerAction(
            action: de.htwg.DurakApp.controller.PlayerAction
        ): de.htwg.DurakApp.model.GameState = {
          processedActions = processedActions :+ action
          currentState =
            currentState.copy(lastEvent = Some(GameEvent.InvalidMove))
          notifyObservers
          currentState
        }
      }
      val controller = new ContinueTestController()
      val outputCapture = new ByteArrayOutputStream()
      val printStream = new PrintStream(outputCapture)
      val inputStream =
        new ByteArrayInputStream("invalid\nstill here\nq\n".getBytes)
      Console.withIn(inputStream) {
        val tui = new TUI(controller, TestGamePhases, printStream)
        tui.run()
      }
      controller.processedActions.size shouldBe 2
      val output = outputCapture.toString
      output should include("Spiel beendet.")
    }
  }
}
