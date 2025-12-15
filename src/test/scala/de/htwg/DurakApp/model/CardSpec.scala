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
  }
}
