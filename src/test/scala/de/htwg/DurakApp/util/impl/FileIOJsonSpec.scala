package de.htwg.DurakApp.util.impl

import de.htwg.DurakApp.model.*
import de.htwg.DurakApp.model.state.{GameEvent, GamePhase}
import de.htwg.DurakApp.testutil.StubGamePhasesImpl
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import scala.util.{Success, Failure}
import java.io.{File, PrintWriter}

class FileIOJsonSpec extends AnyWordSpec with Matchers:

  "FileIOJson" should {

    val testFilePath = "test_gamestate.json"
    val stubGamePhases = new StubGamePhasesImpl()
    val fileIO = new FileIOJson(testFilePath, stubGamePhases)

    val card1 = Card(Suit.Hearts, Rank.Ace, isTrump = true)
    val card2 = Card(Suit.Spades, Rank.King, isTrump = false)
    val player1 = Player("Alice", List(card1), isDone = false)
    val player2 = Player("Bob", List(card2), isDone = false)

    val testGameState = GameState(
      players = List(player1, player2),
      mainAttackerIndex = 0,
      defenderIndex = 1,
      currentAttackerIndex = Some(0),
      lastAttackerIndex = None,
      passedPlayers = Set.empty,
      roundWinner = None,
      deck = List(card1, card2),
      table = Map(card1 -> Some(card2)),
      discardPile = List.empty,
      trumpCard = card1,
      gamePhase = new GamePhase {
        override def handle(gameState: GameState): GameState = gameState
      },
      lastEvent = Some(GameEvent.Attack(card1)),
      setupPlayerCount = Some(2),
      setupPlayerNames = List("Alice", "Bob"),
      setupDeckSize = Some(36)
    )

    "save a GameState to JSON file" in {
      val result = fileIO.save(testGameState)
      result shouldBe a[Success[?]]

      val file = new File(testFilePath)
      file.exists() shouldBe true
    }

    "load a GameState from JSON file" in {
      fileIO.save(testGameState)

      val result = fileIO.load()
      result shouldBe a[Success[?]]

      val loadedState = result.get
      loadedState.players.size shouldBe 2
      loadedState.players.head.name shouldBe "Alice"
      loadedState.players(1).name shouldBe "Bob"
      loadedState.mainAttackerIndex shouldBe 0
      loadedState.defenderIndex shouldBe 1
      loadedState.currentAttackerIndex shouldBe Some(0)
      loadedState.deck.size shouldBe 2
      loadedState.table.size shouldBe 1
      loadedState.trumpCard.suit shouldBe Suit.Hearts
      loadedState.trumpCard.rank shouldBe Rank.Ace
      loadedState.setupPlayerCount shouldBe Some(2)
      loadedState.setupDeckSize shouldBe Some(36)
    }

    "save and load preserves card properties" in {
      fileIO.save(testGameState)
      val loadedState = fileIO.load().get

      val loadedCard = loadedState.players.head.hand.head
      loadedCard.suit shouldBe card1.suit
      loadedCard.rank shouldBe card1.rank
      loadedCard.isTrump shouldBe card1.isTrump
    }

    "save and load preserves table map structure" in {
      fileIO.save(testGameState)
      val loadedState = fileIO.load().get

      loadedState.table should not be empty
      val tableEntry = loadedState.table.head
      tableEntry._1.suit shouldBe card1.suit
      tableEntry._2 shouldBe defined
      tableEntry._2.get.suit shouldBe card2.suit
    }

    "handle GameState with empty collections" in {
      val emptyGameState = testGameState.copy(
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        passedPlayers = Set.empty,
        setupPlayerNames = List.empty
      )

      fileIO.save(emptyGameState)
      val loadedState = fileIO.load().get

      loadedState.deck shouldBe empty
      loadedState.table shouldBe empty
      loadedState.discardPile shouldBe empty
      loadedState.passedPlayers shouldBe empty
    }

    "handle GameState with None options" in {
      val noneGameState = testGameState.copy(
        currentAttackerIndex = None,
        lastAttackerIndex = None,
        roundWinner = None,
        lastEvent = None,
        setupPlayerCount = None,
        setupDeckSize = None
      )

      fileIO.save(noneGameState)
      val loadedState = fileIO.load().get

      loadedState.currentAttackerIndex shouldBe None
      loadedState.lastAttackerIndex shouldBe None
      loadedState.roundWinner shouldBe None
      loadedState.lastEvent shouldBe None
      loadedState.setupPlayerCount shouldBe None
      loadedState.setupDeckSize shouldBe None
    }

    "handle all GameEvent types" in {
      val events = List(
        GameEvent.InvalidMove,
        GameEvent.NotYourTurn,
        GameEvent.Attack(card1),
        GameEvent.Defend(card2),
        GameEvent.Pass,
        GameEvent.Take,
        GameEvent.Draw,
        GameEvent.RoundEnd(true),
        GameEvent.RoundEnd(false),
        GameEvent.GameOver(player1, Some(player2)),
        GameEvent.GameOver(player1, None),
        GameEvent.CannotUndo,
        GameEvent.CannotRedo,
        GameEvent.AskPlayerCount,
        GameEvent.AskPlayerNames,
        GameEvent.AskDeckSize,
        GameEvent.GameSetupComplete,
        GameEvent.SetupError,
        GameEvent.AskPlayAgain,
        GameEvent.ExitApplication,
        GameEvent.GameSaved,
        GameEvent.GameLoaded,
        GameEvent.SaveError,
        GameEvent.LoadError
      )

      events.foreach { event =>
        val gameStateWithEvent = testGameState.copy(lastEvent = Some(event))
        fileIO.save(gameStateWithEvent)
        val loadedState = fileIO.load().get
        loadedState.lastEvent shouldBe defined
      }
    }

    "handle table with None defense cards" in {
      val tableWithNone = Map(card1 -> None, card2 -> Some(card1))
      val gameState = testGameState.copy(table = tableWithNone)

      fileIO.save(gameState)
      val loadedState = fileIO.load().get

      loadedState.table.size shouldBe 2
      loadedState.table.get(card1) shouldBe Some(None)
      loadedState.table.get(card2).flatten shouldBe Some(card1)
    }

    "return Failure when JSON is malformed" in {
      val pw = new PrintWriter(new File(testFilePath))
      pw.write("{invalid json")
      pw.close()

      val result = fileIO.load()
      result shouldBe a[Failure[?]]
    }

    "return Failure when JSON has wrong structure" in {
      val pw = new PrintWriter(new File(testFilePath))
      pw.write("""{"wrongField": "value"}""")
      pw.close()

      val result = fileIO.load()
      result shouldBe a[Failure[?]]
    }

    "save JSON in pretty format" in {
      fileIO.save(testGameState)

      val source = scala.io.Source.fromFile(testFilePath)
      val content =
        try source.mkString
        finally source.close()

      content should include("\n")
      content should include("  ")
    }

    "return Failure when loading non-existent file" in {
      val nonExistentFileIO = new FileIOJson("nonexistent.json", stubGamePhases)
      val result = nonExistentFileIO.load()
      result shouldBe a[Failure[?]]
    }

    "handle unknown event type in JSON" in {
      import play.api.libs.json.*

      fileIO.save(testGameState)

      val source = scala.io.Source.fromFile(testFilePath)
      val content =
        try source.mkString
        finally source.close()

      val modifiedContent =
        content.replace("\"Attack\"", "\"UnknownEventType\"")

      val pw = new PrintWriter(new File(testFilePath))
      pw.write(modifiedContent)
      pw.close()

      val result = fileIO.load()
      result shouldBe a[Failure[?]]
    }

    "gamePhase handle method returns same state" in {
      fileIO.save(testGameState)
      val loadedState = fileIO.load().get

      val handledState = loadedState.gamePhase.handle(loadedState)
      handledState shouldBe loadedState
    }

    "test GameState with passedPlayers" in {
      val stateWithPassed = testGameState.copy(passedPlayers = Set(0, 1))
      fileIO.save(stateWithPassed)
      val loadedState = fileIO.load().get

      loadedState.passedPlayers shouldBe Set(0, 1)
    }

    "test GameState with discardPile" in {
      val stateWithDiscard =
        testGameState.copy(discardPile = List(card1, card2, card1))
      fileIO.save(stateWithDiscard)
      val loadedState = fileIO.load().get

      loadedState.discardPile.size shouldBe 3
    }

    "test all Suit types" in {
      val suits = List(Suit.Hearts, Suit.Diamonds, Suit.Clubs, Suit.Spades)
      suits.foreach { suit =>
        val card = Card(suit, Rank.Ace, isTrump = false)
        val state = testGameState.copy(trumpCard = card)
        fileIO.save(state)
        val loadedState = fileIO.load().get
        loadedState.trumpCard.suit shouldBe suit
      }
    }

    "test all Rank types" in {
      val ranks = List(
        Rank.Six,
        Rank.Seven,
        Rank.Eight,
        Rank.Nine,
        Rank.Ten,
        Rank.Jack,
        Rank.Queen,
        Rank.King,
        Rank.Ace
      )
      ranks.foreach { rank =>
        val card = Card(Suit.Hearts, rank, isTrump = false)
        val state = testGameState.copy(trumpCard = card)
        fileIO.save(state)
        val loadedState = fileIO.load().get
        loadedState.trumpCard.rank shouldBe rank
      }
    }

    "test roundWinner field" in {
      val stateWithWinner = testGameState.copy(roundWinner = Some(1))
      fileIO.save(stateWithWinner)
      val loadedState = fileIO.load().get
      loadedState.roundWinner shouldBe Some(1)
    }

    "test lastAttackerIndex field" in {
      val stateWithLastAttacker =
        testGameState.copy(lastAttackerIndex = Some(1))
      fileIO.save(stateWithLastAttacker)
      val loadedState = fileIO.load().get
      loadedState.lastAttackerIndex shouldBe Some(1)
    }

    "test undoStack serialization" in {
      val state1 = testGameState.copy(mainAttackerIndex = 0)
      val state2 = testGameState.copy(mainAttackerIndex = 1)
      val stateWithUndo = testGameState.copy(undoStack = List(state1, state2))

      fileIO.save(stateWithUndo)
      val loadedState = fileIO.load().get

      loadedState.undoStack.size shouldBe 2
      loadedState.undoStack(0).mainAttackerIndex shouldBe 0
      loadedState.undoStack(1).mainAttackerIndex shouldBe 1
    }

    "test redoStack serialization" in {
      val state1 = testGameState.copy(defenderIndex = 0)
      val state2 = testGameState.copy(defenderIndex = 1)
      val stateWithRedo = testGameState.copy(redoStack = List(state1, state2))

      fileIO.save(stateWithRedo)
      val loadedState = fileIO.load().get

      loadedState.redoStack.size shouldBe 2
      loadedState.redoStack(0).defenderIndex shouldBe 0
      loadedState.redoStack(1).defenderIndex shouldBe 1
    }

    "test empty undoStack and redoStack" in {
      val stateWithEmpty =
        testGameState.copy(undoStack = List.empty, redoStack = List.empty)

      fileIO.save(stateWithEmpty)
      val loadedState = fileIO.load().get

      loadedState.undoStack shouldBe empty
      loadedState.redoStack shouldBe empty
    }

    "test all GamePhase types" in {
      val phases = List(
        ("SetupPhase", stubGamePhases.setupPhase),
        ("AskPlayerCountPhase", stubGamePhases.askPlayerCountPhase),
        ("AskPlayerNamesPhase", stubGamePhases.askPlayerNamesPhase),
        ("AskDeckSizePhase", stubGamePhases.askDeckSizePhase),
        ("GameStartPhase", stubGamePhases.gameStartPhase),
        ("AttackPhase", stubGamePhases.attackPhase),
        ("DefensePhase", stubGamePhases.defensePhase),
        ("DrawPhase", stubGamePhases.drawPhase),
        ("RoundPhase", stubGamePhases.roundPhase),
        ("EndPhase", stubGamePhases.endPhase),
        ("AskPlayAgainPhase", stubGamePhases.askPlayAgainPhase)
      )

      phases.foreach { case (phaseName, phase) =>
        val stateWithPhase = testGameState.copy(gamePhase = phase)
        fileIO.save(stateWithPhase)
        val loadedState = fileIO.load().get
        loadedState.gamePhase should not be null
      }
    }

    "test GamePhase with 'Impl' suffix variants" in {
      import play.api.libs.json.*

      val phaseVariants = List(
        "SetupPhaseImpl",
        "AskPlayerCountPhaseImpl",
        "AskPlayerNamesPhaseImpl",
        "AskDeckSizePhaseImpl",
        "GameStartPhaseImpl",
        "AttackPhaseImpl",
        "DefensePhaseImpl",
        "DrawPhaseImpl",
        "RoundPhaseImpl",
        "EndPhaseImpl",
        "AskPlayAgainPhaseImpl"
      )

      phaseVariants.foreach { phaseImpl =>
        fileIO.save(testGameState)

        val source = scala.io.Source.fromFile(testFilePath)
        val content =
          try source.mkString
          finally source.close()

        val json = Json.parse(content).as[JsObject]
        val modifiedJson = json + ("gamePhase" -> JsString(phaseImpl))

        val pw = new PrintWriter(new File(testFilePath))
        pw.write(Json.prettyPrint(modifiedJson))
        pw.close()

        val loadedState = fileIO.load().get
        loadedState.gamePhase should not be null
      }
    }

    "test loading JSON without undoStack and redoStack fields" in {
      import play.api.libs.json.*

      fileIO.save(testGameState)

      val source = scala.io.Source.fromFile(testFilePath)
      val content =
        try source.mkString
        finally source.close()

      val json = Json.parse(content).as[JsObject]
      val jsonWithoutStacks = json - "undoStack" - "redoStack"

      val pw = new PrintWriter(new File(testFilePath))
      pw.write(Json.prettyPrint(jsonWithoutStacks))
      pw.close()

      val loadedState = fileIO.load().get
      loadedState.undoStack shouldBe empty
      loadedState.redoStack shouldBe empty
    }

    "cleanup test file" in {
      val file = new File(testFilePath)
      if (file.exists()) file.delete()
    }
  }
