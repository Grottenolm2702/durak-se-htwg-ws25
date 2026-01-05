package de.htwg.DurakApp.model.builder

import de.htwg.DurakApp.model.{GameStateFactory, CardFactory}
import de.htwg.DurakApp.model.state.GamePhases

trait GameStateBuilderFactory {
  def create(): GameStateBuilder
}
