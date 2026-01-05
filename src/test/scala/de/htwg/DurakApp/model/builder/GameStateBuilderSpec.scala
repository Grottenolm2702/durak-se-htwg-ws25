package de.htwg.DurakApp.model.builder

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.testutil.{TestGamePhases, TestGamePhasesInstance}
import de.htwg.DurakApp.testutil.TestFactories
import com.google.inject.Guice

class GameStateBuilderSpec extends AnyWordSpec with Matchers {

  // Use DI instead of direct instantiation
  private val injector = Guice.createInjector(new de.htwg.DurakApp.DurakModule)
  private val builderFactory: GameStateBuilderFactory =
    injector.getInstance(classOf[GameStateBuilderFactory])

  "GameStateBuilder" should {
    "build a default game state" in {
      val gameState = builderFactory.create().build()

      gameState.players shouldBe empty
      gameState.deck shouldBe empty
      gameState.gamePhase shouldBe TestGamePhases.setupPhase
    }
  }
}
