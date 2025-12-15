package de.htwg.DurakApp.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.ModelInterface.*
import de.htwg.DurakApp.model.ModelInterface.StateInterface.*

class CardSpec extends AnyWordSpec with Matchers {

  "A Card" should {
    "store its suit, rank and trumpBool correctly" in {
      val card = Card(Suit.Hearts, Rank.Ace, isTrump = true)
      card.suit.shouldBe(Suit.Hearts)
      card.rank.shouldBe(Rank.Ace)
    }

    "use default isTrump value of false" in {
      val card = Card(Suit.Hearts, Rank.Ace)
      card.isTrump shouldBe false
    }

    "support copy with default isTrump" in {
      val card = Card(Suit.Hearts, Rank.Ace, isTrump = true)
      val copied = card.copy(suit = Suit.Diamonds)
      copied.suit shouldBe Suit.Diamonds
      copied.rank shouldBe Rank.Ace
      copied.isTrump shouldBe true
    }

    "use default parameter isTrump=false when creating with suit and rank" in {
      val card = Card(Suit.Spades, Rank.King)
      card.suit shouldBe Suit.Spades
      card.rank shouldBe Rank.King
      card.isTrump shouldBe false
    }
  }
}
