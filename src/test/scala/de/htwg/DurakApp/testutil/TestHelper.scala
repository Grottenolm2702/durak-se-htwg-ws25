package de.htwg.DurakApp.testutil

import de.htwg.DurakApp.model.{GameState, Card, Player, Suit, Rank}
import de.htwg.DurakApp.model.state.{GamePhase, GameEvent}

object TestHelper:
  val cardFactory = new StubCardFactory()
  val playerFactory = new StubPlayerFactory()
  val gameStateFactory = new StubGameStateFactory()
  val gameStateBuilderFactory = new StubGameStateBuilderFactory(
    cardFactory,
    playerFactory,
    gameStateFactory
  )
  val gamePhases = StubGamePhases

  def Card(suit: Suit, rank: Rank, isTrump: Boolean = false): Card =
    cardFactory(suit, rank, isTrump)

  def Player(
      name: String,
      hand: List[Card] = List.empty,
      isDone: Boolean = false
  ): Player =
    playerFactory(name, hand, isDone)

  def GameState(
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
    gameStateFactory(
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

  def createTestGameState(
      players: List[Player] = List(playerFactory("P1"), playerFactory("P2")),
      deck: List[Card] = List.empty,
      table: Map[Card, Option[Card]] = Map.empty,
      discardPile: List[Card] = List.empty,
      trumpCard: Card = cardFactory(Suit.Hearts, Rank.Six, isTrump = true),
      attackerIndex: Int = 0,
      defenderIndex: Int = 1,
      gamePhase: GamePhase = StubGamePhases.setupPhase,
      lastEvent: Option[GameEvent] = None,
      passedPlayers: Set[Int] = Set.empty,
      roundWinner: Option[Int] = None,
      setupPlayerCount: Option[Int] = None,
      setupPlayerNames: List[String] = List.empty,
      setupDeckSize: Option[Int] = None,
      currentAttackerIndex: Option[Int] = None,
      lastAttackerIndex: Option[Int] = None
  ): GameState =
    gameStateFactory(
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
