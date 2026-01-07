package de.htwg.DurakApp.testutil

import de.htwg.DurakApp.model.builder.{GameStateBuilder, GameStateBuilderFactory}
import de.htwg.DurakApp.model.builder.impl.GameStateBuilder as GameStateBuilderObject

class StubGameStateBuilderFactory(
    cardFactory: de.htwg.DurakApp.model.CardFactory,
    playerFactory: de.htwg.DurakApp.model.PlayerFactory,
    gameStateFactory: de.htwg.DurakApp.model.GameStateFactory
) extends GameStateBuilderFactory:
  def create(): GameStateBuilder =
    GameStateBuilderObject(gameStateFactory, cardFactory, new StubGamePhasesImpl())
