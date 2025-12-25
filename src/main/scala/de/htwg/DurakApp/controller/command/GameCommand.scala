package de.htwg.DurakApp.controller.command

import de.htwg.DurakApp.model.GameState

trait GameCommand {
  def execute(gameState: GameState): GameState
  def undo(
      currentGameState: GameState,
      previousGameState: GameState
  ): GameState = previousGameState
}

object GameCommand:
  def createCommand(
      action: de.htwg.DurakApp.controller.PlayerAction,
      gameState: GameState
  ): Either[de.htwg.DurakApp.model.state.GameEvent, GameCommand] =
    impl.CommandFactory.createCommand(action, gameState)
  
  def playCard(card: de.htwg.DurakApp.model.Card): GameCommand =
    impl.PlayCardCommand(card)
  
  def pass(): GameCommand =
    impl.PassCommand()
  
  def takeCards(): GameCommand =
    impl.TakeCardsCommand()
  
  def phaseChange(): GameCommand =
    impl.PhaseChangeCommand()
