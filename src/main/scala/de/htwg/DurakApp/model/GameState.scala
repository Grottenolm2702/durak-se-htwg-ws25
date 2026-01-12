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
    mainAttackerIndex: Int,
    defenderIndex: Int,
    currentAttackerIndex: Option[Int],
    lastAttackerIndex: Option[Int],
    passedPlayers: Set[Int],
    roundWinner: Option[Int],
    deck: List[Card],
    table: Map[Card, Option[Card]],
    discardPile: List[Card],
    trumpCard: Card,
    gamePhase: GamePhase,
    lastEvent: Option[GameEvent],
    setupPlayerCount: Option[Int],
    setupPlayerNames: List[String],
    setupDeckSize: Option[Int]
):
  /** Converts this game state to a builder for further modifications.
    * @return
    *   A GameStateBuilder initialized with this state's values
    */
  def toBuilder(builder: GameStateBuilder): GameStateBuilder =
    builder
      .withPlayers(players)
      .withMainAttackerIndex(mainAttackerIndex)
      .withDefenderIndex(defenderIndex)
      .withCurrentAttackerIndex(currentAttackerIndex)
      .withLastAttackerIndex(lastAttackerIndex)
      .withPassedPlayers(passedPlayers)
      .withRoundWinner(roundWinner)
      .withDeck(deck)
      .withTable(table)
      .withDiscardPile(discardPile)
      .withTrumpCard(trumpCard)
      .withGamePhase(gamePhase)
      .withLastEvent(lastEvent)
      .withSetupPlayerCount(setupPlayerCount)
      .withSetupPlayerNames(setupPlayerNames)
      .withSetupDeckSize(setupDeckSize)
