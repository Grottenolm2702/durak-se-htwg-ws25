package de.htwg.DurakApp.model.state

import de.htwg.DurakApp.model.state.{
  GamePhase as InternalGamePhase,
  GameEvent as InternalGameEvent,
  SetupPhase as InternalSetupPhase,
  AskPlayerCountPhase as InternalAskPlayerCountPhase,
  AskPlayerNamesPhase as InternalAskPlayerNamesPhase,
  AskDeckSizePhase as InternalAskDeckSizePhase,
  AskPlayAgainPhase as InternalAskPlayAgainPhase,
  GameStartPhase as InternalGameStartPhase,
  AttackPhase as InternalAttackPhase,
  DefensePhase as InternalDefensePhase,
  DrawPhase as InternalDrawPhase,
  RoundPhase as InternalRoundPhase,
  EndPhase as InternalEndPhase
}

/** State Component Interface
  *
  * This is the public port to the State component. All external access to state
  * types must go through this interface. The State component manages the game's
  * phase transitions and state behavior according to the Durak game rules.
  *
  * @example
  *   {{{
  * import de.htwg.DurakApp.model.ModelInterface.StateInterface.*
  *
  * val gameState = ... // some game state
  * gameState.gamePhase match {
  *   case AttackPhase => // handle attack phase
  *   case DefensePhase => // handle defense phase
  *   case _ => // handle other phases
  * }
  *   }}}
  */
object StateInterface:

  // Type Aliases

  /** Type alias for the game phase trait. Represents the current phase of the
    * game (e.g., Attack, Defense, Draw).
    */
  type GamePhase = InternalGamePhase

  /** Type alias for game events. Events represent state changes and actions in
    * the game.
    */
  type GameEvent = InternalGameEvent

  // Phase Types

  /** Type alias for SetupPhase. */
  type SetupPhase = InternalSetupPhase.type

  /** Type alias for AskPlayerCountPhase. */
  type AskPlayerCountPhase = InternalAskPlayerCountPhase.type

  /** Type alias for AskPlayerNamesPhase. */
  type AskPlayerNamesPhase = InternalAskPlayerNamesPhase.type

  /** Type alias for AskDeckSizePhase. */
  type AskDeckSizePhase = InternalAskDeckSizePhase.type

  /** Type alias for AskPlayAgainPhase. */
  type AskPlayAgainPhase = InternalAskPlayAgainPhase.type

  /** Type alias for GameStartPhase. */
  type GameStartPhase = InternalGameStartPhase.type

  /** Type alias for AttackPhase. */
  type AttackPhase = InternalAttackPhase.type

  /** Type alias for DefensePhase. */
  type DefensePhase = InternalDefensePhase.type

  /** Type alias for DrawPhase. */
  type DrawPhase = InternalDrawPhase.type

  /** Type alias for RoundPhase. */
  type RoundPhase = InternalRoundPhase.type

  /** Type alias for EndPhase. */
  type EndPhase = InternalEndPhase.type

  // Enum Values

  /** Factory object for creating game events. Provides access to all event
    * types like Attack, Defend, Pass, etc.
    */
  val GameEvent = InternalGameEvent

  // Setup and Configuration Phases

  /** Initial setup phase where cards are dealt and trump is determined. */
  val SetupPhase = InternalSetupPhase

  /** Phase where the number of players is requested. */
  val AskPlayerCountPhase = InternalAskPlayerCountPhase

  /** Phase where player names are requested. */
  val AskPlayerNamesPhase = InternalAskPlayerNamesPhase

  /** Phase where the deck size is requested. */
  val AskDeckSizePhase = InternalAskDeckSizePhase

  /** Phase shown after game end, asking if players want to play again. */
  val AskPlayAgainPhase = InternalAskPlayAgainPhase

  /** Phase when the game starts. */
  val GameStartPhase = InternalGameStartPhase

  // Main Game Phases

  /** Phase where the attacker plays cards. */
  val AttackPhase = InternalAttackPhase

  /** Phase where the defender must defend or take cards. */
  val DefensePhase = InternalDefensePhase

  /** Phase where players draw cards to replenish their hands. */
  val DrawPhase = InternalDrawPhase

  /** Phase at the start of each round, checks for game end condition. */
  val RoundPhase = InternalRoundPhase

  /** Phase when the game ends and winner is determined. */
  val EndPhase = InternalEndPhase
