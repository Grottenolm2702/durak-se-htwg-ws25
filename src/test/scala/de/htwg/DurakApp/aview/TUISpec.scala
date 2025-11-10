package de.htwg.DurakApp
package aview

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.controller.Controller
import model.Player
import model.GameState
import model.Suit
import model.Rank
import model.Card
import java.io.{ByteArrayInputStream, ByteArrayOutputStream, PrintStream}

class TUISpec extends AnyWordSpec with Matchers {

  "A TUI" should {
    val controller = new Controller()

    "allow attacker to choose a card" in {
      val tui = new TUI(controller)
      val attacker = Player("Lucifer")
      val game = GameState(Nil, Nil, Suit.Clubs)

      // Simuliere Benutzereingabe
      val input = "1\n"
      val inStream = new ByteArrayInputStream(input.getBytes)
      Console.withIn(inStream) {
        val outputStream = new ByteArrayOutputStream()
        Console.withOut(outputStream) {
          val choice = tui.chooseAttackCard(attacker, game)
          choice shouldBe "1"
          outputStream.toString should include("Lucifer, wähle Karte-Index")
        }
      }
    }

    "allow defender to choose a card" in {
      val tui = new TUI(controller)
      val defender = Player("Michael")
      val attackCard = Card(Suit.Hearts, Rank.Six, isTrump = false)
      val game = GameState(Nil, Nil, Suit.Clubs)

      val input = "take\n"
      val inStream = new ByteArrayInputStream(input.getBytes)
      Console.withIn(inStream) {
        val outputStream = new ByteArrayOutputStream()
        Console.withOut(outputStream) {
          val choice = tui.chooseDefenseCard(defender, attackCard, game)
          choice shouldBe "take"
          outputStream.toString should include("Michael, verteidige gegen")
        }
      }
    }

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
