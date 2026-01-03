package de.htwg.DurakApp.model.impl

import de.htwg.DurakApp.testutil.TestHelpers._

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Suit, Rank}

class CardImplSpec extends AnyWordSpec with Matchers {
  "CardImpl" should {
    "be created through Card factory" in {
      val card = Card(Suit.Hearts, Rank.Ace)

      card.suit shouldBe Suit.Hearts
      card.rank shouldBe Rank.Ace
      card.isTrump shouldBe false
    }

    "support trump flag" in {
      val card = Card(Suit.Hearts, Rank.Ace, isTrump = true)

      card.isTrump shouldBe true
    }
  }
}
