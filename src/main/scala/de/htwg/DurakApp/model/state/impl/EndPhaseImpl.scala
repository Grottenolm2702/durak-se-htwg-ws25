package de.htwg.DurakApp.model.state.impl

import de.htwg.DurakApp.model._
import de.htwg.DurakApp.model.state.{GamePhase, GameEvent}

case object EndPhaseImpl extends GamePhase {
  override def toString: String = "EndPhase"

  override def handle(gameState: GameState): GameState = {
    val playersWithCards = gameState.players.filter(_.hand.nonEmpty)
    val playersWithoutCards = gameState.players.filter(_.hand.isEmpty)

    playersWithCards.headOption match {

      case Some(loser) =>
        val winner = playersWithoutCards.head

        gameState.copy(
          gamePhase = AskPlayAgainPhaseImpl,
          lastEvent = Some(GameEvent.GameOver(winner, Some(loser)))
        )

      case None =>
        val representativeWinner = gameState.players.head

        gameState.copy(
          gamePhase = AskPlayAgainPhaseImpl,
          lastEvent = Some(GameEvent.GameOver(representativeWinner, None))
        )
    }
  }
}
