package de.htwg.DurakApp.testutil

import de.htwg.DurakApp.model.{GameState, Card, Player}
import de.htwg.DurakApp.model.state.{GamePhase, GameEvent}
import de.htwg.DurakApp.model.state.impl.*

object RealPhaseTestHelper:
  val cardFactory = new StubCardFactory()
  val playerFactory = new StubPlayerFactory()

  def createGameStateWithRealPhases(
      players: List[Player] = List(playerFactory("P1"), playerFactory("P2")),
      deck: List[Card] = List.empty,
      table: Map[Card, Option[Card]] = Map.empty,
      discardPile: List[Card] = List.empty,
      trumpCard: Card = cardFactory(
        de.htwg.DurakApp.model.Suit.Hearts,
        de.htwg.DurakApp.model.Rank.Six,
        isTrump = true
      ),
      attackerIndex: Int = 0,
      defenderIndex: Int = 1,
      gamePhase: GamePhase = SetupPhaseImpl,
      lastEvent: Option[GameEvent] = None,
      passedPlayers: Set[Int] = Set.empty,
      roundWinner: Option[Int] = None,
      setupPlayerCount: Option[Int] = None,
      setupPlayerNames: List[String] = List.empty,
      setupDeckSize: Option[Int] = None,
      currentAttackerIndex: Option[Int] = None,
      lastAttackerIndex: Option[Int] = None
  ): GameState =
    new StubGameState(
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
