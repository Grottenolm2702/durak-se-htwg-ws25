package de.htwg.DurakApp.controller

import de.htwg.DurakApp.model.GameState
import de.htwg.DurakApp.model.state.GameEvent
import de.htwg.DurakApp.util.Observable
import de.htwg.DurakApp.controller.command.{Command, CommandFactory}
import de.htwg.DurakApp.controller.{
  PlayerAction,
  PlayCardAction,
  PassAction,
  TakeCardsAction,
  InvalidAction
}

class Controller(var gameState: GameState) extends Observable {

  def processPlayerAction(action: PlayerAction): Unit = {
    val result = CommandFactory.createCommand(action, gameState)
    result match {
      case Left(event) =>
        this.gameState = gameState.copy(lastEvent = Some(event))
      case Right(command) =>
        this.gameState = command.execute(gameState)
    }
    notifyObservers
  }

  def getStatusString(): String = {
    gameState.gamePhase.toString
  }
}
