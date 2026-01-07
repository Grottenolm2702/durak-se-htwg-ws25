package de.htwg.DurakApp.model.builder.impl

import de.htwg.DurakApp.model.CardFactory
import de.htwg.DurakApp.model.builder.{
  GameStateBuilder,
  GameStateBuilderFactory
}
import de.htwg.DurakApp.model.state.GamePhases
import com.google.inject.Inject

class GameStateBuilderFactoryImpl @Inject() (
    cardFactory: CardFactory,
    gamePhases: GamePhases
) extends GameStateBuilderFactory {

  def create(): GameStateBuilder =
    GameStateBuilder(cardFactory, gamePhases)
}
