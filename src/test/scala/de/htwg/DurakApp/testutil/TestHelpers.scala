package de.htwg.DurakApp.testutil

import de.htwg.DurakApp.model.{Player, GameState, Card, Suit, Rank}
import de.htwg.DurakApp.model.state.{GamePhase, GameEvent}

/** Convenience functions for tests that mimic the old companion object syntax.
  * Import these in your test files: import de.htwg.DurakApp.testutil.TestHelpers._
  */
object TestHelpers:
  
  /** Create a Card for tests */
  def Card(suit: Suit, rank: Rank, isTrump: Boolean = false): Card =
    TestFactories.cardFactory(suit, rank, isTrump)
  
  /** Create a Player for tests */
  def Player(name: String, hand: List[Card] = List.empty, isDone: Boolean = false): Player =
    TestFactories.playerFactory(name, hand, isDone)
  
  /** Create a GameState for tests */
  def GameState(
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
  ): GameState =
    TestFactories.gameStateFactory(
      players,
      deck,
      table,
      discardPile,
      trumpCard,
      attackerIndex,
      defenderIndex,
      gamePhase,
      lastEvent,
      passedPlayers,
      roundWinner,
      setupPlayerCount,
      setupPlayerNames,
      setupDeckSize,
      currentAttackerIndex,
      lastAttackerIndex
    )
