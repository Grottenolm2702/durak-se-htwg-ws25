package de.htwg.DurakApp.controller.command

import de.htwg.DurakApp.model.{Card, GameState}
import de.htwg.DurakApp.model.state.GameEvent
import de.htwg.DurakApp.controller.PlayerAction

object CommandFactory:
  def createCommand(
      action: PlayerAction,
      gameState: GameState
  ): Either[GameEvent, GameCommand] =
    impl.CommandFactory.createCommand(action, gameState)
  
  def playCard(card: Card): GameCommand =
    impl.PlayCardCommand(card)
  
  def pass(): GameCommand =
    impl.PassCommand()
  
  def takeCards(): GameCommand =
    impl.TakeCardsCommand()
  
  def phaseChange(): GameCommand =
    impl.PhaseChangeCommand()
