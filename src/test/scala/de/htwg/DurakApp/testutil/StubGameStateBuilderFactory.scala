package de.htwg.DurakApp.testutil
import de.htwg.DurakApp.model.builder.{
  GameStateBuilder,
  GameStateBuilderFactory
}
import de.htwg.DurakApp.model.builder.impl.GameStateBuilder as GameStateBuilderObject
class StubGameStateBuilderFactory() extends GameStateBuilderFactory:
  def create(): GameStateBuilder =
    GameStateBuilderObject(new StubGamePhasesImpl())
