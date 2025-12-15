package de.htwg.DurakApp.model

import de.htwg.DurakApp.model.{
  Card as InternalCard,
  Player as InternalPlayer,
  GameState as InternalGameState,
  Rank as InternalRank,
  Suit as InternalSuit
}
import de.htwg.DurakApp.model.state.{
  GamePhase as InternalGamePhase,
  GameEvent as InternalGameEvent,
  StateInterface as InternalStateInterface
}
import de.htwg.DurakApp.model.builder.{
  GameStateBuilder as InternalGameStateBuilder
}

/** Model Component Interface
  *
  * This is the public port to the Model component. All external access to model
  * types must go through this interface. The Model component encapsulates the
  * game's domain logic including cards, players, game state, and phase
  * management.
  *
  * Provides factory methods for creating model objects through traits only.
  * Implementation details are hidden in the impl package, following the
  * component-based architecture pattern.
  *
  * @example
  *   {{{
  * import de.htwg.DurakApp.model.ModelInterface.*
  *
  * val card = Card(Suit.Hearts, Rank.Ace)
  * val player = Player("Alice", List(card))
  * val gameState = GameStateBuilder()
  *   .withPlayers(List(player))
  *   .withDeck(List())
  *   .build()
  *   }}}
  */
object ModelInterface:

  // Type Aliases

  /** Type alias for Card. Represents a playing card with suit and rank. */
  type Card = InternalCard

  /** Type alias for Player. Represents a player with name and hand of cards. */
  type Player = InternalPlayer

  /** Type alias for GameState. Represents the complete state of the game. */
  type GameState = InternalGameState

  /** Type alias for Rank. Enum of card ranks (Six, Seven, ..., Ace). */
  type Rank = InternalRank

  /** Type alias for Suit. Enum of card suits (Hearts, Diamonds, Clubs, Spades).
    */
  type Suit = InternalSuit

  /** Type alias for GamePhase. Represents the current phase of the game. */
  type GamePhase = InternalGamePhase

  /** Type alias for GameEvent. Represents events that occur during gameplay. */
  type GameEvent = InternalGameEvent

  /** Type alias for StateInterface. Provides access to state types and phases.
    */
  type StateInterface = InternalStateInterface.type

  // Enum Values

  /** Enum object containing all card ranks (Six through Ace). */
  val Rank = InternalRank

  /** Enum object containing all card suits (Hearts, Diamonds, Clubs, Spades).
    */
  val Suit = InternalSuit

  /** Enum object containing all game events that can occur during gameplay. */
  val GameEvent = de.htwg.DurakApp.model.state.GameEvent

  /** Interface object providing access to game phases and state types. */
  val StateInterface = InternalStateInterface

  // Factory Objects

  /** Factory for creating GameStateBuilder instances.
    *
    * The builder provides a fluent API for constructing GameState objects with
    * all required and optional parameters.
    *
    * @example
    *   {{{
    * val builder = GameStateBuilder()
    *   .withPlayers(playerList)
    *   .withDeck(cardList)
    *   .withTrumpCard(trumpCard)
    * val gameState = builder.build()
    *   }}}
    */
  object GameStateBuilder:
    /** Creates a new GameStateBuilder instance.
      *
      * @return
      *   A new builder for constructing GameState objects
      */
    def apply(): InternalGameStateBuilder =
      InternalGameStateBuilder()

  /** Factory for creating Card instances.
    *
    * Cards are immutable and represent a playing card with a suit, rank, and
    * optional trump status.
    */
  object Card:
    /** Creates a new Card.
      *
      * @param suit
      *   The suit of the card (Hearts, Diamonds, Clubs, or Spades)
      * @param rank
      *   The rank of the card (Six through Ace)
      * @param isTrump
      *   Whether this card is a trump card (default: false)
      * @return
      *   A new Card instance
      */
    def apply(
        suit: Suit,
        rank: Rank,
        isTrump: Boolean = false
    ): InternalCard =
      InternalCard(suit, rank, isTrump)

  /** Factory for creating Player instances.
    *
    * Players have a name, a hand of cards, and a done status indicating whether
    * they have no more cards and have finished the game.
    */
  object Player:
    /** Creates a new Player.
      *
      * @param name
      *   The player's name
      * @param hand
      *   The cards currently in the player's hand (default: empty)
      * @param isDone
      *   Whether the player has finished (no cards left) (default: false)
      * @return
      *   A new Player instance
      */
    def apply(
        name: String,
        hand: List[Card] = List(),
        isDone: Boolean = false
    ): InternalPlayer =
      InternalPlayer(name, hand, isDone)

  /** Factory for creating GameState instances.
    *
    * GameState represents the complete state of a Durak game including all
    * players, cards, current phase, and game progress.
    */
  object GameState:
    /** Creates a new GameState.
      *
      * @param players
      *   List of all players in the game
      * @param deck
      *   The remaining cards in the deck
      * @param table
      *   Map of attack cards to their optional defense cards
      * @param discardPile
      *   Cards that have been played and discarded
      * @param trumpCard
      *   The trump card determining the trump suit
      * @param attackerIndex
      *   Index of the current main attacker
      * @param defenderIndex
      *   Index of the current defender
      * @param gamePhase
      *   The current phase of the game
      * @param lastEvent
      *   The last event that occurred (default: None)
      * @param passedPlayers
      *   Set of player indices who have passed this round (default: empty)
      * @param roundWinner
      *   Index of the round winner, if any (default: None)
      * @param setupPlayerCount
      *   Player count from setup phase (default: None)
      * @param setupPlayerNames
      *   Player names from setup phase (default: empty)
      * @param setupDeckSize
      *   Deck size from setup phase (default: None)
      * @param currentAttackerIndex
      *   Index of the current active attacker (default: None)
      * @param lastAttackerIndex
      *   Index of the last player who attacked (default: None)
      * @return
      *   A new GameState instance
      */
    def apply(
        players: List[Player],
        deck: List[Card],
        table: Map[Card, Option[Card]],
        discardPile: List[Card],
        trumpCard: Card,
        attackerIndex: Int,
        defenderIndex: Int,
        gamePhase: InternalGamePhase,
        lastEvent: Option[InternalGameEvent] = None,
        passedPlayers: Set[Int] = Set.empty,
        roundWinner: Option[Int] = None,
        setupPlayerCount: Option[Int] = None,
        setupPlayerNames: List[String] = List.empty,
        setupDeckSize: Option[Int] = None,
        currentAttackerIndex: Option[Int] = None,
        lastAttackerIndex: Option[Int] = None
    ): InternalGameState =
      InternalGameState(
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
