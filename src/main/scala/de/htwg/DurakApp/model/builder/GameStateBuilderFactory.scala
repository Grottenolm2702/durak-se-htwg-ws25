package de.htwg.DurakApp.model.builder

import de.htwg.DurakApp.model.{Card, Player, GameState, GameStateFactory}
import de.htwg.DurakApp.model.state.{GameEvent, GamePhase}
import com.google.inject.Inject

class GameStateBuilderFactory @Inject() (
    gameStateFactory: GameStateFactory
):
  def create(): GameStateBuilder =
    impl.GameStateBuilder(gameStateFactory)
