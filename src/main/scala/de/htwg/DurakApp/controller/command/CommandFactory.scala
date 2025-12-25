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

object CommandFactory {
  def createCommand(
      action: PlayerAction,
      gameState: GameState
  ): Either[GameEvent, GameCommand] = {
    action match {
      case PlayCardAction(card) =>
        Right(impl.PlayCardCommand(card))

      case PassAction =>
        Right(impl.PassCommand())

      case TakeCardsAction =>
        Right(impl.TakeCardsCommand())

      case InvalidAction | UndoAction | RedoAction =>
        Left(GameEvent.InvalidMove)
      case SetPlayerCountAction(_) => Left(GameEvent.InvalidMove)
      case AddPlayerNameAction(_)  => Left(GameEvent.InvalidMove)
      case SetDeckSizeAction(_)    => Left(GameEvent.InvalidMove)
      case PlayAgainAction         => Left(GameEvent.InvalidMove)
      case ExitGameAction          => Left(GameEvent.InvalidMove)
    }
  }
  
  def playCard(card: Card): GameCommand =
    impl.PlayCardCommand(card)
  
  def pass(): GameCommand =
    impl.PassCommand()
  
  def takeCards(): GameCommand =
    impl.TakeCardsCommand()
  
  def phaseChange(): GameCommand =
    impl.PhaseChangeCommand()
}
