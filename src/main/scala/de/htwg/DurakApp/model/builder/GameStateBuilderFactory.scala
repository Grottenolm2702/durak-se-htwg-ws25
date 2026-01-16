package de.htwg.DurakApp.model.builder

trait GameStateBuilderFactory {
  def create(): GameStateBuilder
}
