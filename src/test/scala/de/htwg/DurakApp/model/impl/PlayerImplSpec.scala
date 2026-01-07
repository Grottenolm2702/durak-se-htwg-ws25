package de.htwg.DurakApp.model.impl

import de.htwg.DurakApp.testutil._

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Player, Card, Suit, Rank}

class PlayerImplSpec extends AnyWordSpec with Matchers {
  "PlayerImpl" should {
    "be created through Player factory" in {
      val player = TestHelper.Player("Alice", List(TestHelper.Card(Suit.Hearts, Rank.Ace)))

      player.name shouldBe "Alice"
      player.hand should have size 1
    }
  }
}
