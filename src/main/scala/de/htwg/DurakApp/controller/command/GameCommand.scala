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
    CommandFactory.createCommand(action, gameState)
  
  def playCard(card: de.htwg.DurakApp.model.Card): GameCommand =
    CommandFactory.playCard(card)
  
  def pass(): GameCommand =
    CommandFactory.pass()
  
  def takeCards(): GameCommand =
    CommandFactory.takeCards()
  
  def phaseChange(): GameCommand =
    CommandFactory.phaseChange()
