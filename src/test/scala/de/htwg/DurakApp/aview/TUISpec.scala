package de.htwg.DurakApp.aview

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.controller.Controller
import java.io.{ByteArrayInputStream, ByteArrayOutputStream, PrintStream}

class TUISpec extends AnyWordSpec with Matchers {

  "A TUI" should {
    val controller = new Controller()
    
    "ask for deck size and use default" in {
      val tui = new TUI(controller)
      val input = ""
      val result = tui.askForDeckSize(() => input)
      result shouldBe 36
    }

    "ask for deck size and use provided value" in {
      val tui = new TUI(controller)
      val input = "52"
      val result = tui.askForDeckSize(() => input)
      result shouldBe 52
    }

    "ask for player count and use default" in {
      val tui = new TUI(controller)
      val input = ""
      val result = tui.askForPlayerCount(() => input)
      result shouldBe 2
    }

    "ask for player count and use provided value" in {
      val tui = new TUI(controller)
      val input = "4"
      val result = tui.askForPlayerCount(() => input)
      result shouldBe 4
    }

    "ask for player count and handle minimum" in {
      val tui = new TUI(controller)
      val input = "1"
      val result = tui.askForPlayerCount(() => input)
      result shouldBe 2
    }

    "ask for player names and use provided names" in {
      val tui = new TUI(controller)
      val inputs = List("Alice", "Bob")
      var remainingInputs = inputs
      val inputReader = () => {
        val head = remainingInputs.head
        remainingInputs = remainingInputs.tail
        head
      }
      val names = tui.askForPlayerNames(2, inputReader)
      names shouldBe List("Alice", "Bob")
    }

    "ask for player names and use default names for empty input" in {
      val tui = new TUI(controller)
      val inputs = List("", "  ")
      var remainingInputs = inputs
      val inputReader = () => {
        val head = remainingInputs.head
        remainingInputs = remainingInputs.tail
        head
      }
      val names = tui.askForPlayerNames(2, inputReader)
      names shouldBe List("Player1", "Player2")
    }

    "clear the screen" in {
      val tui = new TUI(controller)
      tui.clearScreen() shouldBe "\u001b[2J\u001b[H"
    }

    "update method should print to console" in {
        val controller = new Controller()
        val tui = new TUI(controller)
        controller.add(tui)

        val stream = new ByteArrayOutputStream()
        Console.withOut(stream) {
            controller.notifyObservers // This will trigger tui.update
        }
        val output = stream.toString()
        output should include("Status: ready")
        output should include("Trump: Clubs")
    }
  }
}
