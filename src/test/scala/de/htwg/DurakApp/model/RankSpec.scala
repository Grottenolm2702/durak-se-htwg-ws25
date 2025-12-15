package de.htwg.DurakApp.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.ModelInterface.*
import de.htwg.DurakApp.model.ModelInterface.StateInterface.*

class RankSpec extends AnyWordSpec with Matchers {

  "A Rank" should {
    "extend its value correctly" in {
      Rank.Six.value.shouldBe(6)
      Rank.Seven.value.shouldBe(7)
      Rank.Eight.value.shouldBe(8)
      Rank.Nine.value.shouldBe(9)
      Rank.Ten.value.shouldBe(10)
      Rank.Jack.value.shouldBe(11)
      Rank.Queen.value.shouldBe(12)
      Rank.King.value.shouldBe(13)
      Rank.Ace.value.shouldBe(14)
    }
  }
}
