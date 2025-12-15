package de.htwg.DurakApp.aview

import de.htwg.DurakApp.util.Observer
import de.htwg.DurakApp.aview.tui.{TUI as InternalTUI}
import de.htwg.DurakApp.aview.gui.{DurakGUI as InternalGUI}

/** View Component Interface
  *
  * This is the public port to the View component. All external access to view
  * implementations must go through this interface.
  *
  * The View component implements the Observer pattern to react to game state
  * changes from the Controller. Multiple views can observe the same controller
  * simultaneously (e.g., both TUI and GUI can be active).
  *
  * Provides access to:
  *   - TUI: Text-based User Interface for console interaction
  *   - GUI: Graphical User Interface using JavaFX
  *
  * @example
  *   {{{
  * import de.htwg.DurakApp.aview.ViewInterface.*
  *
  * val tui = TUI(controller)
  * val gui = GUI(controller)
  * controller.add(tui)
  * controller.add(gui)
  *   }}}
  */
object ViewInterface:

  // Type Aliases

  /** Type alias for the View trait. Base trait that all view implementations
    * must extend.
    */
  type View = ViewInterface

  /** Type alias for the Text User Interface. Provides console-based interaction
    * with the game.
    */
  type TUI = InternalTUI

  /** Type alias for the Graphical User Interface. Provides JavaFX-based
    * graphical interaction with the game.
    */
  type GUI = InternalGUI

  // Factory Objects

  /** Factory for creating TUI instances.
    *
    * The TUI provides a text-based console interface for playing Durak.
    */
  object TUI:
    /** Creates a new TUI instance.
      *
      * @param controller
      *   The controller managing game state
      * @return
      *   A new TUI instance
      */
    def apply(controller: de.htwg.DurakApp.controller.Controller): InternalTUI =
      new InternalTUI(controller)

  /** Factory for creating GUI instances.
    *
    * The GUI provides a graphical JavaFX interface for playing Durak.
    */
  object GUI:
    /** Creates a new GUI instance.
      *
      * @param controller
      *   The controller managing game state
      * @return
      *   A new GUI instance
      */
    def apply(controller: de.htwg.DurakApp.controller.Controller): InternalGUI =
      new InternalGUI(controller)

/** View Interface Trait
  *
  * Defines the contract that all view implementations must follow. Views
  * implement the Observer pattern to receive notifications when the game state
  * changes in the Controller.
  *
  * This trait extends Observer to enforce that all views can be notified of
  * state changes and must react accordingly.
  *
  * @example
  *   {{{
  * class MyCustomView(controller: Controller) extends ViewInterface:
  *   def update: Unit =
  *     // React to controller state changes
  *     println(s"Game state changed: ${controller.gameState}")
  *   }}}
  */
trait ViewInterface extends Observer:

  /** Called by the Controller when the game state changes.
    *
    * Implementations should refresh their display and show the current game
    * state to the user. This method is invoked automatically via the Observer
    * pattern whenever the Controller's notifyObservers method is called.
    */
  def update: Unit
