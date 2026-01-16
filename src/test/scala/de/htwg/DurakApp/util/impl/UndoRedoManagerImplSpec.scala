package de.htwg.DurakApp.util.impl
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Suit, Rank}
import de.htwg.DurakApp.testutil._
import de.htwg.DurakApp.controller.command.CommandFactory
class UndoRedoManagerImplSpec extends AnyWordSpec with Matchers {
  private val commandFactory: CommandFactory = new StubCommandFactory()
  val player1 =
    TestHelper.Player("Alice", List(TestHelper.Card(Suit.Hearts, Rank.Six)))
  val player2 =
    TestHelper.Player("Bob", List(TestHelper.Card(Suit.Diamonds, Rank.Seven)))
  val trumpCard = TestHelper.Card(Suit.Clubs, Rank.Ace, isTrump = true)
  val gameState = TestHelper.GameState(
    players = List(player1, player2),
    deck = List.empty,
    table = Map.empty,
    discardPile = List.empty,
    trumpCard = trumpCard,
    mainAttackerIndex = 0,
    defenderIndex = 1,
    gamePhase = StubGamePhases.setupPhase,
    lastEvent = None,
    passedPlayers = Set.empty,
    roundWinner = None,
    setupPlayerCount = None,
    setupPlayerNames = List.empty,
    setupDeckSize = None,
    currentAttackerIndex = None,
    lastAttackerIndex = None
  )
  "UndoRedoManagerImpl" should {
    "create with empty stacks" in {
      val manager = UndoRedoManagerImpl(List.empty, List.empty)
      manager.undoStack shouldBe empty
      manager.redoStack shouldBe empty
    }
    "save command and state to undo stack" in {
      val manager = UndoRedoManagerImpl(List.empty, List.empty)
      val command = commandFactory.phaseChange()
      val updated = manager.save(command, gameState)
      updated.undoStack should have size 1
      updated.redoStack shouldBe empty
    }
    "clear redo stack when saving" in {
      val gameState2 = gameState.copy(mainAttackerIndex = 1)
      val manager = UndoRedoManagerImpl(
        List.empty,
        List((commandFactory.phaseChange(), gameState, gameState2))
      )
      val command = commandFactory.phaseChange()
      val updated = manager.save(command, gameState)
      updated.redoStack shouldBe empty
    }
    "undo command successfully" in {
      val command = commandFactory.phaseChange()
      val manager = UndoRedoManagerImpl(List((command, gameState)), List.empty)
      val currentState = gameState.copy(mainAttackerIndex = 1)
      val result = manager.undo(currentState)
      result shouldBe defined
      val (updatedManager, _) = result.get
      updatedManager.undoStack shouldBe empty
      updatedManager.redoStack should have size 1
    }
    "return None when undo stack is empty" in {
      val manager = UndoRedoManagerImpl(List.empty, List.empty)
      val result = manager.undo(gameState)
      result shouldBe None
    }
    "redo command successfully" in {
      val command = commandFactory.phaseChange()
      val gameState2 = gameState.copy(mainAttackerIndex = 1)
      val manager =
        UndoRedoManagerImpl(List.empty, List((command, gameState, gameState2)))
      val result = manager.redo(gameState)
      result shouldBe defined
      val (updatedManager, _) = result.get
      updatedManager.undoStack should have size 1
      updatedManager.redoStack shouldBe empty
    }
    "return None when redo stack is empty" in {
      val manager = UndoRedoManagerImpl(List.empty, List.empty)
      val result = manager.redo(gameState)
      result shouldBe None
    }
    "handle complex undo/redo sequence" in {
      val command1 = commandFactory.phaseChange()
      val command2 = commandFactory.phaseChange()
      val state1 = gameState
      val state2 = gameState.copy(mainAttackerIndex = 1)
      val state3 = gameState.copy(mainAttackerIndex = 2)
      val m1 = UndoRedoManagerImpl(List.empty, List.empty)
      val m2 = m1.save(command1, state1)
      val m3 = m2.save(command2, state2)
      m3.undoStack should have size 2
      m3.redoStack shouldBe empty
      val (m4, _) = m3.undo(state3).get
      m4.undoStack should have size 1
      m4.redoStack should have size 1
      val (m5, _) = m4.redo(state2).get
      m5.undoStack should have size 2
      m5.redoStack shouldBe empty
    }
    "preserve state correctly through undo" in {
      val command = commandFactory.phaseChange()
      val oldState = gameState
      val newState = gameState.copy(mainAttackerIndex = 1)
      val manager = UndoRedoManagerImpl(List((command, oldState)), List.empty)
      val (_, restoredState) = manager.undo(newState).get
      restoredState shouldBe oldState
    }
  }
}
