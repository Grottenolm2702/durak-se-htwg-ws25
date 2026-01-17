package de.htwg.DurakApp

import com.google.inject.Guice

import de.htwg.DurakApp.controller.Controller
import de.htwg.DurakApp.aview.tui.TUI
import de.htwg.DurakApp.aview.gui.DurakGUI

import scalafx.application.Platform

@main def run: Unit = {
  val injector = Guice.createInjector(new DurakModule)

  val tui = injector.getInstance(classOf[TUI])

  try {
    Platform.startup(() => {
      val gui = injector.getInstance(classOf[DurakGUI])
      gui.start()
    })
  } catch {
    case _: Exception => println("GUI not available, running in TUI-only mode")
  }

  tui.run()
}
