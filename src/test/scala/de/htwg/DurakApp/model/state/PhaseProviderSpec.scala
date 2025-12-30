package de.htwg.DurakApp.model.state

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class PhaseProviderSpec extends AnyWordSpec with Matchers {

  "PhaseProvider" should {
    "provide SetupPhase" in {
      val phase = PhaseProvider.setupPhase
      
      phase shouldBe SetupPhase
    }
    
    "provide AskPlayerCountPhase" in {
      val phase = PhaseProvider.askPlayerCountPhase
      
      phase shouldBe AskPlayerCountPhase
    }
    
    "provide AskPlayerNamesPhase" in {
      val phase = PhaseProvider.askPlayerNamesPhase
      
      phase shouldBe AskPlayerNamesPhase
    }
    
    "provide AskDeckSizePhase" in {
      val phase = PhaseProvider.askDeckSizePhase
      
      phase shouldBe AskDeckSizePhase
    }
    
    "provide AskPlayAgainPhase" in {
      val phase = PhaseProvider.askPlayAgainPhase
      
      phase shouldBe AskPlayAgainPhase
    }
    
    "provide GameStartPhase" in {
      val phase = PhaseProvider.gameStartPhase
      
      phase shouldBe GameStartPhase
    }
    
    "provide AttackPhase" in {
      val phase = PhaseProvider.attackPhase
      
      phase shouldBe AttackPhase
    }
    
    "provide DefensePhase" in {
      val phase = PhaseProvider.defensePhase
      
      phase shouldBe DefensePhase
    }
    
    "provide DrawPhase" in {
      val phase = PhaseProvider.drawPhase
      
      phase shouldBe DrawPhase
    }
    
    "provide RoundPhase" in {
      val phase = PhaseProvider.roundPhase
      
      phase shouldBe RoundPhase
    }
    
    "provide EndPhase" in {
      val phase = PhaseProvider.endPhase
      
      phase shouldBe EndPhase
    }
  }
}
