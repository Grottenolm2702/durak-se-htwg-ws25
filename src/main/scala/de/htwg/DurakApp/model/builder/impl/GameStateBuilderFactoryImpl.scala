package de.htwg.DurakApp.model.builder.impl

import de.htwg.DurakApp.model.{GameStateFactory, CardFactory}
import de.htwg.DurakApp.model.builder.{GameStateBuilder, GameStateBuilderFactory}
import de.htwg.DurakApp.model.state.GamePhases
import com.google.inject.Inject

class GameStateBuilderFactoryImpl @Inject() (
  gameStateFactory: GameStateFactory,
  cardFactory: CardFactory,
  gamePhases: GamePhases
) extends GameStateBuilderFactory {
  
  def create(): GameStateBuilder = 
    GameStateBuilder(gameStateFactory, cardFactory, gamePhases)
}
