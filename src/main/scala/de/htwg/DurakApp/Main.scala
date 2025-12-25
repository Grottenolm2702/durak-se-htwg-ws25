package de.htwg.DurakApp

import com.google.inject.Guice
import de.htwg.DurakApp.controller.Controller
import de.htwg.DurakApp.aview.tui.TUI
import de.htwg.DurakApp.aview.gui.DurakGUI
import scalafx.application.Platform

@main def run: Unit = {
  val injector = Guice.createInjector(new DurakModule)
  
  // Get TUI through dependency injection
  val tui = injector.getInstance(classOf[TUI])

  Platform.startup(() => {
    // Get GUI after Platform is initialized
    val gui = injector.getInstance(classOf[DurakGUI])
    gui.start()
  })

  tui.run()
}
