package de.htwg.DurakApp

import de.htwg.DurakApp.aview.tui.TUI
import de.htwg.DurakApp.controller.{Controller, Setup}
import de.htwg.DurakApp.model.builder.GameStateBuilder

@main def run: Unit = {
  val temptui = TUI(new Controller(GameStateBuilder().build()))
  val deckSize = temptui.askForDeckSize()
  val playerCount = temptui.askForPlayerCount()
  val playerNames = temptui.askForPlayerNames(playerCount)

  val initialGameState = Setup.setupGame(playerNames, deckSize)

  initialGameState.foreach { state =>
    val controller = new Controller(state)
    val tui = new TUI(controller)
    controller.add(tui)
    tui.run()
  }
}
