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
      player.name shouldBe "Lucifer"
      player.hand shouldBe hand
    }
    "initialize hand and isDone correctly with minimal parameters" in {
      val player = Player("Michael")
      player.name shouldBe "Michael"
      player.hand shouldBe List()
      player.isDone shouldBe false
    }
  }
}
