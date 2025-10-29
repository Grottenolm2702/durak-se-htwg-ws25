package de.htwg.DurakApp

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class DurakAppSpec extends AnyWordSpec {

  import DurakApp._

  "A Card" should {
    "store its suit and rank correctly" in {
      val card = Card(Suit.Hearts, Rank.Ace)
      card.suit shouldBe Suit.Hearts
      card.rank shouldBe Rank.Ace
    }
  }
}
