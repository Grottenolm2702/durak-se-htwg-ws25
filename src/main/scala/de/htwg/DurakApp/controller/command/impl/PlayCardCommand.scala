package de.htwg.DurakApp.controller.command.impl

import de.htwg.DurakApp.controller.command.GameCommand
import de.htwg.DurakApp.model.ModelInterface.{
  Card,
  GameState,
  GameEvent,
  StateInterface
}

case class PlayCardCommand(card: Card) extends GameCommand {
  override def execute(gameState: GameState): GameState = {

    val activePlayerIndex =
      gameState.gamePhase match {
        case StateInterface.DefensePhase => gameState.defenderIndex
        case _ =>
          gameState.currentAttackerIndex.getOrElse(gameState.attackerIndex)
      }

    val activePlayer = gameState.players(activePlayerIndex)

    if (!activePlayer.hand.contains(card)) {
      gameState.copy(lastEvent = Some(GameEvent.InvalidMove))
    } else {
      gameState.gamePhase.playCard(card, activePlayerIndex, gameState)
    }
  }
}
