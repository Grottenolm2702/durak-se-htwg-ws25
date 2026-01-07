package de.htwg.DurakApp.model.state

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.testutil.{StubGamePhases, StubGamePhasesImpl}

class GamePhasesSpec extends AnyWordSpec with Matchers {

  "GamePhases" should {

    val gamePhases = new StubGamePhasesImpl()

    "identify setup phase correctly" in {
      gamePhases.isSetupPhase(gamePhases.setupPhase) shouldBe true
      gamePhases.isSetupPhase(gamePhases.askPlayerCountPhase) shouldBe true
      gamePhases.isSetupPhase(gamePhases.attackPhase) shouldBe false
      gamePhases.isSetupPhase(gamePhases.defensePhase) shouldBe false
    }

    "identify ask player names phase correctly" in {
      gamePhases.isAskPlayerNamesPhase(gamePhases.askPlayerNamesPhase) shouldBe true
      gamePhases.isAskPlayerNamesPhase(gamePhases.setupPhase) shouldBe false
      gamePhases.isAskPlayerNamesPhase(gamePhases.attackPhase) shouldBe false
    }

    "identify ask deck size phase correctly" in {
      gamePhases.isAskDeckSizePhase(gamePhases.askDeckSizePhase) shouldBe true
      gamePhases.isAskDeckSizePhase(gamePhases.setupPhase) shouldBe false
      gamePhases.isAskDeckSizePhase(gamePhases.attackPhase) shouldBe false
    }

    "identify game start phase correctly" in {
      gamePhases.isGameStartPhase(gamePhases.gameStartPhase) shouldBe true
      gamePhases.isGameStartPhase(gamePhases.setupPhase) shouldBe false
      gamePhases.isGameStartPhase(gamePhases.attackPhase) shouldBe false
    }

    "identify ask play again phase correctly" in {
      gamePhases.isAskPlayAgainPhase(gamePhases.askPlayAgainPhase) shouldBe true
      gamePhases.isAskPlayAgainPhase(gamePhases.setupPhase) shouldBe false
      gamePhases.isAskPlayAgainPhase(gamePhases.endPhase) shouldBe false
    }

    "identify attack phase correctly" in {
      gamePhases.isAttackPhase(gamePhases.attackPhase) shouldBe true
      gamePhases.isAttackPhase(gamePhases.defensePhase) shouldBe false
      gamePhases.isAttackPhase(gamePhases.setupPhase) shouldBe false
    }

    "identify defense phase correctly" in {
      gamePhases.isDefensePhase(gamePhases.defensePhase) shouldBe true
      gamePhases.isDefensePhase(gamePhases.attackPhase) shouldBe false
      gamePhases.isDefensePhase(gamePhases.setupPhase) shouldBe false
    }

    "identify any setup phase correctly" in {
      gamePhases.isAnySetupPhase(gamePhases.setupPhase) shouldBe true
      gamePhases.isAnySetupPhase(gamePhases.askPlayerCountPhase) shouldBe true
      gamePhases.isAnySetupPhase(gamePhases.askPlayerNamesPhase) shouldBe true
      gamePhases.isAnySetupPhase(gamePhases.askDeckSizePhase) shouldBe true
      gamePhases.isAnySetupPhase(gamePhases.gameStartPhase) shouldBe true
      gamePhases.isAnySetupPhase(gamePhases.attackPhase) shouldBe false
      gamePhases.isAnySetupPhase(gamePhases.defensePhase) shouldBe false
      gamePhases.isAnySetupPhase(gamePhases.drawPhase) shouldBe false
      gamePhases.isAnySetupPhase(gamePhases.roundPhase) shouldBe false
      gamePhases.isAnySetupPhase(gamePhases.endPhase) shouldBe false
      gamePhases.isAnySetupPhase(gamePhases.askPlayAgainPhase) shouldBe false
    }
  }
}
