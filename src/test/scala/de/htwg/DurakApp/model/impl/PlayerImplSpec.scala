package de.htwg.DurakApp.model.impl

import de.htwg.DurakApp.testutil.TestHelpers._

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Player, Card, Suit, Rank}

class PlayerImplSpec extends AnyWordSpec with Matchers {
  "PlayerImpl" should {
    "be created through Player factory" in {
      val player = Player("Alice", List(Card(Suit.Hearts, Rank.Ace)))

      player.name shouldBe "Alice"
      player.hand should have size 1
    }
  }
}
