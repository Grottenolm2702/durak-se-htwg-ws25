package de.htwg.DurakApp.model.builder

import de.htwg.DurakApp.model.{Card, Player, GameState, GameStateFactory, CardFactory}
import de.htwg.DurakApp.model.state.{GameEvent, GamePhase}
import com.google.inject.Inject

class GameStateBuilderFactory @Inject() (
    gameStateFactory: GameStateFactory,
    cardFactory: CardFactory
):
  def create(): GameStateBuilder =
    impl.GameStateBuilder(gameStateFactory, cardFactory)
