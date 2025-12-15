package de.htwg.DurakApp.controller.command

import de.htwg.DurakApp.controller.command.GameCommand
import de.htwg.DurakApp.model.Card
import de.htwg.DurakApp.model.state.{DefensePhase, GameEvent}
import de.htwg.DurakApp.model.GameState

case class PlayCardCommand(card: Card) extends GameCommand {
  override def execute(gameState: GameState): GameState = {

    val activePlayerIndex =
      gameState.gamePhase match {
        case DefensePhase => gameState.defenderIndex
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
