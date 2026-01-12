package de.htwg.DurakApp.model.state.impl

import com.google.inject.Inject
import de.htwg.DurakApp.model.state.*

class GamePhasesImpl @Inject() (
    val setupPhase: SetupPhase,
    val askPlayerCountPhase: AskPlayerCountPhase,
    val askPlayerNamesPhase: AskPlayerNamesPhase,
    val askDeckSizePhase: AskDeckSizePhase,
    val askPlayAgainPhase: AskPlayAgainPhase,
    val gameStartPhase: GameStartPhase,
    val attackPhase: AttackPhase,
    val defensePhase: DefensePhase,
    val drawPhase: DrawPhase,
    val roundPhase: RoundPhase,
    val endPhase: EndPhase
) extends GamePhases
