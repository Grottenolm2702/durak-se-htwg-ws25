package de.htwg.DurakApp.model.state

trait GamePhases {
  def setupPhase: SetupPhase
  def askPlayerCountPhase: AskPlayerCountPhase
  def askPlayerNamesPhase: AskPlayerNamesPhase
  def askDeckSizePhase: AskDeckSizePhase
  def askPlayAgainPhase: AskPlayAgainPhase
  def gameStartPhase: GameStartPhase
  def attackPhase: AttackPhase
  def defensePhase: DefensePhase
  def drawPhase: DrawPhase
  def roundPhase: RoundPhase
  def endPhase: EndPhase

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
