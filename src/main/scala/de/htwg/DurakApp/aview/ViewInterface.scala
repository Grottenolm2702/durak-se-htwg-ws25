package de.htwg.DurakApp.aview

import de.htwg.DurakApp.util.Observer

/**
 * View Component Interface
 * 
 * This is the public port to the View component. All external access
 * to view implementations must go through this interface.
 * 
 * Exports:
 * - TUI: Text User Interface
 * - DurakGUI: Graphical User Interface
 * 
 * Views observe the controller and update when notified.
 */
object ViewInterface:
  export de.htwg.DurakApp.aview.tui.TUI
  export de.htwg.DurakApp.aview.gui.DurakGUI

/**
 * View Interface Trait
 * 
 * Defines the external contract for view implementations.
 * All views must implement the Observer pattern.
 * 
 * The update method is called by the controller when the game state changes.
 */
trait ViewInterface extends Observer:
  def update: Unit
