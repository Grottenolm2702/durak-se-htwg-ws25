package de.htwg.DurakApp

import de.htwg.DurakApp.aview.gui.DurakGUI
import de.htwg.DurakApp.aview.tui.TUI
import de.htwg.DurakApp.controller.{Controller, Setup}
import de.htwg.DurakApp.model.builder.GameStateBuilder
import de.htwg.DurakApp.util.UndoRedoManager
import scalafx.application.Platform

@main def run: Unit = {
  val inputTui = TUI(
    new Controller(GameStateBuilder().build(), UndoRedoManager())
  )
  val deckSize = inputTui.askForDeckSize()
  val playerCount = inputTui.askForPlayerCount()
  val playerNames = inputTui.askForPlayerNames(playerCount)

  Setup.setupGame(playerNames, deckSize).foreach { state =>
    val controller = new Controller(state, UndoRedoManager())
    val tui = new TUI(controller)
    controller.add(tui)

    Platform.startup(() => {
      val gui = new DurakGUI(controller)
      gui.start()
    })

    tui.run()
  }
}

