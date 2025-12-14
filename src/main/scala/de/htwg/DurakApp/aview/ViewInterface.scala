package de.htwg.DurakApp.aview

import de.htwg.DurakApp.util.Observer

trait ViewInterface extends Observer {
  def run(): Unit
}
