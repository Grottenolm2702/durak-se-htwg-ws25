package de.htwg.DurakApp.model.builder

import de.htwg.DurakApp.model.{Card, Player, GameState, GameStateFactory}
import de.htwg.DurakApp.model.state.{GameEvent, GamePhase}
import scala.util.Try

/** Builder for constructing GameState instances with fluent API.
  *
  * Instances are created through GameStateBuilder.apply() (via DI).
  * Dependencies are injected via constructor parameters.
  */
trait GameStateBuilder {
  def withPlayers(newPlayers: List[Player]): GameStateBuilder
  def withDeck(newDeck: List[Card]): GameStateBuilder
  def withTable(newTable: Map[Card, Option[Card]]): GameStateBuilder
  def withDiscardPile(newDiscardPile: List[Card]): GameStateBuilder
  def withTrumpCard(newTrumpCard: Card): GameStateBuilder
  def withAttackerIndex(newAttackerIndex: Int): GameStateBuilder
  def withDefenderIndex(newDefenderIndex: Int): GameStateBuilder
  def withGamePhase(newGamePhase: GamePhase): GameStateBuilder
  def withLastEvent(newEvent: Option[GameEvent]): GameStateBuilder
  def withPassedPlayers(newPassedPlayers: Set[Int]): GameStateBuilder
  def withRoundWinner(newRoundWinner: Option[Int]): GameStateBuilder
  def withSetupPlayerCount(count: Option[Int]): GameStateBuilder
  def withSetupPlayerNames(names: List[String]): GameStateBuilder
  def withSetupDeckSize(size: Option[Int]): GameStateBuilder
  def withCurrentAttackerIndex(index: Option[Int]): GameStateBuilder
  def withLastAttackerIndex(index: Option[Int]): GameStateBuilder
  def withGameStateFactory(factory: GameStateFactory): GameStateBuilder
  def build(): Try[GameState]
}
