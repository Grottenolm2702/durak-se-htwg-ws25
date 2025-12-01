package de.htwg.DurakApp.util

import de.htwg.DurakApp.model.{Card, Rank, Suit, GameState}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.controller.command.GameCommand

class UndoRedoManagerSpec extends AnyWordSpec with Matchers {

  val initialState = GameState(
    Nil,
    Nil,
    Map.empty,
    Nil,
    Card(Suit.Clubs, Rank.Six),
    0,
    1,
    de.htwg.DurakApp.model.state.SetupPhase
  )
  val state1 = initialState.copy(attackerIndex = initialState.attackerIndex + 1)
  val state2 = state1.copy(attackerIndex = state1.attackerIndex + 1)
  val state3 = state2.copy(attackerIndex = state2.attackerIndex + 1)

  case class TestCommand(
      oldState: GameState,
      newState: GameState
  ) extends GameCommand {
    override def execute(gameState: GameState): GameState = {
      gameState should be(oldState)
      newState
    }
    override def undo(
        currentGameState: GameState,
        previousGameState: GameState
    ): GameState = {
      currentGameState should be(newState)
      previousGameState
    }
  }

  "An UndoRedoManager" should {

    "be empty initially" in {
      val manager = UndoRedoManager()
      manager.undoStack should be(Nil)
      manager.redoStack should be(Nil)
    }

    "save a state correctly" in {
      val manager = UndoRedoManager().save(
        TestCommand(initialState, state1),
        initialState
      )
      manager.undoStack.head._2 should be(initialState)
      manager.undoStack.head._1 shouldBe a[TestCommand]
      manager.redoStack should be(Nil)

      val manager2 = manager.save(TestCommand(state1, state2), state1)
      manager2.undoStack.head._2 should be(state1)
      manager2.undoStack.head._1 shouldBe a[TestCommand]
      manager2.undoStack(1)._2 should be(initialState)
      manager2.redoStack should be(Nil)
    }

    "undo a state correctly" in {
      val manager = UndoRedoManager()
        .save(TestCommand(initialState, state1), initialState)
        .save(TestCommand(state1, state2), state1)

      val (managerAfterUndo, undoneState) = manager.undo(state2).get
      undoneState should be(state1)
      managerAfterUndo.undoStack.head._2 should be(initialState)
      managerAfterUndo.redoStack.head._2 should be(state1)
      managerAfterUndo.redoStack.head._1 shouldBe a[TestCommand]
    }

    "undo multiple states correctly" in {
      val manager = UndoRedoManager()
        .save(TestCommand(initialState, state1), initialState)
        .save(TestCommand(state1, state2), state1)
        .save(TestCommand(state2, state3), state2)

      val (m1, s1) = manager.undo(state3).get
      s1 should be(state2)
      m1.undoStack.head._2 should be(state1)
      m1.redoStack.head._2 should be(state2)

      val (m2, s2) = m1.undo(s1).get
      s2 should be(state1)
      m2.undoStack.head._2 should be(initialState)
      m2.redoStack.head._2 should be(state1)
    }

    "return None when undoing with an empty undoStack" in {
      val manager = UndoRedoManager()
      manager.undo(initialState) should be(None)
    }

    "redo a state correctly" in {
      val manager = UndoRedoManager()
        .save(TestCommand(initialState, state1), initialState)
        .save(TestCommand(state1, state2), state1)

      val (managerAfterUndo, undoneState) = manager.undo(state2).get
      undoneState should be(state1)

      val (managerAfterRedo, redoneState) =
        managerAfterUndo.redo(undoneState).get
      redoneState should be(state2)
      managerAfterRedo.undoStack.head._2 should be(state1)
      managerAfterRedo.redoStack should be(Nil)
    }

    "redo multiple states correctly" in {
      val manager = UndoRedoManager()
        .save(TestCommand(initialState, state1), initialState)
        .save(TestCommand(state1, state2), state1)
        .save(TestCommand(state2, state3), state2)

      val (m1, s1) = manager.undo(state3).get
      val (m2, s2) = m1.undo(s1).get
      val (m3, s3) = m2.undo(s2).get
      s3 should be(initialState)

      val (m4, s4) = m3.redo(s3).get
      s4 should be(state1)
      m4.undoStack.head._2 should be(initialState)
      m4.redoStack.head._2 should be(state1)

      val (m5, s5) = m4.redo(s4).get
      s5 should be(state2)
      m5.undoStack.head._2 should be(state1)
      m5.redoStack.head._2 should be(state2)
    }

    "return None when redoing with an empty redoStack" in {
      val manager = UndoRedoManager().save(
        TestCommand(initialState, state1),
        initialState
      )
      manager.redo(state1) should be(None)
    }

    "clear redo stack on new save" in {
      val manager = UndoRedoManager()
        .save(TestCommand(initialState, state1), initialState)
        .save(TestCommand(state1, state2), state1)

      val (managerAfterUndo, _) = manager.undo(state2).get
      val managerAfterNewSave =
        managerAfterUndo.save(TestCommand(state1, state3), state1)
      managerAfterNewSave.undoStack.head._2 should be(state1)
      managerAfterNewSave.redoStack should be(Nil)
    }

    "handle initial state correctly with undo" in {
      val manager = UndoRedoManager().save(
        TestCommand(initialState, state1),
        initialState
      )
      val (m1, s1) = manager.undo(state1).get
      s1 should be(initialState)
      m1.undo(initialState) should be(None)
    }

    "return the correct manager instance after operations" in {
      val initialManager = UndoRedoManager()
      val manager1 =
        initialManager.save(TestCommand(initialState, state1), initialState)
      val manager2 = manager1.save(TestCommand(state1, state2), state1)

      val (managerAfterUndo, _) = manager2.undo(state2).get
      managerAfterUndo should not be theSameInstanceAs(manager2)

      val (managerAfterRedo, _) = managerAfterUndo.redo(state1).get
      managerAfterRedo should not be theSameInstanceAs(managerAfterUndo)
      managerAfterRedo.undoStack.head._2 should be(state1)
    }
  }
}
