package de.htwg.DurakApp.testutil

import de.htwg.DurakApp.model.state.{GamePhase, GameEvent}
import de.htwg.DurakApp.model.{GameState, Card}

class SpyGamePhase(val phaseName: String) extends GamePhase:
  private var _handleCalls: List[GameState] = List.empty
  private var _playCardCalls: List[(Card, Int, GameState)] = List.empty
  private var _passCalls: List[(Int, GameState)] = List.empty
  private var _takeCardsCalls: List[(Int, GameState)] = List.empty
  
  def handleCalls: List[GameState] = _handleCalls
  def playCardCalls: List[(Card, Int, GameState)] = _playCardCalls
  def passCalls: List[(Int, GameState)] = _passCalls
  def takeCardsCalls: List[(Int, GameState)] = _takeCardsCalls
  
  def handle(gameState: GameState): GameState =
    _handleCalls = _handleCalls :+ gameState
    gameState
  
  override def playCard(card: Card, playerIdx: Int, gameState: GameState): GameState =
    _playCardCalls = _playCardCalls :+ (card, playerIdx, gameState)
    gameState.copy(lastEvent = Some(GameEvent.InvalidMove))
  
  override def pass(playerIdx: Int, gameState: GameState): GameState =
    _passCalls = _passCalls :+ (playerIdx, gameState)
    gameState.copy(lastEvent = Some(GameEvent.InvalidMove))
  
  override def takeCards(playerIdx: Int, gameState: GameState): GameState =
    _takeCardsCalls = _takeCardsCalls :+ (playerIdx, gameState)
    gameState.copy(lastEvent = Some(GameEvent.InvalidMove))
