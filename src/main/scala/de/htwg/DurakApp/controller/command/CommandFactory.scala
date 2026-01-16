package de.htwg.DurakApp.controller.command

import de.htwg.DurakApp.model.{Card, GameState}
import de.htwg.DurakApp.model.state.GameEvent

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

trait CommandFactory {
  def createCommand(
      action: PlayerAction,
      gameState: GameState
  ): Either[GameEvent, GameCommand] = {
    action match {
      case PlayCardAction(card) =>
        Right(playCard(card))

      case PassAction =>
        Right(pass())

      case TakeCardsAction =>
        Right(takeCards())

      case InvalidAction | UndoAction | RedoAction =>
        Left(GameEvent.InvalidMove)
      case SetPlayerCountAction(_) => Left(GameEvent.InvalidMove)
      case AddPlayerNameAction(_)  => Left(GameEvent.InvalidMove)
      case SetDeckSizeAction(_)    => Left(GameEvent.InvalidMove)
      case PlayAgainAction         => Left(GameEvent.InvalidMove)
      case ExitGameAction          => Left(GameEvent.InvalidMove)
    }
  }

  def playCard(card: Card): PlayCardCommand

  def pass(): PassCommand

  def takeCards(): TakeCardsCommand

  def phaseChange(): PhaseChangeCommand
}
