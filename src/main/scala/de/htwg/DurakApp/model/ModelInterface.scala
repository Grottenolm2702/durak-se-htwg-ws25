package de.htwg.DurakApp.model

/** Model Component Interface
  *
  * This is the public port to the Model component. All external access to model
  * types must go through this interface.
  *
  * Provides factory methods for creating model objects through traits only.
  * Implementation details are hidden in the impl package.
  */
object ModelInterface:
  type Card = de.htwg.DurakApp.model.Card
  type Player = de.htwg.DurakApp.model.Player
  type GameState = de.htwg.DurakApp.model.GameState
  type Rank = de.htwg.DurakApp.model.Rank
  type Suit = de.htwg.DurakApp.model.Suit
  type GamePhase = de.htwg.DurakApp.model.state.GamePhase
  type GameEvent = de.htwg.DurakApp.model.state.GameEvent
  type StateInterface = de.htwg.DurakApp.model.state.StateInterface.type

  val Rank = de.htwg.DurakApp.model.Rank
  val Suit = de.htwg.DurakApp.model.Suit
  val StateInterface = de.htwg.DurakApp.model.state.StateInterface

  object GameStateBuilder:
    def apply(): de.htwg.DurakApp.model.builder.GameStateBuilder =
      de.htwg.DurakApp.model.builder.GameStateBuilder()

  object Card:
    def apply(
        suit: Suit,
        rank: Rank,
        isTrump: Boolean = false
    ): de.htwg.DurakApp.model.Card =
      de.htwg.DurakApp.model.Card(suit, rank, isTrump)

  object Player:
    def apply(
        name: String,
        hand: List[Card] = List(),
        isDone: Boolean = false
    ): de.htwg.DurakApp.model.Player =
      de.htwg.DurakApp.model.Player(name, hand, isDone)

  object GameState:
    def apply(
        players: List[Player],
        deck: List[Card],
        table: Map[Card, Option[Card]],
        discardPile: List[Card],
        trumpCard: Card,
        attackerIndex: Int,
        defenderIndex: Int,
        gamePhase: de.htwg.DurakApp.model.state.GamePhase,
        lastEvent: Option[de.htwg.DurakApp.model.state.GameEvent] = None,
        passedPlayers: Set[Int] = Set.empty,
        roundWinner: Option[Int] = None,
        setupPlayerCount: Option[Int] = None,
        setupPlayerNames: List[String] = List.empty,
        setupDeckSize: Option[Int] = None,
        currentAttackerIndex: Option[Int] = None,
        lastAttackerIndex: Option[Int] = None
    ): de.htwg.DurakApp.model.GameState =
      de.htwg.DurakApp.model.GameState(
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
