package de.htwg.DurakApp.model.impl

import de.htwg.DurakApp.model._
import de.htwg.DurakApp.model.builder.GameStateBuilderFactory
import de.htwg.DurakApp.model.state.{GameEvent, GamePhase, GamePhases}
import com.google.inject.Inject

class CardFactoryImpl extends CardFactory:
  def apply(suit: Suit, rank: Rank, isTrump: Boolean = false): Card =
    CardImpl(suit, rank, isTrump)

class PlayerFactoryImpl extends PlayerFactory:
  def apply(
      name: String,
      hand: List[Card] = List(),
      isDone: Boolean = false
  ): Player =
    PlayerImpl(name, hand, isDone)

class GameStateFactoryImpl @Inject() (
    gamePhases: GamePhases,
    cardFactory: CardFactory,
    playerFactory: PlayerFactory,
    gameStateBuilderFactory: GameStateBuilderFactory
) extends GameStateFactory:
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
    GameStateImpl(
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
      lastAttackerIndex,
      gamePhases,
      cardFactory,
      playerFactory,
      gameStateBuilderFactory.create()
    )
