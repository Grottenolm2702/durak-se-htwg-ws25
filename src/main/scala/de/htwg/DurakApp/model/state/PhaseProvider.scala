package de.htwg.DurakApp.model.state

object PhaseProvider:
  def setupPhase: GamePhase = SetupPhase
  def askPlayerCountPhase: GamePhase = AskPlayerCountPhase
  def askPlayerNamesPhase: GamePhase = AskPlayerNamesPhase
  def askDeckSizePhase: GamePhase = AskDeckSizePhase
  def askPlayAgainPhase: GamePhase = AskPlayAgainPhase
  def gameStartPhase: GamePhase = GameStartPhase
  def attackPhase: GamePhase = AttackPhase
  def defensePhase: GamePhase = DefensePhase
  def drawPhase: GamePhase = DrawPhase
  def roundPhase: GamePhase = RoundPhase
  def endPhase: GamePhase = EndPhase
