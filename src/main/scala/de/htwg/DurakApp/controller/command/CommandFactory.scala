package de.htwg.DurakApp.controller.command

import de.htwg.DurakApp.model.GameState
import de.htwg.DurakApp.model.state.GameEvent

import de.htwg.DurakApp.controller.{PlayerAction, PlayCardAction, PassAction, TakeCardsAction, InvalidAction}

object CommandFactory {
  def createCommand(action: PlayerAction, gameState: GameState): Either[GameEvent, Command] = {
    action match {
      case PlayCardAction(cardString) =>
        Right(PlayCardCommand(cardString))

      case PassAction =>
        Right(PassCommand())

      case TakeCardsAction =>
        Right(TakeCardsCommand())

      case InvalidAction =>
        Left(GameEvent.InvalidMove)
    }
  }
}