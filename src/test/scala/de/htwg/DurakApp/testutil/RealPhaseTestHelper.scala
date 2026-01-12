package de.htwg.DurakApp.testutil
import de.htwg.DurakApp.model.{GameState, Card, Player}
import de.htwg.DurakApp.model.state.{GamePhase, GameEvent}
import de.htwg.DurakApp.model.state.impl.*
object RealPhaseTestHelper:
  def createGameStateWithRealPhases(
      players: List[Player] = List(
        de.htwg.DurakApp.model.Player("P1"),
        de.htwg.DurakApp.model.Player("P2")
      ),
      deck: List[Card] = List.empty,
      table: Map[Card, Option[Card]] = Map.empty,
      discardPile: List[Card] = List.empty,
      trumpCard: Card = de.htwg.DurakApp.model.Card(
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
    de.htwg.DurakApp.model.GameState(
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
