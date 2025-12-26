package de.htwg.DurakApp.model

/** Factory objects for creating model instances.
  * 
  * These factories are bound as singletons in Guice (see DurakModule).
  * They are the only places that reference impl classes.
  * 
  * Note: In most cases, you should use the companion objects (Card, Player, GameState)
  * instead of these factories. These are primarily for dependency injection scenarios
  * where you need to inject the factory itself.
  */

object CardFactory:
  def apply(suit: Suit, rank: Rank, isTrump: Boolean = false): Card =
    impl.CardImpl(suit, rank, isTrump)

object PlayerFactory:
  def apply(
      name: String,
      hand: List[Card] = List(),
      isDone: Boolean = false
  ): Player =
    impl.PlayerImpl(name, hand, isDone)

object GameStateFactory:
  def apply(
      players: List[Player],
      deck: List[Card],
      table: Map[Card, Option[Card]],
      discardPile: List[Card],
      trumpCard: Card,
      attackerIndex: Int,
      defenderIndex: Int,
      gamePhase: state.GamePhase,
      lastEvent: Option[state.GameEvent],
      passedPlayers: Set[Int],
      roundWinner: Option[Int],
      setupPlayerCount: Option[Int],
      setupPlayerNames: List[String],
      setupDeckSize: Option[Int],
      currentAttackerIndex: Option[Int],
      lastAttackerIndex: Option[Int]
  ): GameState =
    impl.GameStateImpl(
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
