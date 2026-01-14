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
        GameEvent.ExitApplication
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

    "cleanup test file" in {
      val file = new File(testFilePath)
      if (file.exists()) file.delete()
    }
  }
