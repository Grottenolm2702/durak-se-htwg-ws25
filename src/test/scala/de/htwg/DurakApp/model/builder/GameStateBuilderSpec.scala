package de.htwg.DurakApp.model.builder

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.ModelInterface._
import de.htwg.DurakApp.model.ModelInterface.StateInterface._

class GameStateBuilderSpec extends AnyWordSpec with Matchers {
  "GameStateBuilder" should {
    "build a default game state" in {
      val gameState = GameStateBuilder().build()

      gameState.players shouldBe empty
      gameState.deck shouldBe empty
      gameState.gamePhase shouldBe SetupPhase
    }
  }
}
