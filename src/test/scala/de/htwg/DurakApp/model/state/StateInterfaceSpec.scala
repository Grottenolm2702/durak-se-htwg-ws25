package de.htwg.DurakApp.model.state

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.ModelInterface.StateInterface._

class StateInterfaceSpec extends AnyWordSpec with Matchers {
  "StateInterface" should {
    "provide access to game phases" in {
      SetupPhase shouldBe a[GamePhase]
      AttackPhase shouldBe a[GamePhase]
    }
  }
}
