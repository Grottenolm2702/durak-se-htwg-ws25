package de.htwg.DurakApp.controller.command.impl

import de.htwg.DurakApp.model.ModelInterface.{GameState, GameEvent}

import de.htwg.DurakApp.controller.{
  PlayerAction,
  PlayCardAction,
  PassAction,
  TakeCardsAction,
  InvalidAction,
  UndoAction,
  RedoAction,
  SetPlayerCountAction,
  AddPlayerNameAction,
  SetDeckSizeAction,
  PlayAgainAction,
  ExitGameAction
}

import de.htwg.DurakApp.controller.command.GameCommand

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
      case SetPlayerCountAction(_) => Left(GameEvent.InvalidMove)
      case AddPlayerNameAction(_)  => Left(GameEvent.InvalidMove)
      case SetDeckSizeAction(_)    => Left(GameEvent.InvalidMove)
      case PlayAgainAction         => Left(GameEvent.InvalidMove)
      case ExitGameAction          => Left(GameEvent.InvalidMove)
    }
  }
}
