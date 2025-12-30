package de.htwg.DurakApp.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class PlayerSpec extends AnyWordSpec with Matchers {

  "A Player" should {
    "store its name and hand correctly" in {
      val hand = List(
        Card(Suit.Hearts, Rank.Six, isTrump = false),
        Card(Suit.Clubs, Rank.Jack, isTrump = true)
      )
      val player = Player("Lucifer", hand, false)
      player.name.shouldBe("Lucifer")
      player.hand.shouldBe(hand)
    }
    "initialize hand and isDone correctly with minimal parameters" in {
      val player = Player("Michael")
      player.name.shouldBe("Michael")
      player.hand.shouldBe(List())
      player.isDone.shouldBe(false)
    }

    "support copy with default isDone" in {
      val hand = List(Card(Suit.Hearts, Rank.Six))
      val player = Player("Gabriel", hand, isDone = true)
      val copied = player.copy(name = "Raphael")
      copied.name shouldBe "Raphael"
      copied.hand shouldBe hand
      copied.isDone shouldBe true
    }

    "use default parameter for hand when creating with only name" in {
      val player = Player("Solo")
      player.name shouldBe "Solo"
      player.hand shouldBe List.empty
      player.isDone shouldBe false
    }

    "use default parameter for isDone when creating with name and hand" in {
      val hand = List(Card(Suit.Hearts, Rank.Six))
      val player = Player("Hero", hand)
      player.name shouldBe "Hero"
      player.hand shouldBe hand
      player.isDone shouldBe false
    }
  }
}
