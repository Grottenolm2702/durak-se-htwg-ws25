package de.htwg.DurakApp.model.state

import de.htwg.DurakApp.model.GameState

case object EndPhase extends GamePhase {
  override def handle(gameState: GameState): GameState = {
    val playersWithCards = gameState.players.filter(_.hand.nonEmpty)
    val playersWithoutCards = gameState.players.filter(_.hand.isEmpty)

    playersWithCards.headOption match {
      case Some(loser) =>
        val winner = playersWithoutCards.headOption.getOrElse(
          throw new IllegalStateException("EndPhase: Expected at least one player without cards when a loser is identified.")
        )
        gameState.copy(
          gamePhase = this,
          lastEvent = Some(GameEvent.GameOver(winner, Some(loser)))
        )
      case None =>
        val representativeWinner = gameState.players.headOption.getOrElse(
          throw new IllegalStateException("EndPhase: No players in game state. Cannot determine winner.")
        )
        gameState.copy(
          gamePhase = this,
          lastEvent = Some(GameEvent.GameOver(representativeWinner, None))
        )
    }
  }
}
