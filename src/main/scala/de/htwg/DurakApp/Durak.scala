package de.htwg.DurakApp

import aview.TUI
import controller.Controller
import scala.util.Random

@main def run: Unit =
  println("Welcome to Durak")

  val controller = Controller()
  val tui = new TUI(controller)
  controller.add(tui)

  val deckSize = tui.askForDeckSize()
  val playerCount = tui.askForPlayerCount()
  val playerNames = tui.askForPlayerNames(playerCount)

  controller.setupGameAndStart(deckSize, playerNames, new Random, tui)

