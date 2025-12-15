package de.htwg.DurakApp.model.impl

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.ModelInterface._

class PlayerImplSpec extends AnyWordSpec with Matchers {
  "PlayerImpl" should {
    "be created through Player factory" in {
      val player = Player("Alice", List(Card(Suit.Hearts, Rank.Ace)))

      player.name shouldBe "Alice"
      player.hand should have size 1
    }
  }
}
