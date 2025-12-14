package de.htwg.DurakApp.controller

import de.htwg.DurakApp.model.{GameState, ModelInterface}
import de.htwg.DurakApp.util.Observable

trait ControllerInterface extends Observable with ModelInterface {
  def processPlayerAction(action: PlayerAction): GameState
  def undo(): Option[GameState]
  def redo(): Option[GameState]
  def getStatusString(): String
}
