package de.htwg.DurakApp.util.impl

import de.htwg.DurakApp.model.*
import de.htwg.DurakApp.model.state.{GameEvent, GamePhase}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import scala.util.{Success, Failure}
import java.io.File

class FileIOXmlSpec extends AnyWordSpec with Matchers:
  import de.htwg.DurakApp.testutil.StubGamePhasesImpl

  "FileIOXml" should {

    val testFilePath = "test_gamestate.xml"
    val stubGamePhases = new StubGamePhasesImpl()
    val fileIO = new FileIOXml(testFilePath, stubGamePhases)

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

    "save a GameState to XML file" in {
      val result = fileIO.save(testGameState)
      result shouldBe a[Success[?]]

      val file = new File(testFilePath)
      file.exists() shouldBe true
    }

    "load a GameState from XML file" in {
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

    "handle GameState with multiple passed players" in {
      val gameState = testGameState.copy(passedPlayers = Set(0, 1, 2))

      fileIO.save(gameState)
      val loadedState = fileIO.load().get

      loadedState.passedPlayers shouldBe Set(0, 1, 2)
    }

    "handle GameState with multiple setup player names" in {
      val gameState =
        testGameState.copy(setupPlayerNames = List("Alice", "Bob", "Charlie"))

      fileIO.save(gameState)
      val loadedState = fileIO.load().get

      loadedState.setupPlayerNames shouldBe List("Alice", "Bob", "Charlie")
    }

    "handle GameState with non-empty discard pile" in {
      val card3 = Card(Suit.Diamonds, Rank.Queen, isTrump = false)
      val card4 = Card(Suit.Clubs, Rank.Jack, isTrump = false)
      val gameState =
        testGameState.copy(discardPile = List(card1, card2, card3, card4))

      fileIO.save(gameState)
      val loadedState = fileIO.load().get

      loadedState.discardPile.size shouldBe 4
      loadedState.discardPile(0).suit shouldBe Suit.Hearts
      loadedState.discardPile(1).suit shouldBe Suit.Spades
      loadedState.discardPile(2).suit shouldBe Suit.Diamonds
      loadedState.discardPile(3).suit shouldBe Suit.Clubs
    }

    "stringToGamePhase creates GamePhase with handle method" in {
      val gameState = testGameState.copy(discardPile = List(card1, card2))

      fileIO.save(gameState)
      val loadedState = fileIO.load().get

      val handledState = loadedState.gamePhase.handle(loadedState)
      handledState shouldBe loadedState
    }

    "handle all GamePhase types" in {
      import java.io.{File, PrintWriter}
      import scala.xml.XML

      val phaseTestCases = List(
        "SetupPhase",
        "SetupPhaseImpl",
        "AskPlayerCountPhase",
        "AskPlayerCountPhaseImpl",
        "AskPlayerNamesPhase",
        "AskPlayerNamesPhaseImpl",
        "AskDeckSizePhase",
        "AskDeckSizePhaseImpl",
        "GameStartPhase",
        "GameStartPhaseImpl",
        "AttackPhase",
        "AttackPhaseImpl",
        "DefensePhase",
        "DefensePhaseImpl",
        "DrawPhase",
        "DrawPhaseImpl",
        "RoundPhase",
        "RoundPhaseImpl",
        "EndPhase",
        "EndPhaseImpl",
        "AskPlayAgainPhase",
        "AskPlayAgainPhaseImpl"
      )

      phaseTestCases.foreach { phaseName =>
        val filename = s"test_phase_$phaseName.xml"
        val testFileIO = new FileIOXml(filename, stubGamePhases)

        val xmlContent = s"""<?xml version="1.0" encoding="UTF-8"?>
<gameState>
  <players>
    <player><name>Alice</name><hand></hand><isDone>false</isDone></player>
  </players>
  <mainAttackerIndex>0</mainAttackerIndex>
  <defenderIndex>0</defenderIndex>
  <currentAttackerIndex></currentAttackerIndex>
  <lastAttackerIndex></lastAttackerIndex>
  <passedPlayers></passedPlayers>
  <roundWinner></roundWinner>
  <deck></deck>
  <table></table>
  <discardPile></discardPile>
  <trumpCard><card><suit>Hearts</suit><rank>Ace</rank><isTrump>true</isTrump></card></trumpCard>
  <gamePhase>$phaseName</gamePhase>
  <lastEvent><none/></lastEvent>
  <setupPlayerCount></setupPlayerCount>
  <setupPlayerNames></setupPlayerNames>
  <setupDeckSize></setupDeckSize>
  <undoStack></undoStack>
  <redoStack></redoStack>
</gameState>"""

        val writer = new PrintWriter(filename)
        writer.write(xmlContent)
        writer.close()

        val loadedState = testFileIO.load().get
        loadedState.gamePhase should not be null

        new File(filename).delete()
      }
    }

    "handle GameState with undo and redo stacks" in {
      val nestedGameState1 = testGameState.copy(
        mainAttackerIndex = 1,
        undoStack = List.empty,
        redoStack = List.empty
      )
      val nestedGameState2 = testGameState.copy(
        defenderIndex = 0,
        undoStack = List.empty,
        redoStack = List.empty
      )

      val gameStateWithStacks = testGameState.copy(
        undoStack = List(nestedGameState1, nestedGameState2),
        redoStack = List(nestedGameState2, nestedGameState1)
      )

      fileIO.save(gameStateWithStacks)
      val loadedState = fileIO.load().get

      loadedState.undoStack.size shouldBe 2
      loadedState.undoStack(0).mainAttackerIndex shouldBe 1
      loadedState.undoStack(1).defenderIndex shouldBe 0

      loadedState.redoStack.size shouldBe 2
      loadedState.redoStack(0).defenderIndex shouldBe 0
      loadedState.redoStack(1).mainAttackerIndex shouldBe 1
    }

    "handle nested undo/redo stacks recursively" in {
      val deepNestedState = testGameState.copy(
        mainAttackerIndex = 2,
        undoStack = List.empty,
        redoStack = List.empty
      )

      val midNestedState = testGameState.copy(
        mainAttackerIndex = 1,
        undoStack = List(deepNestedState),
        redoStack = List.empty
      )

      val topGameState = testGameState.copy(
        mainAttackerIndex = 0,
        undoStack = List(midNestedState),
        redoStack = List.empty
      )

      fileIO.save(topGameState)
      val loadedState = fileIO.load().get

      loadedState.undoStack.size shouldBe 1
      loadedState.undoStack(0).undoStack.size shouldBe 1
      loadedState.undoStack(0).undoStack(0).mainAttackerIndex shouldBe 2
    }

    "return Failure when loading non-existent file" in {
      val nonExistentFileIO =
        new FileIOXml("nonexistent.xml", new StubGamePhasesImpl())
      val result = nonExistentFileIO.load()
      result shouldBe a[Failure[?]]
    }

    "cleanup test file" in {
      val file = new File(testFilePath)
      if (file.exists()) file.delete()
    }
  }
