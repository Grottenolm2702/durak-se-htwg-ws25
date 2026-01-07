package de.htwg.DurakApp.testutil

import de.htwg.DurakApp.model.{GameState, GameStateFactory, Card, Player}
import de.htwg.DurakApp.model.state.{GamePhase, GameEvent}
import de.htwg.DurakApp.model.builder.GameStateBuilder

private class StubGameState(
    val players: List[Player],
    val deck: List[Card],
    val table: Map[Card, Option[Card]],
    val discardPile: List[Card],
    val trumpCard: Card,
    val attackerIndex: Int,
    val defenderIndex: Int,
    val gamePhase: GamePhase,
    val lastEvent: Option[GameEvent],
    val passedPlayers: Set[Int],
    val roundWinner: Option[Int],
    val setupPlayerCount: Option[Int],
    val setupPlayerNames: List[String],
    val setupDeckSize: Option[Int],
    val currentAttackerIndex: Option[Int],
    val lastAttackerIndex: Option[Int]
) extends GameState:
  
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
  ): GameState =
    new StubGameState(
      players, deck, table, discardPile, trumpCard, attackerIndex, defenderIndex,
      gamePhase, lastEvent, passedPlayers, roundWinner, setupPlayerCount,
      setupPlayerNames, setupDeckSize, currentAttackerIndex, lastAttackerIndex
    )
  
  def toBuilder: GameStateBuilder =
    val gameStateFactory = new StubGameStateFactory()
    val builderFactory = new StubGameStateBuilderFactory(
      new StubCardFactory(),
      new StubPlayerFactory(),
      gameStateFactory
    )
    builderFactory.create()
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

  override def equals(obj: Any): Boolean = obj match
    case that: StubGameState =>
      this.players == that.players &&
      this.deck == that.deck &&
      this.table == that.table &&
      this.discardPile == that.discardPile &&
      this.trumpCard == that.trumpCard &&
      this.attackerIndex == that.attackerIndex &&
      this.defenderIndex == that.defenderIndex &&
      this.gamePhase == that.gamePhase &&
      this.lastEvent == that.lastEvent &&
      this.passedPlayers == that.passedPlayers &&
      this.roundWinner == that.roundWinner &&
      this.setupPlayerCount == that.setupPlayerCount &&
      this.setupPlayerNames == that.setupPlayerNames &&
      this.setupDeckSize == that.setupDeckSize &&
      this.currentAttackerIndex == that.currentAttackerIndex &&
      this.lastAttackerIndex == that.lastAttackerIndex
    case _ => false

  override def hashCode(): Int =
    val state = Seq(
      players, deck, table, discardPile, trumpCard, attackerIndex, defenderIndex,
      gamePhase, lastEvent, passedPlayers, roundWinner, setupPlayerCount,
      setupPlayerNames, setupDeckSize, currentAttackerIndex, lastAttackerIndex
    )
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)

class StubGameStateFactory extends GameStateFactory:
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
  ): GameState =
    new StubGameState(
      players, deck, table, discardPile, trumpCard, attackerIndex, defenderIndex,
      gamePhase, lastEvent, passedPlayers, roundWinner, setupPlayerCount,
      setupPlayerNames, setupDeckSize, currentAttackerIndex, lastAttackerIndex
    )
