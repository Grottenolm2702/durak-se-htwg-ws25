package de.htwg.DurakApp


import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.should.Matchers._


class RenderTUISpec extends AnyWordSpec with Matchers{
    "def renderCard" should {
        "render a card correctly" in {
            val card = Card(Suit.Hearts, Rank.Ace)
            val rendered = RenderTUI.renderCard(card)
            val expected = List(
            "+-----+",
            "|A    |",
            "|  \u001b[31m♥\u001b[0m  |",
            "|     |",
            "+-----+"
            )
            rendered shouldBe expected
        }
    }
    "def renderHand" should {
        "render a hand of cards correctly" in {
            val hand = List(
            Card(Suit.Hearts, Rank.Six),
            Card(Suit.Hearts, Rank.Seven),
            Card(Suit.Hearts, Rank.Eight),
            Card(Suit.Clubs, Rank.Nine),
            Card(Suit.Spades, Rank.Ten),
            Card(Suit.Clubs, Rank.Jack),
            Card(Suit.Hearts, Rank.Queen),
            Card(Suit.Diamonds, Rank.King)
            )
            val rendered = RenderTUI.renderHand(hand)
            val expected =
            "+-----+ +-----+ +-----+ +-----+ +-----+ +-----+ +-----+ +-----+\n" +
            "|6    | |7    | |8    | |9    | |10   | |J    | |Q    | |K    |\n" +
            "|  \u001b[31m♥\u001b[0m  | |  \u001b[31m♥\u001b[0m  | |  \u001b[31m♥\u001b[0m  | |  \u001b[32m♣\u001b[0m  | |  \u001b[32m♠\u001b[0m  | |  \u001b[32m♣\u001b[0m  | |  \u001b[31m♥\u001b[0m  | |  \u001b[31m♦\u001b[0m  |\n" +
            "|     | |     | |     | |     | |     | |     | |     | |     |\n" +
            "+-----+ +-----+ +-----+ +-----+ +-----+ +-----+ +-----+ +-----+"
            rendered shouldBe expected
        }
        "renderHand" should {
            "render an empty hand correctly" in {
                val hand = List()
                val rendered = RenderTUI.renderHand(hand)
                val expected = "Empty hand"
                rendered shouldBe expected
            }
        }
        "def renderTable" should {
            "render a table of cards correctly" in {
                val table = List(
                Card(Suit.Hearts, Rank.Six),
                Card(Suit.Clubs, Rank.Jack)
                )
                val rendered = RenderTUI.renderTable(table)
                val expected =
                "+-----+ +-----+\n" +
                "|6    | |J    |\n" +
                "|  \u001b[31m♥\u001b[0m  | |  \u001b[32m♣\u001b[0m  |\n" +
                "|     | |     |\n" +
                "+-----+ +-----+"
                rendered shouldBe expected
            }
        }
        "def renderGame" should {
            "render the entire game state correctly" in {
                val players = List(
                Player("Lucifer", List(Card(Suit.Hearts, Rank.Six))),
                Player("Michael", List(Card(Suit.Clubs, Rank.Jack)))
                )
                val table = List(
                Card(Suit.Spades, Rank.Ace)
                )
                val trump = Suit.Diamonds
                val gameState = GameState(players, List(), table, trump)
                val rendered = RenderTUI.renderGame(gameState)
                val expected =
                "Trump suit: Diamonds\n\n" +
                "Table:\n" +
                "+-----+\n" +
                "|A    |\n" +
                "|  \u001b[32m♠\u001b[0m  |\n" +
                "|     |\n" +
                "+-----+\n\n" +
                "Lucifer's hand:\n" +
                "+-----+\n" +
                "|6    |\n" +
                "|  \u001b[31m♥\u001b[0m  |\n" +
                "|     |\n" +
                "+-----+\n\n" +
                "Michael's hand:\n" +
                "+-----+\n" +
                "|J    |\n" +
                "|  \u001b[32m♣\u001b[0m  |\n" +
                "|     |\n" +
                "+-----+"
                rendered shouldBe expected
            }
        }
        "def renderGame" should {
            "render the entire game state with empty hands and table correctly" in {
                val players = List(
                Player("Lucifer", List()),
                Player("Michael", List())
                )
                val table = List()
                val trump = Suit.Diamonds
                val gameState = GameState(players, List(), table, trump)
                val rendered = RenderTUI.renderGame(gameState)
                val expected =
                "Trump suit: Diamonds\n\n" +
                "Table:\n" +
                "Table is empty\n\n" +
                "Lucifer's hand:\n" +
                "Empty hand\n\n" +
                "Michael's hand:\n" +
                "Empty hand"
                rendered shouldBe expected
            }
        }
    }
}
