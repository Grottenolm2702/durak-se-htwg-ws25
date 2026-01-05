package de.htwg.DurakApp.model

import de.htwg.DurakApp.model.state.{GameEvent, GamePhase}

/** Factory trait for creating Card instances.
  *
  * Inject this factory via Guice to create Card instances.
  */
trait CardFactory:
  def apply(suit: Suit, rank: Rank, isTrump: Boolean = false): Card

/** Factory trait for creating Player instances.
  *
  * Inject this factory via Guice to create Player instances.
  */
trait PlayerFactory:
  def apply(
      name: String,
      hand: List[Card] = List(),
      isDone: Boolean = false
  ): Player

/** Factory trait for creating GameState instances.
  *
  * Inject this factory via Guice to create GameState instances.
  */
trait GameStateFactory:
  def apply(
      players: List[Player],
      deck: List[Card],
      table: Map[Card, Option[Card]],
      discardPile: List[Card],
      trumpCard: Card,
      attackerIndex: Int,
      defenderIndex: Int,
      gamePhase: GamePhase,
      lastEvent: Option[GameEvent],
      passedPlayers: Set[Int],
      roundWinner: Option[Int],
      setupPlayerCount: Option[Int],
      setupPlayerNames: List[String],
      setupDeckSize: Option[Int],
      currentAttackerIndex: Option[Int],
      lastAttackerIndex: Option[Int]
  ): GameState
