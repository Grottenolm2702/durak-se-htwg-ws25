package de.htwg.DurakApp.model

import de.htwg.DurakApp.model.state.{GameEvent, GamePhase}
import de.htwg.DurakApp.model.builder.GameStateBuilder

/** Trait representing the complete state of a Durak game.
  *
  * This immutable data structure contains all information about the current
  * game including players, cards, current phase, and game progress. Use the
  * `copy` method to create modified versions of the state.
  *
  * Use GameStateFactory to create instances via dependency injection.
  */
trait GameState:

  /** All players in the game. */
  def players: List[Player]

  /** Cards remaining in the deck. */
  def deck: List[Card]

  /** Cards currently on the table. Maps attack cards to their optional defense
    * cards (None if undefended).
    */
  def table: Map[Card, Option[Card]]

  /** Cards that have been discarded (successfully defended attacks). */
  def discardPile: List[Card]

  /** The trump card that determines the trump suit for the game. */
  def trumpCard: Card

  /** Index of the main attacker in the players list. */
  def attackerIndex: Int

  /** Index of the current defender in the players list. */
  def defenderIndex: Int

  /** The current phase of the game (e.g., Attack, Defense, Draw). */
  def gamePhase: GamePhase

  /** The last event that occurred, if any. */
  def lastEvent: Option[GameEvent]

  /** Set of player indices who have passed this round. */
  def passedPlayers: Set[Int]

  /** Index of the round winner (defender if they successfully defended), if
    * any.
    */
  def roundWinner: Option[Int]

  /** Number of players selected during setup, if any. */
  def setupPlayerCount: Option[Int]

  /** List of player names collected during setup. */
  def setupPlayerNames: List[String]

  /** Deck size selected during setup, if any. */
  def setupDeckSize: Option[Int]

  /** Index of the current active attacker (may differ from main attacker). */
  def currentAttackerIndex: Option[Int]

  /** Index of the last player who played an attack card. */
  def lastAttackerIndex: Option[Int]

  /** Converts this game state to a builder for further modifications.
    * @return
    *   A GameStateBuilder initialized with this state's values
    */
  def toBuilder: GameStateBuilder

  /** Creates a copy of this game state with specified fields modified.
    *
    * This is the primary way to update the game state since it is immutable.
    * Any field not specified will retain its current value.
    *
    * @param players
    *   The players in the game
    * @param deck
    *   The remaining deck
    * @param table
    *   The cards on the table
    * @param discardPile
    *   The discarded cards
    * @param trumpCard
    *   The trump card
    * @param attackerIndex
    *   The main attacker's index
    * @param defenderIndex
    *   The defender's index
    * @param gamePhase
    *   The current game phase
    * @param lastEvent
    *   The last event that occurred
    * @param passedPlayers
    *   Set of players who passed
    * @param roundWinner
    *   The round winner's index
    * @param setupPlayerCount
    *   The player count from setup
    * @param setupPlayerNames
    *   The player names from setup
    * @param setupDeckSize
    *   The deck size from setup
    * @param currentAttackerIndex
    *   The current attacker's index
    * @param lastAttackerIndex
    *   The last attacker's index
    * @return
    *   A new GameState with the specified modifications
    */
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
  ): GameState

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
