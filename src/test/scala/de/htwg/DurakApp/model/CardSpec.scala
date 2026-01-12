package de.htwg.DurakApp.model
import de.htwg.DurakApp.testutil._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
class CardSpec extends AnyWordSpec with Matchers {
  "A Card" should {
    "store its suit, rank and trumpBool correctly" in {
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace, isTrump = true)
      card.suit.shouldBe(Suit.Hearts)
      card.rank.shouldBe(Rank.Ace)
    }
    "use default isTrump value of false" in {
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace)
      card.isTrump shouldBe false
    }
    "support copy with default isTrump" in {
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace, isTrump = true)
      val copied = card.copy(suit = Suit.Diamonds)
      copied.suit shouldBe Suit.Diamonds
      copied.rank shouldBe Rank.Ace
      copied.isTrump shouldBe true
    }
    "use default parameter isTrump=false when creating with suit and rank" in {
      val card = TestHelper.Card(Suit.Spades, Rank.King)
      card.suit shouldBe Suit.Spades
      card.rank shouldBe Rank.King
      card.isTrump shouldBe false
    }
  }
  "Card copy method" should {
    val originalCard = TestHelper.Card(Suit.Hearts, Rank.Ace, isTrump = true)
    "copy with only suit changed" in {
      val copied = originalCard.copy(suit = Suit.Diamonds)
      copied.suit shouldBe Suit.Diamonds
      copied.rank shouldBe Rank.Ace
      copied.isTrump shouldBe true
    }
    "copy with only rank changed" in {
      val copied = originalCard.copy(rank = Rank.King)
      copied.suit shouldBe Suit.Hearts
      copied.rank shouldBe Rank.King
      copied.isTrump shouldBe true
    }
    "copy with only isTrump changed" in {
      val copied = originalCard.copy(isTrump = false)
      copied.suit shouldBe Suit.Hearts
      copied.rank shouldBe Rank.Ace
      copied.isTrump shouldBe false
    }
    "copy with suit and rank changed" in {
      val copied = originalCard.copy(suit = Suit.Clubs, rank = Rank.Six)
      copied.suit shouldBe Suit.Clubs
      copied.rank shouldBe Rank.Six
      copied.isTrump shouldBe true
    }
    "copy with suit and isTrump changed" in {
      val copied = originalCard.copy(suit = Suit.Spades, isTrump = false)
      copied.suit shouldBe Suit.Spades
      copied.rank shouldBe Rank.Ace
      copied.isTrump shouldBe false
    }
    "copy with rank and isTrump changed" in {
      val copied = originalCard.copy(rank = Rank.Seven, isTrump = false)
      copied.suit shouldBe Suit.Hearts
      copied.rank shouldBe Rank.Seven
      copied.isTrump shouldBe false
    }
    "copy with all parameters changed" in {
      val copied = originalCard.copy(
        suit = Suit.Diamonds,
        rank = Rank.Queen,
        isTrump = false
      )
      copied.suit shouldBe Suit.Diamonds
      copied.rank shouldBe Rank.Queen
      copied.isTrump shouldBe false
    }
    "copy with no parameters (returns same values)" in {
      val copied = originalCard.copy()
      copied.suit shouldBe originalCard.suit
      copied.rank shouldBe originalCard.rank
      copied.isTrump shouldBe originalCard.isTrump
    }
    "test copy default parameters via trait" in {
      val card: Card = TestHelper.Card(Suit.Clubs, Rank.Ten, isTrump = true)
      val copied1: Card = card.copy(suit = Suit.Diamonds)
      val copied2: Card = card.copy(rank = Rank.Jack)
      val copied3: Card = card.copy(isTrump = false)
      copied1.suit shouldBe Suit.Diamonds
      copied2.rank shouldBe Rank.Jack
      copied3.isTrump shouldBe false
    }
  }
  "Card constructor" should {
    "create a card with suit and rank" in {
      val card = Card(Suit.Hearts, Rank.Six)
      card.suit shouldBe Suit.Hearts
      card.rank shouldBe Rank.Six
      card.isTrump shouldBe false
    }
    "create a trump card" in {
      val card = Card(Suit.Diamonds, Rank.Ace, isTrump = true)
      card.suit shouldBe Suit.Diamonds
      card.rank shouldBe Rank.Ace
      card.isTrump shouldBe true
    }
    "use default isTrump=false when not specified" in {
      val card = Card(Suit.Hearts, Rank.Ace)
      card.isTrump shouldBe false
    }
    "respect explicit isTrump=true" in {
      val card = Card(Suit.Hearts, Rank.Ace, isTrump = true)
      card.isTrump shouldBe true
    }
    "respect explicit isTrump=false" in {
      val card = Card(Suit.Hearts, Rank.Ace, isTrump = false)
      card.isTrump shouldBe false
    }
  }
}
