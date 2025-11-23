package de.htwg.DurakApp
package aview

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.controller.Controller
import de.htwg.DurakApp.model._
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

class TUISpec extends AnyWordSpec with Matchers {

  "A TUI" should {
    val initialGame = GameState(playerList = Nil, deck = Nil, trump = Suit.Clubs)
    val controller = new Controller(initialGame)
    val tui = new TUI(controller)

    val heartAce = Card(Suit.Hearts, Rank.Ace, isTrump = true)
    val spadeSix = Card(Suit.Spades, Rank.Six, isTrump = false)
    val diamondTen = Card(Suit.Diamonds, Rank.Ten, isTrump = false)
    val clubKing = Card(Suit.Clubs, Rank.King, isTrump = false)


    "allow attacker to choose a card" in {
      val attacker = Player("Lucifer")
      val game = GameState(Nil, Nil, Suit.Clubs)

      val input = "1\n"
      val inStream = new ByteArrayInputStream(input.getBytes)
      Console.withIn(inStream) {
        val outputStream = new ByteArrayOutputStream()
        Console.withOut(outputStream) {
          val choice = tui.chooseAttackCard(attacker, game)
          choice shouldBe 1
          outputStream.toString should include("Lucifer, wähle Karte-Index")
        }
      }
    }

    "allow attacker to pass" in {
      val attacker = Player("Lucifer")
      val game = GameState(Nil, Nil, Suit.Clubs)

      val input = "pass\n"
      val inStream = new ByteArrayInputStream(input.getBytes)
      Console.withIn(inStream) {
        val outputStream = new ByteArrayOutputStream()
        Console.withOut(outputStream) {
          val choice = tui.chooseAttackCard(attacker, game)
          choice shouldBe -1
          outputStream.toString should include("Lucifer, wähle Karte-Index")
        }
      }
    }

    "allow defender to choose a card" in {
      val defender = Player("Michael")
      val attackCard = Card(Suit.Hearts, Rank.Six, isTrump = false)
      val game = GameState(Nil, Nil, Suit.Clubs)

      val input = "0\n"
      val inStream = new ByteArrayInputStream(input.getBytes)
      Console.withIn(inStream) {
        val outputStream = new ByteArrayOutputStream()
        Console.withOut(outputStream) {
          val choice = tui.chooseDefenseCard(defender, attackCard, game)
          choice shouldBe 0
          outputStream.toString should include("Michael, verteidige gegen")
        }
      }
    }

    "allow defender to take" in {
      val defender = Player("Michael")
      val attackCard = Card(Suit.Hearts, Rank.Six, isTrump = false)
      val game = GameState(Nil, Nil, Suit.Clubs)

      val input = "take\n"
      val inStream = new ByteArrayInputStream(input.getBytes)
      Console.withIn(inStream) {
        val outputStream = new ByteArrayOutputStream()
        Console.withOut(outputStream) {
          val choice = tui.chooseDefenseCard(defender, attackCard, game)
          choice shouldBe -1
          outputStream.toString should include("Michael, verteidige gegen")
        }
      }
    }

    "ask for deck size and use default" in {
      val input = ""
      val result = tui.askForDeckSize(() => input)
      result shouldBe 36
    }

    "askForDeckSize uses default readLine" in {
      val input = "42\n"

      // Wir legen readLine auf einen simulierten Input um
      val inStream = new ByteArrayInputStream(input.getBytes)
      Console.withIn(inStream) {
        val result = tui.askForDeckSize() // **ohne Lambda** → nutzt readLine
        result shouldBe 42
      }
    }

    "ask for deck size and use provided value" in {
      val input = "52"
      val result = tui.askForDeckSize(() => input)
      result shouldBe 52
    }

    "ask for player count and use default" in {
      val input = ""
      val result = tui.askForPlayerCount(() => input)
      result shouldBe 2
    }

    "askForPlayerCount uses default readLine" in {
      val input = "3\n"

      val inStream = new ByteArrayInputStream(input.getBytes)
      Console.withIn(inStream) {
        val result = tui.askForPlayerCount()
        result shouldBe 3
      }
    }

    "ask for player count and use provided value" in {
      val input = "4"
      val result = tui.askForPlayerCount(() => input)
      result shouldBe 4
    }

    "ask for player count and handle minimum" in {
      val input = "1"
      val result = tui.askForPlayerCount(() => input)
      result shouldBe 2
    }

    "ask for player names and use provided names" in {
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

    "askForPlayerNames uses default readLine" in {
      // Simulierte Eingaben für zwei Spieler
      val input = "Alice\nBob\n"
      val inStream = new ByteArrayInputStream(input.getBytes)

      Console.withIn(inStream) {
        val names = tui.askForPlayerNames(2) // **ohne Lambda** → nutzt readLine
        names shouldBe List("Alice", "Bob")
      }
    }

    "ask for player names and use default names for empty input" in {
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
      tui.clearScreen() shouldBe "\u001b[2J\u001b[H"
    }

    "update method should print to console" in {
      controller.add(tui)

      val stream = new ByteArrayOutputStream()
      Console.withOut(stream) {
        controller.notifyObservers
      }
      val output = stream.toString()
      output should include("Status: Willkommen bei Durak!")
      output should include("Trump: Clubs")
    }

    "render all suit symbols correctly" in {
      val hearts =
        tui.renderCard(Card(Suit.Hearts, Rank.Six, false)).mkString
      val diamonds =
        tui.renderCard(Card(Suit.Diamonds, Rank.Seven, false)).mkString
      val clubs =
        tui.renderCard(Card(Suit.Clubs, Rank.Eight, false)).mkString
      val spades =
        tui.renderCard(Card(Suit.Spades, Rank.Nine, false)).mkString

      hearts should include("♥")
      diamonds should include("♦")
      clubs should include("♣")
      spades should include("♠")
    }

    "render all ranks correctly" in {
      val allRanks = List(
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
      val rendered = allRanks.map(r =>
        tui.renderCard(Card(Suit.Hearts, r, false)).mkString
      )
      rendered.mkString should include("6")
      rendered.mkString should include("7")
      rendered.mkString should include("8")
      rendered.mkString should include("9")
      rendered.mkString should include("10")
      rendered.mkString should include("J")
      rendered.mkString should include("Q")
      rendered.mkString should include("K")
      rendered.mkString should include("A")
    }

    "render a single red heart card correctly" in {
      val lines = tui.renderCard(heartAce)
      lines.mkString("\n") should include("♥")
      lines.mkString should include("A")
      lines.length shouldBe 5
    }

    "render a single green spade card correctly" in {
      val lines = tui.renderCard(spadeSix)
      lines.mkString("\n") should include("♠")
      lines.mkString should include("6")
      lines.length shouldBe 5
    }

    "combine lines of multiple cards into a single string" in {
      val c1 = tui.renderCard(heartAce)
      val c2 = tui.renderCard(spadeSix)
      val combined = PrivateMethodTester.combine(c1, c2)
      combined should include("+-----+ +-----+")
    }

    "combineCardLines returns empty string when no cards given" in {
      val empty = PrivateMethodTester.combine()
      empty shouldBe ""
    }

    "render a hand with indices" in {
      val hand = List(heartAce, spadeSix, diamondTen, clubKing)
      val output = tui.renderHandWithIndices(hand)
      output should include("A")
      output should include("6")
      output should include("10")
      output should include("K")
      output should include("0")
      output should include("1")
      output should include("2")
      output should include("3")
    }

    "render empty hand as 'Empty hand'" in {
      tui.renderHandWithIndices(Nil) should include("Empty hand")
    }

    "render empty table line" in {
      val output = tui.renderTableLine("Attacking", Nil)
      output should include("Empty")
      output should include("Attacking")
    }

    "render table line with cards" in {
      val output = tui.renderTableLine("Defending", List(heartAce))
      output should include("Defending")
      output should include("A")
      output should include("♥")
    }

    "render screen with non-empty status" in {
      val p1 = Player("Lucifer", List(heartAce, spadeSix), false)
      val p2 = Player("Michael", List(diamondTen, clubKing), isDone = true)
      val game = GameState(
        playerList = List(p1, p2),
        deck = List(heartAce),
        trump = Suit.Clubs,
        attackingCards = List(spadeSix),
        defendingCards = List(diamondTen),
        discardPile = List(heartAce)
      )
      val output = tui.renderScreen(game, "Fight!")
      output should include("Trump")
      output should include("Deck")
      output should include("Discard")
      output should include("Attacking")
      output should include("Defending")
      output should include("Lucifer")
      output should include("Michael")
      output should include("Fight!")
    }

    "render screen with empty status as 'Status: ready'" in {
      val game = GameState(List(Player("Solo", List(), false)), List(heartAce), Suit.Hearts)
      val output = tui.renderScreen(game, "")
      output should include("Status: ready")
    }

    "render screen with null status as 'Status: ready'" in {
      val game = GameState(List(Player("Solo", List(), false)), List(heartAce), Suit.Hearts)
      val output = tui.renderScreen(game, null)
      output should include("Status: ready")
    }

    "cardShortString: return short string for card (includes trump tag)" in {
      val s = tui.cardShortString(heartAce)
      s should include("Ace")
      s should include("Hearts")
      s should include("(T)")
    }

    "cardShortString: return short string for card (excludes trump tag)" in {
      val s = tui.cardShortString(spadeSix)
      s should include("Six")
      s should include("Spades")
      s should not include("(T)")
    }
  }

  // Helper to access private combineCardLines
  private object PrivateMethodTester {
    val initialGame = GameState(playerList = Nil, deck = Nil, trump = Suit.Clubs)
    val controller = new Controller(initialGame)
    val tui = new TUI(controller)
    def combine(cards: List[String]*): String = {
      val method = classOf[TUI].getDeclaredMethods
        .find(_.getName.contains("combineCardLines"))
        .get
      method.setAccessible(true)
      method.invoke(tui, cards.toList).asInstanceOf[String]
    }
  }
}
