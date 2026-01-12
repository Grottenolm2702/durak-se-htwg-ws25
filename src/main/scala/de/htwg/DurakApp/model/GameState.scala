package de.htwg.DurakApp.model

import de.htwg.DurakApp.model.state.{GameEvent, GamePhase}
import de.htwg.DurakApp.model.builder.GameStateBuilder

/** Case class representing the complete state of a Durak game.
  *
  * This immutable data structure contains all information about the current
  * game including players, cards, current phase, and game progress.
  */
case class GameState(
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
):
  /** Converts this game state to a builder for further modifications.
    * @return
    *   A GameStateBuilder initialized with this state's values
    */
  def toBuilder(builder: GameStateBuilder): GameStateBuilder =
    builder
      .withPlayers(players)
      .withDeck(deck)
      .withTable(table)
      .withDiscardPile(discardPile)
      .withTrumpCard(trumpCard)
      .withAttackerIndex(attackerIndex)
      .withDefenderIndex(defenderIndex)
      .withGamePhase(gamePhase)
      .withLastEvent(lastEvent)
      .withPassedPlayers(passedPlayers)
      .withRoundWinner(roundWinner)
      .withSetupPlayerCount(setupPlayerCount)
      .withSetupPlayerNames(setupPlayerNames)
      .withSetupDeckSize(setupDeckSize)
      .withCurrentAttackerIndex(currentAttackerIndex)
      .withLastAttackerIndex(lastAttackerIndex)
