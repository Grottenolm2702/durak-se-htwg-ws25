package de.htwg.DurakApp.model.state.impl

import com.google.inject.Inject
import com.google.inject.name.Named
import de.htwg.DurakApp.model.state.{GamePhase, GamePhases}

private[state] class GamePhasesImpl @Inject() (
  @Named("SetupPhase") val setupPhase: GamePhase,
  @Named("AskPlayerCountPhase") val askPlayerCountPhase: GamePhase,
  @Named("AskPlayerNamesPhase") val askPlayerNamesPhase: GamePhase,
  @Named("AskDeckSizePhase") val askDeckSizePhase: GamePhase,
  @Named("AskPlayAgainPhase") val askPlayAgainPhase: GamePhase,
  @Named("GameStartPhase") val gameStartPhase: GamePhase,
  @Named("AttackPhase") val attackPhase: GamePhase,
  @Named("DefensePhase") val defensePhase: GamePhase,
  @Named("DrawPhase") val drawPhase: GamePhase,
  @Named("RoundPhase") val roundPhase: GamePhase,
  @Named("EndPhase") val endPhase: GamePhase
) extends GamePhases
