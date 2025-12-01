package de.htwg.DurakApp.util

import de.htwg.DurakApp.model.{Card, Rank, Suit, GameState}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class ImmutableUndoRedoManagerSpec extends AnyWordSpec with Matchers {

  "An ImmutableUndoRedoManager" should {

    val initialState = GameState(Nil, Nil, Map.empty, Nil, Card(Suit.Clubs, Rank.Six), 0, 1, de.htwg.DurakApp.model.state.SetupPhase)
    val state1 = GameState(Nil, Nil, Map.empty, Nil, Card(Suit.Clubs, Rank.Seven), 1, 2, de.htwg.DurakApp.model.state.SetupPhase)
    val state2 = GameState(Nil, Nil, Map.empty, Nil, Card(Suit.Clubs, Rank.Eight), 2, 3, de.htwg.DurakApp.model.state.SetupPhase)
    val state3 = GameState(Nil, Nil, Map.empty, Nil, Card(Suit.Clubs, Rank.Nine), 3, 0, de.htwg.DurakApp.model.state.SetupPhase)

    "be empty initially" in {
      val manager = ImmutableUndoRedoManager()
      manager.undoStack should be(Nil)
      manager.redoStack should be(Nil)
    }

    "save a state correctly" in {
      val manager = ImmutableUndoRedoManager().save(initialState)
      manager.undoStack should be(List(initialState))
      manager.redoStack should be(Nil)

      val manager2 = manager.save(state1)
      manager2.undoStack should be(List(state1, initialState))
      manager2.redoStack should be(Nil)
    }

    "undo a state correctly" in {
      val manager = ImmutableUndoRedoManager().save(initialState).save(state1)
      val (managerAfterUndo, undoneState) = manager.undo.get
      undoneState should be(state1)
      managerAfterUndo.undoStack should be(List(initialState))
      managerAfterUndo.redoStack should be(List(state1))
    }

    "undo multiple states correctly" in {
      val manager = ImmutableUndoRedoManager().save(initialState).save(state1).save(state2)
      val (m1, s1) = manager.undo.get
      s1 should be(state2)
      m1.undoStack should be(List(state1, initialState))
      m1.redoStack should be(List(state2))

      val (m2, s2) = m1.undo.get
      s2 should be(state1)
      m2.undoStack should be(List(initialState))
      m2.redoStack should be(List(state1, state2))
    }

    "return None when undoing with an empty undoStack" in {
      val manager = ImmutableUndoRedoManager()
      manager.undo should be(None)
    }

    "redo a state correctly" in {
      val manager = ImmutableUndoRedoManager().save(initialState).save(state1)
      val (managerAfterUndo, _) = manager.undo.get

      val (managerAfterRedo, redoneState) = managerAfterUndo.redo.get
      redoneState should be(state1)
      managerAfterRedo.undoStack should be(List(state1, initialState))
      managerAfterRedo.redoStack should be(Nil)
    }

    "redo multiple states correctly" in {
      val manager = ImmutableUndoRedoManager().save(initialState).save(state1).save(state2)
      val (m1, _) = manager.undo.get
      val (m2, _) = m1.undo.get

      val (m3, s3) = m2.redo.get
      s3 should be(state1)
      m3.undoStack should be(List(state1, initialState))
      m3.redoStack should be(List(state2))

      val (m4, s4) = m3.redo.get
      s4 should be(state2)
      m4.undoStack should be(List(state2, state1, initialState))
      m4.redoStack should be(Nil)
    }

    "return None when redoing with an empty redoStack" in {
      val manager = ImmutableUndoRedoManager().save(initialState)
      manager.redo should be(None)
      val (managerAfterUndo, _) = manager.undo.get
      managerAfterUndo.redo.isDefined should be(true)
    }

    "clear redo stack on new save" in {
      val manager = ImmutableUndoRedoManager().save(initialState).save(state1)
      val (managerAfterUndo, _) = manager.undo.get
      val managerAfterNewSave = managerAfterUndo.save(state2)
      managerAfterNewSave.undoStack should be(List(state2, initialState))
      managerAfterNewSave.redoStack should be(Nil)
    }

    "handle initial state correctly with undo" in {
      val manager = ImmutableUndoRedoManager().save(initialState)
      val (m1, s1) = manager.undo.get
      s1 should be(initialState)
      m1.undoStack should be(Nil)
      m1.redoStack should be(List(initialState))

      m1.undo should be(None)
    }

    "return the correct manager instance after operations" in {
      val initialManager = ImmutableUndoRedoManager()
      val manager1 = initialManager.save(initialState)
      val manager2 = manager1.save(state1)

      val (managerAfterUndo, _) = manager2.undo.get
      managerAfterUndo should not be theSameInstanceAs(manager2)

      val (managerAfterRedo, _) = managerAfterUndo.redo.get
      managerAfterRedo should not be theSameInstanceAs(managerAfterUndo)
      managerAfterRedo.undoStack should be(List(state1, initialState))
    }
  }
}
