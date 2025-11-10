package de.htwg.DurakApp

import aview.TUI
import controller.Controller

import scala.util.Random

@main def run: Unit =
  println("Welcome to Durak")
  val controller = Controller()
  val tui = TUI(controller)
  controller.add(tui)
  controller.run(new Random)
