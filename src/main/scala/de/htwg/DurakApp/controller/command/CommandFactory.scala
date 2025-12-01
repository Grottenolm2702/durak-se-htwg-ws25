package de.htwg.DurakApp.controller.command

import de.htwg.DurakApp.model.GameState
import de.htwg.DurakApp.model.state.GameEvent

import de.htwg.DurakApp.controller.{
  PlayerAction,
  PlayCardAction,
  PassAction,
  TakeCardsAction,
  InvalidAction,
  UndoAction,
  RedoAction
}

object CommandFactory {
  def createCommand(
      action: PlayerAction,
      gameState: GameState
  ): Either[GameEvent, GameCommand] = {
    action match {
      case PlayCardAction(card) =>
        Right(PlayCardCommand(card))

      case PassAction =>
        Right(PassCommand())

      case TakeCardsAction =>
        Right(TakeCardsCommand())

      case InvalidAction | UndoAction | RedoAction =>
        Left(GameEvent.InvalidMove)
    }
  }
}
