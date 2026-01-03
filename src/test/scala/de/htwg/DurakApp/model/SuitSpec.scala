package de.htwg.DurakApp.model

import de.htwg.DurakApp.testutil.TestHelpers._

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class SuitSpec extends AnyWordSpec with Matchers {
  "Suit enum" should {
    "contain exactly four suits" in {
      Suit.values should have size 4
    }

    "contain Hearts, Diamonds, Clubs and Spades" in {
      Suit.values should contain allOf (
        Suit.Hearts, Suit.Diamonds, Suit.Clubs, Suit.Spades
      )
    }

    "support equality comparison" in {
      Suit.Hearts shouldBe Suit.Hearts
      Suit.Hearts should not be Suit.Diamonds
      Suit.Clubs shouldBe Suit.Clubs
      Suit.Spades should not be Suit.Clubs
    }

    "have non-empty string representation" in {
      Suit.Hearts.toString should not be empty
      Suit.Diamonds.toString should not be empty
      Suit.Clubs.toString should not be empty
      Suit.Spades.toString should not be empty
    }

    "be usable in Cards" in {
      val heartsCard = Card(Suit.Hearts, Rank.Ace)
      val spadesCard = Card(Suit.Spades, Rank.King)

      heartsCard.suit shouldBe Suit.Hearts
      spadesCard.suit shouldBe Suit.Spades
    }
  }
}
