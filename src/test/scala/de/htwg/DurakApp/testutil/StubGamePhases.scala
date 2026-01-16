package de.htwg.DurakApp.testutil
import de.htwg.DurakApp.model.state.*
import de.htwg.DurakApp.model.{GameState, Card}
class StubGamePhase(val phaseName: String) extends GamePhase:
  def handle(gameState: GameState): GameState = gameState
  override def playCard(
      card: Card,
      playerIdx: Int,
      gameState: GameState
  ): GameState =
    gameState.copy(lastEvent = Some(GameEvent.InvalidMove))
  override def pass(playerIdx: Int, gameState: GameState): GameState =
    gameState.copy(lastEvent = Some(GameEvent.InvalidMove))
  override def takeCards(playerIdx: Int, gameState: GameState): GameState =
    gameState.copy(lastEvent = Some(GameEvent.InvalidMove))
  override def toString: String = phaseName
class StubGamePhaseWithTransition(phaseName: String, nextPhase: => GamePhase)
    extends StubGamePhase(phaseName):
  override def handle(gameState: GameState): GameState =
    gameState.copy(gamePhase = nextPhase, lastEvent = Some(GameEvent.Draw))
object StubGamePhases:
  val setupPhase: SetupPhase = new StubGamePhase("SetupPhase") with SetupPhase
  val askPlayerCountPhase: AskPlayerCountPhase = new StubGamePhase(
    "AskPlayerCountPhase"
  ) with AskPlayerCountPhase
  val askPlayerNamesPhase: AskPlayerNamesPhase = new StubGamePhase(
    "AskPlayerNamesPhase"
  ) with AskPlayerNamesPhase
  val askDeckSizePhase: AskDeckSizePhase = new StubGamePhase("AskDeckSizePhase")
    with AskDeckSizePhase
  val askPlayAgainPhase: AskPlayAgainPhase = new StubGamePhase(
    "AskPlayAgainPhase"
  ) with AskPlayAgainPhase
  val gameStartPhase: GameStartPhase = new StubGamePhase("GameStartPhase")
    with GameStartPhase
  val attackPhase: AttackPhase = new StubGamePhase("AttackPhase")
    with AttackPhase
  val defensePhase: DefensePhase = new StubGamePhase("DefensePhase")
    with DefensePhase
  val drawPhase: DrawPhase = new StubGamePhase("DrawPhase") with DrawPhase
  val roundPhase: RoundPhase = new StubGamePhase("RoundPhase") with RoundPhase
  val endPhase: EndPhase = new StubGamePhase("EndPhase") with EndPhase
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
