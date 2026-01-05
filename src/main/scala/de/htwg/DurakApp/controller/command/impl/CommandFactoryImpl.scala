package de.htwg.DurakApp.controller.command.impl

import de.htwg.DurakApp.controller.command.{
  CommandFactory,
  PlayCardCommand,
  PassCommand,
  TakeCardsCommand,
  PhaseChangeCommand
}
import de.htwg.DurakApp.model.Card
import de.htwg.DurakApp.model.state.GamePhases

import com.google.inject.Inject

class CommandFactoryImpl @Inject() (gamePhases: GamePhases) extends CommandFactory {
  
  override def playCard(card: Card): PlayCardCommand =
    PlayCardCommand(card, gamePhases)
  
  override def pass(): PassCommand =
    PassCommand(gamePhases)
  
  override def takeCards(): TakeCardsCommand =
    TakeCardsCommand()
  
  override def phaseChange(): PhaseChangeCommand =
    PhaseChangeCommand()
}
