package de.htwg.DurakApp.util.impl

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.util.UndoRedoManager

class UndoRedoManagerImplSpec extends AnyWordSpec with Matchers {
  "UndoRedoManagerImpl" should {
    "be created through UndoRedoManager factory" in {
      val manager = UndoRedoManager()
      manager shouldBe a[UndoRedoManager]
    }
  }
}
