package de.htwg.DurakApp.aview.tui.handler

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.controller._
import de.htwg.DurakApp.model.GameState
import de.htwg.DurakApp.testutil._

class FileIOHandlerSpec extends AnyWordSpec with Matchers {
  val gameStateBuilderFactory = new StubGameStateBuilderFactory()
  def createBuilder() = gameStateBuilderFactory.create()

  "FileIOHandler" should {
    "handle 'save' command" in {
      val handler = new FileIOHandler(None)
      val gameState = createBuilder().build().get

      val action = handler.handleRequest("save", gameState)

      action shouldBe SaveGameAction
    }

    "handle 'Save' command case-insensitively" in {
      val handler = new FileIOHandler(None)
      val gameState = createBuilder().build().get

      val action = handler.handleRequest("Save", gameState)

      action shouldBe SaveGameAction
    }

    "handle 'SAVE' command case-insensitively" in {
      val handler = new FileIOHandler(None)
      val gameState = createBuilder().build().get

      val action = handler.handleRequest("SAVE", gameState)

      action shouldBe SaveGameAction
    }

    "handle 's' shortcut" in {
      val handler = new FileIOHandler(None)
      val gameState = createBuilder().build().get

      val action = handler.handleRequest("s", gameState)

      action shouldBe SaveGameAction
    }

    "handle 'S' shortcut case-insensitively" in {
      val handler = new FileIOHandler(None)
      val gameState = createBuilder().build().get

      val action = handler.handleRequest("S", gameState)

      action shouldBe SaveGameAction
    }

    "handle 'load' command" in {
      val handler = new FileIOHandler(None)
      val gameState = createBuilder().build().get

      val action = handler.handleRequest("load", gameState)

      action shouldBe LoadGameAction
    }

    "handle 'Load' command case-insensitively" in {
      val handler = new FileIOHandler(None)
      val gameState = createBuilder().build().get

      val action = handler.handleRequest("Load", gameState)

      action shouldBe LoadGameAction
    }

    "handle 'LOAD' command case-insensitively" in {
      val handler = new FileIOHandler(None)
      val gameState = createBuilder().build().get

      val action = handler.handleRequest("LOAD", gameState)

      action shouldBe LoadGameAction
    }

    "handle 'l' shortcut" in {
      val handler = new FileIOHandler(None)
      val gameState = createBuilder().build().get

      val action = handler.handleRequest("l", gameState)

      action shouldBe LoadGameAction
    }

    "handle 'L' shortcut case-insensitively" in {
      val handler = new FileIOHandler(None)
      val gameState = createBuilder().build().get

      val action = handler.handleRequest("L", gameState)

      action shouldBe LoadGameAction
    }

    "pass unknown command to next handler" in {
      val nextHandler = new InvalidInputHandler(None)
      val handler = new FileIOHandler(Some(nextHandler))
      val gameState = createBuilder().build().get

      val action = handler.handleRequest("unknown", gameState)

      action shouldBe InvalidAction
    }

    "return InvalidAction when no next handler and unknown command" in {
      val handler = new FileIOHandler(None)
      val gameState = createBuilder().build().get

      val action = handler.handleRequest("unknown", gameState)

      action shouldBe InvalidAction
    }

    "handle 'save' with leading/trailing spaces" in {
      val handler = new FileIOHandler(None)
      val gameState = createBuilder().build().get

      val action = handler.handleRequest(" save ", gameState)

      action shouldBe InvalidAction
    }

    "not handle 'saves' (partial match)" in {
      val handler = new FileIOHandler(None)
      val gameState = createBuilder().build().get

      val action = handler.handleRequest("saves", gameState)

      action shouldBe InvalidAction
    }

    "not handle 'loading' (partial match)" in {
      val handler = new FileIOHandler(None)
      val gameState = createBuilder().build().get

      val action = handler.handleRequest("loading", gameState)

      action shouldBe InvalidAction
    }

    "chain with multiple handlers" in {
      val invalidHandler = new InvalidInputHandler(None)
      val passHandler = new PassHandler(Some(invalidHandler))
      val handler = new FileIOHandler(Some(passHandler))
      val gameState = createBuilder().build().get

      handler.handleRequest("save", gameState) shouldBe SaveGameAction
      handler.handleRequest("load", gameState) shouldBe LoadGameAction
      handler.handleRequest("pass", gameState) shouldBe PassAction
      handler.handleRequest("unknown", gameState) shouldBe InvalidAction
    }
  }
}
