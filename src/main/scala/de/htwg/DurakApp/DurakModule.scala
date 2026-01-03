package de.htwg.DurakApp

import com.google.inject.{AbstractModule, Provides, Singleton}
import net.codingwell.scalaguice.ScalaModule

import de.htwg.DurakApp.controller.{Controller, GameSetup}
import de.htwg.DurakApp.controller.command.CommandFactory

import de.htwg.DurakApp.model.{
  GameState,
  CardFactory,
  PlayerFactory,
  GameStateFactory
}
import de.htwg.DurakApp.model.builder.{
  GameStateBuilder,
  GameStateBuilderFactory
}
import de.htwg.DurakApp.model.state.{SetupPhase, PhaseProvider}

import de.htwg.DurakApp.util.{UndoRedoManager, UndoRedoManagerFactory}

class DurakModule extends AbstractModule with ScalaModule:
  override def configure(): Unit =
    bind[CommandFactory.type].toInstance(CommandFactory)
    
    bind[CardFactory].to[model.impl.CardFactoryImpl]
    bind[PlayerFactory].to[model.impl.PlayerFactoryImpl]
    bind[GameStateFactory].to[model.impl.GameStateFactoryImpl]
    
    bind[PhaseProvider.type].toInstance(PhaseProvider)

    bind[GameStateBuilderFactory].asEagerSingleton()

    bind[GameSetup].to[controller.impl.GameSetupImpl]

    bind[UndoRedoManagerFactory].to[util.impl.UndoRedoManagerFactoryImpl]

  @Provides
  @Singleton
  def provideUndoRedoManager(factory: UndoRedoManagerFactory): UndoRedoManager =
    factory.create()

  @Provides
  def provideGameState(builderFactory: GameStateBuilderFactory): GameState =
    builderFactory.create().withGamePhase(SetupPhase).build()

  @Provides
  @Singleton
  def provideController(
      gameState: GameState,
      undoRedoManager: UndoRedoManager,
      commandFactory: CommandFactory.type,
      gameSetup: GameSetup,
      undoRedoManagerFactory: UndoRedoManagerFactory
  ): Controller =
    new controller.impl.ControllerImpl(
      gameState,
      undoRedoManager,
      commandFactory,
      gameSetup,
      undoRedoManagerFactory
    )
