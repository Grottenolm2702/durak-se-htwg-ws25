package de.htwg.DurakApp.model

/** Model Component Interface
  *
  * This is the public port to the Model component. All external access to model
  * types must go through this interface.
  *
  * Exports:
  *   - Basic types: Card, Player, Rank, Suit, GameState
  *   - Builder: GameStateBuilder
  *   - Game phases: All phase implementations
  *   - Events: GameEvent
  */
object ModelInterface:
  export de.htwg.DurakApp.model.{Card, Player, Rank, Suit, GameState}
  export de.htwg.DurakApp.model.builder.GameStateBuilder
  export de.htwg.DurakApp.model.state.{
    GamePhase,
    GameEvent,
    SetupPhase,
    AskPlayerCountPhase,
    AskPlayerNamesPhase,
    AskDeckSizePhase,
    AskPlayAgainPhase,
    GameStartPhase,
    AttackPhase,
    DefensePhase,
    DrawPhase,
    RoundPhase,
    EndPhase
  }

/** GameState Interface
  *
  * Defines the external contract for accessing game state. All implementations
  * must provide these methods.
  */
trait GameStateInterface:
  def players: List[Player]
  def deck: List[Card]
  def table: Map[Card, Option[Card]]
  def discardPile: List[Card]
  def trumpCard: Card
  def attackerIndex: Int
  def defenderIndex: Int
  def gamePhase: de.htwg.DurakApp.model.state.GamePhase
  def toBuilder: de.htwg.DurakApp.model.builder.GameStateBuilder
