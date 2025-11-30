package de.htwg.DurakApp.controller.command

import de.htwg.DurakApp.controller.command.Command
import de.htwg.DurakApp.model.Card
import de.htwg.DurakApp.model.state.{DefensePhase, GameEvent}
import de.htwg.DurakApp.model.GameState

case class PlayCardCommand(card: Card) extends Command {
  override def execute(gameState: GameState): GameState = {

    val playerIdx =
      gameState.gamePhase match {
        case DefensePhase => gameState.defenderIndex
        case _            => gameState.attackerIndex
      }

    val player = gameState.players(playerIdx)

    if (!player.hand.contains(card)) {
      gameState.copy(lastEvent = Some(GameEvent.InvalidMove))
    } else {
      gameState.gamePhase.playCard(card, playerIdx, gameState)
    }
  }
}
