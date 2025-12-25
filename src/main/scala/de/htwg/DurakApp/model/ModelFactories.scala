package de.htwg.DurakApp.model

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
