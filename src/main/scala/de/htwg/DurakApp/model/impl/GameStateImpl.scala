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
    lastEvent: Option[GameEvent] = None,
    passedPlayers: Set[Int] = Set.empty,
    roundWinner: Option[Int] = None,
    setupPlayerCount: Option[Int] = None,
    setupPlayerNames: List[String] = List.empty,
    setupDeckSize: Option[Int] = None,
    currentAttackerIndex: Option[Int] = None,
    lastAttackerIndex: Option[Int] = None
) extends GameState {

  def toBuilder: GameStateBuilder = {
    GameStateBuilder()
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
