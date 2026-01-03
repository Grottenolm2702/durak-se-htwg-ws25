package de.htwg.DurakApp.model.builder.impl

import de.htwg.DurakApp.model.{Card, Player, Rank, Suit, GameStateFactory}
import de.htwg.DurakApp.model.state.{GameEvent, GamePhase, SetupPhase}
import com.google.inject.Inject

object GameStateBuilder {
  def apply(
      gameStateFactory: GameStateFactory
  ): de.htwg.DurakApp.model.builder.GameStateBuilder =
    new GameStateBuilderImpl(gameStateFactory = gameStateFactory)
}

private[model] case class GameStateBuilderImpl(
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
    setupDeckSize: Option[Int] = None,
    currentAttackerIndex: Option[Int] = None,
    lastAttackerIndex: Option[Int] = None,
    gameStateFactory: GameStateFactory
) extends de.htwg.DurakApp.model.builder.GameStateBuilder {

  def withPlayers(
      newPlayers: List[Player]
  ): de.htwg.DurakApp.model.builder.GameStateBuilder =
    copy(players = newPlayers)

  def withDeck(
      newDeck: List[Card]
  ): de.htwg.DurakApp.model.builder.GameStateBuilder =
    copy(deck = newDeck)

  def withTable(
      newTable: Map[Card, Option[Card]]
  ): de.htwg.DurakApp.model.builder.GameStateBuilder =
    copy(table = newTable)

  def withDiscardPile(
      newDiscardPile: List[Card]
  ): de.htwg.DurakApp.model.builder.GameStateBuilder =
    copy(discardPile = newDiscardPile)

  def withTrumpCard(
      newTrumpCard: Card
  ): de.htwg.DurakApp.model.builder.GameStateBuilder =
    copy(trumpCard = newTrumpCard)

  def withAttackerIndex(
      newAttackerIndex: Int
  ): de.htwg.DurakApp.model.builder.GameStateBuilder =
    copy(attackerIndex = newAttackerIndex)

  def withDefenderIndex(
      newDefenderIndex: Int
  ): de.htwg.DurakApp.model.builder.GameStateBuilder =
    copy(defenderIndex = newDefenderIndex)

  def withGamePhase(
      newGamePhase: GamePhase
  ): de.htwg.DurakApp.model.builder.GameStateBuilder =
    copy(gamePhase = newGamePhase)

  def withLastEvent(
      newEvent: Option[GameEvent]
  ): de.htwg.DurakApp.model.builder.GameStateBuilder =
    copy(lastEvent = newEvent)

  def withPassedPlayers(
      newPassedPlayers: Set[Int]
  ): de.htwg.DurakApp.model.builder.GameStateBuilder =
    copy(passedPlayers = newPassedPlayers)

  def withRoundWinner(
      newRoundWinner: Option[Int]
  ): de.htwg.DurakApp.model.builder.GameStateBuilder =
    copy(roundWinner = newRoundWinner)

  def withSetupPlayerCount(
      count: Option[Int]
  ): de.htwg.DurakApp.model.builder.GameStateBuilder =
    copy(setupPlayerCount = count)

  def withSetupPlayerNames(
      names: List[String]
  ): de.htwg.DurakApp.model.builder.GameStateBuilder =
    copy(setupPlayerNames = names)

  def withSetupDeckSize(
      size: Option[Int]
  ): de.htwg.DurakApp.model.builder.GameStateBuilder =
    copy(setupDeckSize = size)

  def withCurrentAttackerIndex(
      index: Option[Int]
  ): de.htwg.DurakApp.model.builder.GameStateBuilder =
    copy(currentAttackerIndex = index)

  def withLastAttackerIndex(
      index: Option[Int]
  ): de.htwg.DurakApp.model.builder.GameStateBuilder =
    copy(lastAttackerIndex = index)

  def build(): de.htwg.DurakApp.model.GameState = {
    gameStateFactory(
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
      setupDeckSize = setupDeckSize,
      currentAttackerIndex = currentAttackerIndex,
      lastAttackerIndex = lastAttackerIndex
    )
  }
}
