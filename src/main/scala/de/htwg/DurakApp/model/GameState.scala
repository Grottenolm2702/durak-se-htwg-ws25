package de.htwg.DurakApp.model

import de.htwg.DurakApp.model.state.{GameEvent, GamePhase}

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
  roundWinner: Option[Int] = None,
)
