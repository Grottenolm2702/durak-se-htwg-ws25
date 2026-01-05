package de.htwg.DurakApp.model.state

trait GamePhases {
  def setupPhase: GamePhase
  def askPlayerCountPhase: GamePhase
  def askPlayerNamesPhase: GamePhase
  def askDeckSizePhase: GamePhase
  def askPlayAgainPhase: GamePhase
  def gameStartPhase: GamePhase
  def attackPhase: GamePhase
  def defensePhase: GamePhase
  def drawPhase: GamePhase
  def roundPhase: GamePhase
  def endPhase: GamePhase

  def isSetupPhase(phase: GamePhase): Boolean =
    phase == setupPhase || phase == askPlayerCountPhase
  def isAskPlayerNamesPhase(phase: GamePhase): Boolean =
    phase == askPlayerNamesPhase
  def isAskDeckSizePhase(phase: GamePhase): Boolean = phase == askDeckSizePhase
  def isGameStartPhase(phase: GamePhase): Boolean = phase == gameStartPhase
  def isAskPlayAgainPhase(phase: GamePhase): Boolean =
    phase == askPlayAgainPhase
  def isAttackPhase(phase: GamePhase): Boolean = phase == attackPhase
  def isDefensePhase(phase: GamePhase): Boolean = phase == defensePhase
  def isAnySetupPhase(phase: GamePhase): Boolean =
    phase == setupPhase || phase == askPlayerCountPhase ||
      phase == askPlayerNamesPhase || phase == askDeckSizePhase ||
      phase == gameStartPhase
}
