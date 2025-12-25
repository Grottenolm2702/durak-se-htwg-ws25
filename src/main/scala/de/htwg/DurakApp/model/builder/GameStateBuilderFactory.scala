package de.htwg.DurakApp.model.builder

import de.htwg.DurakApp.model.{Card, Player, GameState}
import de.htwg.DurakApp.model.state.{GameEvent, GamePhase}
import com.google.inject.Inject

class GameStateBuilderFactory @Inject() ():
  def create(): GameStateBuilder =
    impl.GameStateBuilder()
