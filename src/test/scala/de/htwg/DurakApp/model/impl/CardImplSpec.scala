package de.htwg.DurakApp.model.impl
import de.htwg.DurakApp.testutil._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Suit, Rank}
class CardImplSpec extends AnyWordSpec with Matchers {
  "CardImpl" should {
    "be created through Card factory" in {
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace)
      card.suit shouldBe Suit.Hearts
      card.rank shouldBe Rank.Ace
      card.isTrump shouldBe false
    }
    "support trump flag" in {
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace, isTrump = true)
      card.isTrump shouldBe true
    }
    "support copy with suit change" in {
      val card = CardImpl(Suit.Hearts, Rank.Ace, isTrump = false)
      val copied = card.copy(suit = Suit.Diamonds)
      copied.suit shouldBe Suit.Diamonds
      copied.rank shouldBe Rank.Ace
      copied.isTrump shouldBe false
    }
    "support copy with rank change" in {
      val card = CardImpl(Suit.Hearts, Rank.Ace, isTrump = false)
      val copied = card.copy(rank = Rank.King)
      copied.suit shouldBe Suit.Hearts
      copied.rank shouldBe Rank.King
      copied.isTrump shouldBe false
    }
    "support copy with isTrump change" in {
      val card = CardImpl(Suit.Hearts, Rank.Ace, isTrump = false)
      val copied = card.copy(isTrump = true)
      copied.suit shouldBe Suit.Hearts
      copied.rank shouldBe Rank.Ace
      copied.isTrump shouldBe true
    }
    "support copy with no parameters" in {
      val card = CardImpl(Suit.Hearts, Rank.Ace, isTrump = true)
      val copied = card.copy()
      copied.suit shouldBe card.suit
      copied.rank shouldBe card.rank
      copied.isTrump shouldBe card.isTrump
    }
  }
}
