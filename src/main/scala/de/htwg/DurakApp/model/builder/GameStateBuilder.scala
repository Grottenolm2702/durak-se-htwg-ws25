package de.htwg.DurakApp.model.builder

import de.htwg.DurakApp.model.{Card, GameState, Player, Rank, Suit}
import de.htwg.DurakApp.model.state.{GameEvent, GamePhase, SetupPhase}

object GameStateBuilder {
  def apply(): GameStateBuilder = new GameStateBuilder()
}

case class GameStateBuilder(
    players: List[Player] = List.empty,
    deck: List[Card] = List.empty,
    table: Map[Card, Option[Card]] = Map.empty,
    discardPile: List[Card] = List.empty,
    trumpCard: Card = Card(Suit.Hearts, Rank.Six),
    attackerIndex: Int = 0,
    defenderIndex: Int = 1,
    gamePhase: GamePhase = SetupPhase,
    lastEvent: Option[GameEvent] = None,
    passedPlayers: Set[Int] = Set.empty,
    roundWinner: Option[Int] = None,
    setupPlayerCount: Option[Int] = None,
    setupPlayerNames: List[String] = List.empty,
    setupDeckSize: Option[Int] = None
) {

  def withPlayers(newPlayers: List[Player]): GameStateBuilder =
    copy(players = newPlayers)

  def withDeck(newDeck: List[Card]): GameStateBuilder =
    copy(deck = newDeck)

  def withTable(newTable: Map[Card, Option[Card]]): GameStateBuilder =
    copy(table = newTable)

  def withDiscardPile(newDiscardPile: List[Card]): GameStateBuilder =
    copy(discardPile = newDiscardPile)

  def withTrumpCard(newTrumpCard: Card): GameStateBuilder =
    copy(trumpCard = newTrumpCard)

  def withAttackerIndex(newAttackerIndex: Int): GameStateBuilder =
    copy(attackerIndex = newAttackerIndex)

  def withDefenderIndex(newDefenderIndex: Int): GameStateBuilder =
    copy(defenderIndex = newDefenderIndex)

  def withGamePhase(newGamePhase: GamePhase): GameStateBuilder =
    copy(gamePhase = newGamePhase)

  def withLastEvent(newEvent: Option[GameEvent]): GameStateBuilder =
    copy(lastEvent = newEvent)

  def withPassedPlayers(newPassedPlayers: Set[Int]): GameStateBuilder =
    copy(passedPlayers = newPassedPlayers)

  def withRoundWinner(newRoundWinner: Option[Int]): GameStateBuilder =
    copy(roundWinner = newRoundWinner)

  def withSetupPlayerCount(count: Option[Int]): GameStateBuilder =
    copy(setupPlayerCount = count)

  def withSetupPlayerNames(names: List[String]): GameStateBuilder =
    copy(setupPlayerNames = names)

  def withSetupDeckSize(size: Option[Int]): GameStateBuilder =
    copy(setupDeckSize = size)

  def build(): GameState = {
    GameState(
      players = players,
      deck = deck,
      table = table,
      discardPile = discardPile,
      trumpCard = trumpCard,
      attackerIndex = attackerIndex,
      defenderIndex = defenderIndex,
      gamePhase = gamePhase,
      lastEvent = lastEvent,
      passedPlayers = passedPlayers,
      roundWinner = roundWinner,
      setupPlayerCount = setupPlayerCount,
      setupPlayerNames = setupPlayerNames,
      setupDeckSize = setupDeckSize
    )
  }
}
