package de.htwg.DurakApp.model

import de.htwg.DurakApp.model.state.{GameEvent, GamePhase}
import de.htwg.DurakApp.model.builder.GameStateBuilder

trait GameState:
  def players: List[Player]
  def deck: List[Card]
  def table: Map[Card, Option[Card]]
  def discardPile: List[Card]
  def trumpCard: Card
  def attackerIndex: Int
  def defenderIndex: Int
  def gamePhase: GamePhase
  def lastEvent: Option[GameEvent]
  def passedPlayers: Set[Int]
  def roundWinner: Option[Int]
  def setupPlayerCount: Option[Int]
  def setupPlayerNames: List[String]
  def setupDeckSize: Option[Int]
  def currentAttackerIndex: Option[Int]
  def lastAttackerIndex: Option[Int]
  def toBuilder: GameStateBuilder

  def copy(
      players: List[Player] = this.players,
      deck: List[Card] = this.deck,
      table: Map[Card, Option[Card]] = this.table,
      discardPile: List[Card] = this.discardPile,
      trumpCard: Card = this.trumpCard,
      attackerIndex: Int = this.attackerIndex,
      defenderIndex: Int = this.defenderIndex,
      gamePhase: GamePhase = this.gamePhase,
      lastEvent: Option[GameEvent] = this.lastEvent,
      passedPlayers: Set[Int] = this.passedPlayers,
      roundWinner: Option[Int] = this.roundWinner,
      setupPlayerCount: Option[Int] = this.setupPlayerCount,
      setupPlayerNames: List[String] = this.setupPlayerNames,
      setupDeckSize: Option[Int] = this.setupDeckSize,
      currentAttackerIndex: Option[Int] = this.currentAttackerIndex,
      lastAttackerIndex: Option[Int] = this.lastAttackerIndex
  ): GameState = GameState(
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

object GameState:
  def apply(
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
      setupPlayerCount: Option[Int] = None,
      setupPlayerNames: List[String] = List.empty,
      setupDeckSize: Option[Int] = None,
      currentAttackerIndex: Option[Int] = None,
      lastAttackerIndex: Option[Int] = None
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
