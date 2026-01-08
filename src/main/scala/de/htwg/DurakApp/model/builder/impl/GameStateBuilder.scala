package de.htwg.DurakApp.model.builder.impl

import de.htwg.DurakApp.model.{
  Card,
  Player,
  Rank,
  Suit,
  GameState,
  GameStateFactory,
  CardFactory
}
import de.htwg.DurakApp.model.builder.GameStateBuilder
import de.htwg.DurakApp.model.state.{GameEvent, GamePhase, GamePhases}
import com.google.inject.Inject
import scala.util.{Try, Success, Failure}

object GameStateBuilder {
  def apply(
      cardFactory: CardFactory,
      gamePhases: GamePhases
  ): GameStateBuilder =
    new GameStateBuilderImpl(
      cardFactory = cardFactory,
      gamePhases = gamePhases
    )
}

private[model] case class GameStateBuilderImpl(
    players: List[Player] = List.empty,
    deck: List[Card] = List.empty,
    table: Map[Card, Option[Card]] = Map.empty,
    discardPile: List[Card] = List.empty,
    trumpCard: Option[Card] = None,
    attackerIndex: Int = 0,
    defenderIndex: Int = 1,
    gamePhase: Option[GamePhase] = None,
    lastEvent: Option[GameEvent] = None,
    passedPlayers: Set[Int] = Set.empty,
    roundWinner: Option[Int] = None,
    setupPlayerCount: Option[Int] = None,
    setupPlayerNames: List[String] = List.empty,
    setupDeckSize: Option[Int] = None,
    currentAttackerIndex: Option[Int] = None,
    lastAttackerIndex: Option[Int] = None,
    gameStateFactory: Option[GameStateFactory] = None,
    cardFactory: CardFactory,
    gamePhases: GamePhases
) extends GameStateBuilder {

  def withPlayers(
      newPlayers: List[Player]
  ): GameStateBuilder =
    copy(players = newPlayers)

  def withDeck(
      newDeck: List[Card]
  ): GameStateBuilder =
    copy(deck = newDeck)

  def withTable(
      newTable: Map[Card, Option[Card]]
  ): GameStateBuilder =
    copy(table = newTable)

  def withDiscardPile(
      newDiscardPile: List[Card]
  ): GameStateBuilder =
    copy(discardPile = newDiscardPile)

  def withTrumpCard(
      newTrumpCard: Card
  ): GameStateBuilder =
    copy(trumpCard = Some(newTrumpCard))

  def withAttackerIndex(
      newAttackerIndex: Int
  ): GameStateBuilder =
    copy(attackerIndex = newAttackerIndex)

  def withDefenderIndex(
      newDefenderIndex: Int
  ): GameStateBuilder =
    copy(defenderIndex = newDefenderIndex)

  def withGamePhase(
      newGamePhase: GamePhase
  ): GameStateBuilder =
    copy(gamePhase = Some(newGamePhase))

  def withLastEvent(
      newEvent: Option[GameEvent]
  ): GameStateBuilder =
    copy(lastEvent = newEvent)

  def withPassedPlayers(
      newPassedPlayers: Set[Int]
  ): GameStateBuilder =
    copy(passedPlayers = newPassedPlayers)

  def withRoundWinner(
      newRoundWinner: Option[Int]
  ): GameStateBuilder =
    copy(roundWinner = newRoundWinner)

  def withSetupPlayerCount(
      count: Option[Int]
  ): GameStateBuilder =
    copy(setupPlayerCount = count)

  def withSetupPlayerNames(
      names: List[String]
  ): GameStateBuilder =
    copy(setupPlayerNames = names)

  def withSetupDeckSize(
      size: Option[Int]
  ): GameStateBuilder =
    copy(setupDeckSize = size)

  def withCurrentAttackerIndex(
      index: Option[Int]
  ): GameStateBuilder =
    copy(currentAttackerIndex = index)

  def withLastAttackerIndex(
      index: Option[Int]
  ): GameStateBuilder =
    copy(lastAttackerIndex = index)

  def withGameStateFactory(
      factory: GameStateFactory
  ): GameStateBuilder =
    copy(gameStateFactory = Some(factory))

  def build(): Try[GameState] = {
    gameStateFactory match {
      case None =>
        Failure(
          new IllegalStateException(
            "GameStateFactory must be set before building"
          )
        )
      case Some(factory) =>
        Try {
          val defaultTrumpCard =
            trumpCard.getOrElse(cardFactory(Suit.Hearts, Rank.Six))
          val defaultGamePhase = gamePhase.getOrElse(gamePhases.setupPhase)

          factory(
            players = players,
            deck = deck,
            table = table,
            discardPile = discardPile,
            trumpCard = defaultTrumpCard,
            attackerIndex = attackerIndex,
            defenderIndex = defenderIndex,
            gamePhase = defaultGamePhase,
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
  }
}
