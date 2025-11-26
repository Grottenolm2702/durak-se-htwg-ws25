package de.htwg.DurakApp

import aview.TUI
import controller.{Controller, Setup}

@main def run(): Unit = {

  println("Geben Sie die Spielernamen ein (kommagetrennt):")
  val names = scala.io.StdIn.readLine()
  val playerNames = names.split(',').map(_.trim).filter(_.nonEmpty).toList

  if (playerNames.length < 2) {
    println("Es werden mindestens 2 Spieler benötigt. Bitte neu starten.")
  } else {
    val tempTui = new TUI(new Controller(null))

    val deckSize = tempTui.askForDeckSize()

    val initialGameState = Setup.setupGame(playerNames, deckSize)

    val controller = new Controller(initialGameState)
    val tui = new TUI(controller)

    tui.run()
  }
}
