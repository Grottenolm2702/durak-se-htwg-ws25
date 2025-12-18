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

  private val javafxInitialized =
    new java.util.concurrent.atomic.AtomicBoolean(false)

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
            latch.countDown()
        }
      }).start()

      latch.await(5, TimeUnit.SECONDS)
    }
  }

  "ViewInterface TUI factory" should {
    "create TUI instances through apply method" in {
      val controller = Controller(
        GameStateBuilder().withGamePhase(SetupPhase).build(),
        UndoRedoManager()
      )

      val tui = TUI(controller)

      tui shouldBe a[TUI]
      tui shouldBe a[View]
      tui shouldBe a[ViewInterface]
    }
  }

  "ViewInterface GUI factory" should {
    "create GUI instances through apply method" in {
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

      gui should not be null
      gui shouldBe a[GUI]
      gui shouldBe a[View]
      gui shouldBe a[ViewInterface]
    }
  }


}
