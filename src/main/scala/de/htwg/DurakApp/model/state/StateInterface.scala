package de.htwg.DurakApp.model.state

/** State Component Interface
  *
  * This is the public port to the State component. All external access to state
  * types must go through this interface.
  */
object StateInterface:
  type GamePhase = de.htwg.DurakApp.model.state.GamePhase
  type GameEvent = de.htwg.DurakApp.model.state.GameEvent

  val GameEvent = de.htwg.DurakApp.model.state.GameEvent

  val SetupPhase = de.htwg.DurakApp.model.state.SetupPhase
  val AskPlayerCountPhase = de.htwg.DurakApp.model.state.AskPlayerCountPhase
  val AskPlayerNamesPhase = de.htwg.DurakApp.model.state.AskPlayerNamesPhase
  val AskDeckSizePhase = de.htwg.DurakApp.model.state.AskDeckSizePhase
  val AskPlayAgainPhase = de.htwg.DurakApp.model.state.AskPlayAgainPhase
  val GameStartPhase = de.htwg.DurakApp.model.state.GameStartPhase
  val AttackPhase = de.htwg.DurakApp.model.state.AttackPhase
  val DefensePhase = de.htwg.DurakApp.model.state.DefensePhase
  val DrawPhase = de.htwg.DurakApp.model.state.DrawPhase
  val RoundPhase = de.htwg.DurakApp.model.state.RoundPhase
  val EndPhase = de.htwg.DurakApp.model.state.EndPhase
