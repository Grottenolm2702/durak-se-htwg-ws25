package de.htwg.DurakApp.model.builder
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.testutil._
class GameStateBuilderSpec extends AnyWordSpec with Matchers {
  private val builderFactory = TestHelper.gameStateBuilderFactory
  "GameStateBuilder" should {
    "build a default game state" in {
      val gameState = builderFactory.create().build()
      gameState.players shouldBe empty
      gameState.deck shouldBe empty
      gameState.gamePhase shouldBe StubGamePhases.setupPhase
    }
  }
}
