package de.htwg.DurakApp.testutil

import de.htwg.DurakApp.model.state.{GamePhase, GamePhases, GameEvent}
import de.htwg.DurakApp.model.{GameState, Card}

class StubGamePhase(val phaseName: String) extends GamePhase:
  def handle(gameState: GameState): GameState = gameState
  
  override def playCard(card: Card, playerIdx: Int, gameState: GameState): GameState =
    gameState.copy(lastEvent = Some(GameEvent.InvalidMove))
  
  override def pass(playerIdx: Int, gameState: GameState): GameState =
    gameState.copy(lastEvent = Some(GameEvent.InvalidMove))
  
  override def takeCards(playerIdx: Int, gameState: GameState): GameState =
    gameState.copy(lastEvent = Some(GameEvent.InvalidMove))
  
  override def toString: String = phaseName

object StubGamePhases:
  val setupPhase: GamePhase = new StubGamePhase("SetupPhase")
  val askPlayerCountPhase: GamePhase = new StubGamePhase("AskPlayerCountPhase")
  val askPlayerNamesPhase: GamePhase = new StubGamePhase("AskPlayerNamesPhase")
  val askDeckSizePhase: GamePhase = new StubGamePhase("AskDeckSizePhase")
  val askPlayAgainPhase: GamePhase = new StubGamePhase("AskPlayAgainPhase")
  val gameStartPhase: GamePhase = new StubGamePhase("GameStartPhase")
  val attackPhase: GamePhase = new StubGamePhase("AttackPhase")
  val defensePhase: GamePhase = new StubGamePhase("DefensePhase")
  val drawPhase: GamePhase = new StubGamePhase("DrawPhase")
  val roundPhase: GamePhase = new StubGamePhase("RoundPhase")
  val endPhase: GamePhase = new StubGamePhase("EndPhase")

class StubGamePhasesImpl extends GamePhases:
  def setupPhase = StubGamePhases.setupPhase
  def askPlayerCountPhase = StubGamePhases.askPlayerCountPhase
  def askPlayerNamesPhase = StubGamePhases.askPlayerNamesPhase
  def askDeckSizePhase = StubGamePhases.askDeckSizePhase
  def askPlayAgainPhase = StubGamePhases.askPlayAgainPhase
  def gameStartPhase = StubGamePhases.gameStartPhase
  def attackPhase = StubGamePhases.attackPhase
  def defensePhase = StubGamePhases.defensePhase
  def drawPhase = StubGamePhases.drawPhase
  def roundPhase = StubGamePhases.roundPhase
  def endPhase = StubGamePhases.endPhase
