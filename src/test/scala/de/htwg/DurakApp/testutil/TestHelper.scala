package de.htwg.DurakApp.testutil
import de.htwg.DurakApp.model.{GameState, Card, Player, Suit, Rank}
import de.htwg.DurakApp.model.state.{GamePhase, GameEvent}
object TestHelper:
  val gameStateBuilderFactory = new StubGameStateBuilderFactory()
  val gamePhases = StubGamePhases
  def Card(suit: Suit, rank: Rank, isTrump: Boolean = false): Card =
    de.htwg.DurakApp.model.Card(suit, rank, isTrump)
  def Player(
      name: String,
      hand: List[Card] = List.empty,
      isDone: Boolean = false
  ): Player =
    de.htwg.DurakApp.model.Player(name, hand, isDone)
  def GameState(
      players: List[Player],
      deck: List[Card],
      table: Map[Card, Option[Card]],
      discardPile: List[Card],
      trumpCard: Card,
      mainAttackerIndex: Int,
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
    de.htwg.DurakApp.model.GameState(
      players,
      mainAttackerIndex,
      defenderIndex,
      currentAttackerIndex,
      lastAttackerIndex,
      passedPlayers,
      roundWinner,
      deck,
      table,
      discardPile,
      trumpCard,
      gamePhase,
      lastEvent,
      setupPlayerCount,
      setupPlayerNames,
      setupDeckSize
    )
  def createTestGameState(
      players: List[Player] = List(Player("P1"), Player("P2")),
      deck: List[Card] = List.empty,
      table: Map[Card, Option[Card]] = Map.empty,
      discardPile: List[Card] = List.empty,
      trumpCard: Card = Card(Suit.Hearts, Rank.Six, isTrump = true),
      mainAttackerIndex: Int = 0,
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
    de.htwg.DurakApp.model.GameState(
      players,
      mainAttackerIndex,
      defenderIndex,
      currentAttackerIndex,
      lastAttackerIndex,
      passedPlayers,
      roundWinner,
      deck,
      table,
      discardPile,
      trumpCard,
      gamePhase,
      lastEvent,
      setupPlayerCount,
      setupPlayerNames,
      setupDeckSize
    )
