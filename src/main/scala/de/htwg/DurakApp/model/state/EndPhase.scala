package de.htwg.DurakApp.model.state

import de.htwg.DurakApp.model._

case object EndPhase extends GamePhase {

  override def handle(gameState: GameState): GameState = {
    require(
      gameState.players.nonEmpty,
      "EndPhase: No players in game state. Cannot determine winner."
    )

    val playersWithCards = gameState.players.filter(_.hand.nonEmpty)
    val playersWithoutCards = gameState.players.filter(_.hand.isEmpty)

    playersWithCards.headOption match {

      case Some(loser) =>
        require(
          playersWithoutCards.nonEmpty,
          "EndPhase: Expected at least one player without cards when a loser is identified."
        )

        val winner = playersWithoutCards.head

        gameState.copy(
          gamePhase = this,
          lastEvent = Some(GameEvent.GameOver(winner, Some(loser)))
        )

      case None =>
        val representativeWinner = gameState.players.head

        gameState.copy(
          gamePhase = this,
          lastEvent = Some(GameEvent.GameOver(representativeWinner, None))
        )
    }
  }
}
