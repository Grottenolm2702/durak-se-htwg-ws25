package de.htwg.DurakApp

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class RenderTUISpec extends AnyWordSpec with Matchers {

  val heartAce = Card(Suit.Hearts, Rank.Ace, isTrump = true)
  val spadeSix = Card(Suit.Spades, Rank.Six, isTrump = false)
  val diamondTen = Card(Suit.Diamonds, Rank.Ten, isTrump = false)
  val clubKing = Card(Suit.Clubs, Rank.King, isTrump = false)

  "RenderTUI" should {

    "render all suit symbols correctly" in {
      val hearts =
        RenderTUI.renderCard(Card(Suit.Hearts, Rank.Six, false)).mkString
      val diamonds =
        RenderTUI.renderCard(Card(Suit.Diamonds, Rank.Seven, false)).mkString
      val clubs =
        RenderTUI.renderCard(Card(Suit.Clubs, Rank.Eight, false)).mkString
      val spades =
        RenderTUI.renderCard(Card(Suit.Spades, Rank.Nine, false)).mkString

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
        RenderTUI.renderCard(Card(Suit.Hearts, r, false)).mkString
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
      val lines = RenderTUI.renderCard(heartAce)
      lines.mkString("\n") should include("♥")
      lines.mkString should include("A")
      lines.length shouldBe 5
    }

    "render a single green spade card correctly" in {
      val lines = RenderTUI.renderCard(spadeSix)
      lines.mkString("\n") should include("♠")
      lines.mkString should include("6")
      lines.length shouldBe 5
    }

    "combine lines of multiple cards into a single string" in {
      val c1 = RenderTUI.renderCard(heartAce)
      val c2 = RenderTUI.renderCard(spadeSix)
      val combined = PrivateMethodTester.combine(c1, c2)
      combined should include("+-----+ +-----+")
    }

    "combineCardLines returns empty string when no cards given" in {
      val empty = PrivateMethodTester.combine()
      empty shouldBe ""
    }

    "render a hand with indices" in {
      val hand = List(heartAce, spadeSix, diamondTen, clubKing)
      val output = RenderTUI.renderHandWithIndices(hand)
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
      RenderTUI.renderHandWithIndices(Nil) should include("Empty hand")
    }

    "render empty table line" in {
      val output = RenderTUI.renderTableLine("Attacking", Nil)
      output should include("Empty")
      output should include("Attacking")
    }

    "render table line with cards" in {
      val output = RenderTUI.renderTableLine("Defending", List(heartAce))
      output should include("Defending")
      output should include("A")
      output should include("♥")
    }

    "render screen with non-empty status" in {
      val p1 = Player("Lucifer", List(heartAce, spadeSix))
      val p2 = Player("Michael", List(diamondTen, clubKing), isDone = true)
      val game = GameState(
        playerList = List(p1, p2),
        deck = List(heartAce),
        trump = Suit.Clubs,
        attackingCards = List(spadeSix),
        defendingCards = List(diamondTen),
        discardPile = List(heartAce)
      )
      val output = RenderTUI.renderScreen(game, "Fight!")
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
      val game = GameState(List(Player("Solo")), List(heartAce), Suit.Hearts)
      val output = RenderTUI.renderScreen(game, "")
      output should include("Status: ready")
    }
    "render empty table and return correct string on clearAndRender" in {
      val player1 = Player(
        "Ronny",
        List(
          Card(Suit.Hearts, Rank.Ace, false),
          Card(Suit.Diamonds, Rank.Seven, false)
        )
      )
      val player2 = Player("Bot", List(Card(Suit.Spades, Rank.Six, false)))
      val game =
        GameState(List(player1, player2), Nil, Suit.Hearts, List(), List())

      val output = RenderTUI.clearAndRender(game, "Ready")

      // Check that rendering includes key information
      output should include("Trump: Hearts")
      output should include("Ronny")
      output should include("Bot")
      output should include("Ready")

      // Table empty indicator
      output should include("Empty")
    }

    "render with non-empty table and return correct string" in {
      val card1 = Card(Suit.Clubs, Rank.King, false)
      val card2 = Card(Suit.Spades, Rank.King, false)
      val player1 = Player("Ronny", List(Card(Suit.Hearts, Rank.Queen, false)))
      val player2 = Player("Bot", List(Card(Suit.Diamonds, Rank.Jack, false)))
      val game = GameState(
        playerList = List(player1, player2),
        deck = Nil,
        trump = Suit.Spades,
        attackingCards = List(card1),
        defendingCards = List(card2),
        discardPile = Nil
      )

      val output = RenderTUI.clearAndRender(game, "Fighting")

      output should include("Trump: Spades")
      output should include("Ronny")
      output should include("Bot")
      output should include("Fighting")
      output should include("♣")
      output should include("♠")
    }

  }

  // Helper to access private combineCardLines
  private object PrivateMethodTester {
    def combine(cards: List[String]*): String = {
      val method = classOf[RenderTUI.type].getDeclaredMethods
        .find(_.getName.contains("combineCardLines"))
        .get
      method.setAccessible(true)
      method.invoke(RenderTUI, cards.toList).asInstanceOf[String]
    }
  }
}
