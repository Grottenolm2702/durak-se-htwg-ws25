package de.htwg.DurakApp.model

import de.htwg.DurakApp.model.state.{GameEvent, GamePhase}
import de.htwg.DurakApp.model.builder.GameStateBuilder

case class GameState(
    players: List[Player],
    deck: List[Card],
    table: Map[Card, Option[Card]],
    discardPile: List[Card],
    trumpCard: Card,
    attackerIndex: Int,
    defenderIndex: Int,
    gamePhase: GamePhase,
    lastEvent: Option[GameEvent] = None,
    passedPlayers: Set[Int] = Set.empty,
    roundWinner: Option[Int] = None
) {

  def description: String = gamePhase.toString

  def toBuilder: GameStateBuilder = {
    GameStateBuilder()
      .withPlayers(players)
      .withDeck(deck)
      .withTable(table)
      .withDiscardPile(discardPile)
      .withTrumpCard(trumpCard)
      .withAttackerIndex(attackerIndex)
      .withDefenderIndex(defenderIndex)
      .withGamePhase(gamePhase)
      .withLastEvent(lastEvent)
      .withPassedPlayers(passedPlayers)
      .withRoundWinner(roundWinner)
  }
}
