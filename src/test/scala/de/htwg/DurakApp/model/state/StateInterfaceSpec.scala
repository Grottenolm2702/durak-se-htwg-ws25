package de.htwg.DurakApp.model.state

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.ModelInterface.StateInterface._

class StateInterfaceSpec extends AnyWordSpec with Matchers {
  "StateInterface setup phases" should {
    "provide SetupPhase" in {
      SetupPhase shouldBe a[GamePhase]
      SetupPhase.toString should not be empty
    }
    
    "provide AskPlayerCountPhase" in {
      AskPlayerCountPhase shouldBe a[GamePhase]
    }
    
    "provide AskPlayerNamesPhase" in {
      AskPlayerNamesPhase shouldBe a[GamePhase]
    }
    
    "provide AskDeckSizePhase" in {
      AskDeckSizePhase shouldBe a[GamePhase]
    }
    
    "provide GameStartPhase" in {
      GameStartPhase shouldBe a[GamePhase]
    }
    
    "provide AskPlayAgainPhase" in {
      AskPlayAgainPhase shouldBe a[GamePhase]
    }
  }
  
  "StateInterface main game phases" should {
    "provide AttackPhase" in {
      AttackPhase shouldBe a[GamePhase]
      AttackPhase.toString should not be empty
    }
    
    "provide DefensePhase" in {
      DefensePhase shouldBe a[GamePhase]
    }
    
    "provide DrawPhase" in {
      DrawPhase shouldBe a[GamePhase]
    }
    
    "provide RoundPhase" in {
      RoundPhase shouldBe a[GamePhase]
    }
    
    "provide EndPhase" in {
      EndPhase shouldBe a[GamePhase]
    }
  }
  
  "StateInterface phases" should {
    "be distinct objects" in {
      AttackPhase should not be DefensePhase
      SetupPhase should not be EndPhase
      DrawPhase should not be RoundPhase
    }
    
    "be singletons" in {
      val phase1 = AttackPhase
      val phase2 = AttackPhase
      
      phase1 shouldBe phase2
      phase1 shouldBe theSameInstanceAs(phase2)
    }
  }
}
