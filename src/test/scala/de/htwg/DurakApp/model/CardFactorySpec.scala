package de.htwg.DurakApp.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.impl.CardFactoryImpl

class CardFactorySpec extends AnyWordSpec with Matchers {
  
  "CardFactory" should {
    val cardFactory: CardFactory = new CardFactoryImpl()
    
    "create a card with suit and rank" in {
      val card = cardFactory(Suit.Hearts, Rank.Six)
      card.suit shouldBe Suit.Hearts
      card.rank shouldBe Rank.Six
      card.isTrump shouldBe false
    }
    
    "create a trump card" in {
      val card = cardFactory(Suit.Diamonds, Rank.Ace, isTrump = true)
      card.suit shouldBe Suit.Diamonds
      card.rank shouldBe Rank.Ace
      card.isTrump shouldBe true
    }
  }
}
