package de.htwg.DurakApp.model.impl

import de.htwg.DurakApp.model.{GameState, Player, Card}
import de.htwg.DurakApp.model.state.{GameEvent, GamePhase}
import de.htwg.DurakApp.model.builder.GameStateBuilder

private[model] case class GameStateImpl(
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
) extends GameState {

  def toBuilder: GameStateBuilder = {
    // Create builder implementation directly since we're in the impl package
    de.htwg.DurakApp.model.builder.impl.GameStateBuilder()
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
  }
}
