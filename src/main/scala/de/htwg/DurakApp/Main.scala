package de.htwg.DurakApp

import de.htwg.DurakApp.aview.gui.DurakGUI
import de.htwg.DurakApp.aview.tui.TUI
import de.htwg.DurakApp.controller.Controller
import de.htwg.DurakApp.model.builder.GameStateBuilder
import de.htwg.DurakApp.util.UndoRedoManager
import scalafx.application.Platform
import de.htwg.DurakApp.model.state.SetupPhase

@main def run: Unit = {
  val initialGameState = GameStateBuilder().withGamePhase(SetupPhase).build()
  val controller = new Controller(initialGameState, UndoRedoManager())

  val tui = new TUI(controller)
  controller.add(tui)

    Platform.startup(() => {
      val gui = new DurakGUI(controller)
      gui.start()
    })

  tui.run()
}
