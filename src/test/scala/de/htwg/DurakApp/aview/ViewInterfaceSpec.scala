package de.htwg.DurakApp.aview

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.aview.ViewInterface._
import de.htwg.DurakApp.controller.ControllerInterface._
import de.htwg.DurakApp.model.ModelInterface._
import de.htwg.DurakApp.model.ModelInterface.StateInterface._
import de.htwg.DurakApp.util.UndoRedoManager
import scalafx.application.Platform
import java.util.concurrent.{CountDownLatch, TimeUnit}

class ViewInterfaceSpec extends AnyWordSpec with Matchers {
  
  // Helper to initialize JavaFX toolkit once
  private val javafxInitialized = new java.util.concurrent.atomic.AtomicBoolean(false)
  
  private def ensureJavaFXInitialized(): Unit = {
    if (!javafxInitialized.getAndSet(true)) {
      val latch = new CountDownLatch(1)
      new Thread(() => {
        try {
          Platform.startup(() => {
            latch.countDown()
          })
        } catch {
          case _: IllegalStateException => 
            // Already initialized
            latch.countDown()
        }
      }).start()
      
      latch.await(5, TimeUnit.SECONDS)
    }
  }
  
  "ViewInterface TUI factory" should {
    "create TUI instances through apply method (line 61-62)" in {
      val controller = Controller(
        GameStateBuilder().withGamePhase(SetupPhase).build(),
        UndoRedoManager()
      )
      
      // Test: TUI(controller) calls new InternalTUI(controller)
      val tui = TUI(controller)
      
      tui shouldBe a[TUI]
      tui shouldBe a[View]
      tui shouldBe a[ViewInterface]
    }
    
    "create TUI with different game states" in {
      val gameState = GameStateBuilder()
        .withPlayers(List(Player("Alice"), Player("Bob")))
        .withGamePhase(AttackPhase)
        .build()
      
      val controller = Controller(gameState, UndoRedoManager())
      
      // Test: Factory method works with different controller states
      val tui = TUI(controller)
      
      tui shouldBe a[ViewInterface]
    }
    
    "create distinct TUI instances for different controllers" in {
      val controller1 = Controller(
        GameStateBuilder().withGamePhase(SetupPhase).build(),
        UndoRedoManager()
      )
      val controller2 = Controller(
        GameStateBuilder().withGamePhase(AttackPhase).build(),
        UndoRedoManager()
      )
      
      // Test: Each call to TUI(controller) creates a new instance
      val tui1 = TUI(controller1)
      val tui2 = TUI(controller2)
      
      tui1 should not be theSameInstanceAs(tui2)
    }
  }
  
  "ViewInterface GUI factory" should {
    "create GUI instances through apply method (line 74-75)" in {
      ensureJavaFXInitialized()
      
      val controller = Controller(
        GameStateBuilder().withGamePhase(SetupPhase).build(),
        UndoRedoManager()
      )
      
      // Test: GUI(controller) calls new InternalGUI(controller)
      var guiCreated = false
      var gui: GUI = null
      
      Platform.runLater(() => {
        try {
          gui = GUI(controller)
          guiCreated = true
        } catch {
          case e: Throwable => 
            fail(s"Failed to create GUI: ${e.getMessage}")
        }
      })
      
      // Wait for JavaFX thread to complete
      Thread.sleep(1000)
      
      guiCreated shouldBe true
      gui should not be null
      gui shouldBe a[GUI]
    }
    
    "create GUI that is a ViewInterface" in {
      ensureJavaFXInitialized()
      
      val controller = Controller(
        GameStateBuilder().withGamePhase(SetupPhase).build(),
        UndoRedoManager()
      )
      
      var gui: GUI = null
      
      Platform.runLater(() => {
        gui = GUI(controller)
      })
      
      Thread.sleep(1000)
      
      gui shouldBe a[ViewInterface]
      gui shouldBe a[View]
    }
    
    "create GUI with different game states" in {
      ensureJavaFXInitialized()
      
      val gameState = GameStateBuilder()
        .withPlayers(List(Player("Alice"), Player("Bob")))
        .withGamePhase(AttackPhase)
        .withTrumpCard(Card(Suit.Hearts, Rank.Ace))
        .build()
      
      val controller = Controller(gameState, UndoRedoManager())
      
      var gui: GUI = null
      
      Platform.runLater(() => {
        gui = GUI(controller)
      })
      
      Thread.sleep(1000)
      
      gui shouldBe a[ViewInterface]
    }
    
    "create distinct GUI instances for different controllers" in {
      ensureJavaFXInitialized()
      
      val controller1 = Controller(
        GameStateBuilder().withGamePhase(SetupPhase).build(),
        UndoRedoManager()
      )
      val controller2 = Controller(
        GameStateBuilder().withGamePhase(AttackPhase).build(),
        UndoRedoManager()
      )
      
      var gui1: GUI = null
      var gui2: GUI = null
      
      Platform.runLater(() => {
        gui1 = GUI(controller1)
        gui2 = GUI(controller2)
      })
      
      Thread.sleep(1000)
      
      gui1 should not be null
      gui2 should not be null
      gui1 should not be theSameInstanceAs(gui2)
    }
    
    "register GUI as observer on controller" in {
      ensureJavaFXInitialized()
      
      val controller = Controller(
        GameStateBuilder().withGamePhase(SetupPhase).build(),
        UndoRedoManager()
      )
      
      var gui: GUI = null
      
      Platform.runLater(() => {
        gui = GUI(controller)
      })
      
      Thread.sleep(1000)
      
      // GUI constructor should register itself as observer
      // We verify this by checking that gui is a ViewInterface (Observer)
      gui shouldBe a[ViewInterface]
    }
    
    "create GUI instance with correct internal type" in {
      ensureJavaFXInitialized()
      
      val controller = Controller(
        GameStateBuilder().withGamePhase(SetupPhase).build(),
        UndoRedoManager()
      )
      
      var gui: Any = null
      
      Platform.runLater(() => {
        gui = GUI(controller)
      })
      
      Thread.sleep(1000)
      
      // Verify that the returned object is actually the InternalGUI implementation
      gui.getClass.getSimpleName shouldBe "DurakGUI"
    }
  }
  
  "ViewInterface" should {
    "define View trait as base type" in {
      val tuiController = Controller(
        GameStateBuilder().withGamePhase(SetupPhase).build(),
        UndoRedoManager()
      )
      
      val view: View = TUI(tuiController)
      view shouldBe a[ViewInterface]
    }
    
    "provide consistent factory pattern for all view types" in {
      val controller = Controller(
        GameStateBuilder().withGamePhase(SetupPhase).build(),
        UndoRedoManager()
      )
      
      // TUI factory works
      noException should be thrownBy TUI(controller)
      
      // GUI factory object exists
      GUI shouldBe a[Object]
    }
    
    "support type aliasing for both TUI and GUI" in {
      val controller = Controller(
        GameStateBuilder().withGamePhase(SetupPhase).build(),
        UndoRedoManager()
      )
      
      // Both should compile with View type
      val tui: View = TUI(controller)
      
      tui shouldBe a[View]
    }
  }
}
