package de.htwg.DurakApp.controller.command

import de.htwg.DurakApp.model.GameState
import de.htwg.DurakApp.model.state.GameEvent

object CommandFactory {
  def createCommand(input: String, gameState: GameState): Either[GameEvent, Command] = {
    val inputArgs = input.trim.toLowerCase.split("\\s+").toList
    inputArgs.headOption match {
      case Some("play") if inputArgs.length > 1 =>
        Right(PlayCardCommand(inputArgs.tail.mkString(" ")))

      case Some("pass") =>
        Right(PassCommand())

      case Some("take") =>
        Right(TakeCardsCommand())

      case _ =>
        Left(GameEvent.InvalidMove)
    }
  }
}