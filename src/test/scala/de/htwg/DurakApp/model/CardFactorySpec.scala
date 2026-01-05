package de.htwg.DurakApp.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import com.google.inject.Guice

class CardFactorySpec extends AnyWordSpec with Matchers {

  // Use DI instead of direct instantiation
  private val injector = Guice.createInjector(new de.htwg.DurakApp.DurakModule)

  "CardFactory" should {
    val cardFactory: CardFactory = injector.getInstance(classOf[CardFactory])

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
