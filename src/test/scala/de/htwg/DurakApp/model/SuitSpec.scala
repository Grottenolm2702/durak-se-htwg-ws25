package de.htwg.DurakApp.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.ModelInterface._

class SuitSpec extends AnyWordSpec with Matchers {
  "Suit" should {
    "have all 4 suits" in {
      Suit.values should have size 4
    }
  }
}
