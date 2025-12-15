package de.htwg.DurakApp

import de.htwg.DurakApp.model.ModelInterface.*
import de.htwg.DurakApp.model.ModelInterface.StateInterface.*
import de.htwg.DurakApp.controller.ControllerInterface.*
import de.htwg.DurakApp.aview.ViewInterface.*
import de.htwg.DurakApp.util.UndoRedoManager
import scalafx.application.Platform

@main def run: Unit = {
  val initialGameState = GameStateBuilder().withGamePhase(SetupPhase).build()
  val controller = Controller(initialGameState, UndoRedoManager())
  val tui = new TUI(controller)

  Platform.startup(() => {
    val gui = new DurakGUI(controller)
    gui.start()
  })

  tui.run()
}
