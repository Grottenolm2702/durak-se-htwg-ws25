package de.htwg.DurakApp.model.state

import de.htwg.DurakApp.model.GameState

case object EndPhase extends GamePhase {
  override def handle(gameState: GameState): GameState = {
    val playersWithCards = gameState.players.filter(_.hand.nonEmpty)
    val playersWithoutCards = gameState.players.filter(_.hand.isEmpty)

    val loser = playersWithCards.headOption

    val winner = playersWithoutCards.headOption

    (winner, loser) match {
      case (Some(w), Some(l)) =>
        gameState.copy(
          gamePhase = this,
          lastEvent = Some(GameEvent.GameOver(w, l))
        )
      case _ =>
        gameState.copy(
          gamePhase = this,
          lastEvent = Some(GameEvent.InvalidMove)
        )
    }
  }
}
