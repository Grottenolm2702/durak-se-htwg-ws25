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
import de.htwg.DurakApp.model.state.{GamePhase, GamePhases}
import de.htwg.DurakApp.model.state.impl.GamePhasesImpl
import com.google.inject.name.{Named, Names}

import de.htwg.DurakApp.util.{UndoRedoManager, UndoRedoManagerFactory}

class DurakModule extends AbstractModule with ScalaModule:
  override def configure(): Unit =
    bind[GamePhase]
      .annotatedWith(Names.named("SetupPhase"))
      .toInstance(de.htwg.DurakApp.model.state.impl.SetupPhaseImpl)
    bind[GamePhase]
      .annotatedWith(Names.named("AskPlayerCountPhase"))
      .toInstance(de.htwg.DurakApp.model.state.impl.AskPlayerCountPhaseImpl)
    bind[GamePhase]
      .annotatedWith(Names.named("AskPlayerNamesPhase"))
      .toInstance(de.htwg.DurakApp.model.state.impl.AskPlayerNamesPhaseImpl)
    bind[GamePhase]
      .annotatedWith(Names.named("AskDeckSizePhase"))
      .toInstance(de.htwg.DurakApp.model.state.impl.AskDeckSizePhaseImpl)
    bind[GamePhase]
      .annotatedWith(Names.named("AskPlayAgainPhase"))
      .toInstance(de.htwg.DurakApp.model.state.impl.AskPlayAgainPhaseImpl)
    bind[GamePhase]
      .annotatedWith(Names.named("GameStartPhase"))
      .toInstance(de.htwg.DurakApp.model.state.impl.GameStartPhaseImpl)
    bind[GamePhase]
      .annotatedWith(Names.named("AttackPhase"))
      .toInstance(de.htwg.DurakApp.model.state.impl.AttackPhaseImpl)
    bind[GamePhase]
      .annotatedWith(Names.named("DefensePhase"))
      .toInstance(de.htwg.DurakApp.model.state.impl.DefensePhaseImpl)
    bind[GamePhase]
      .annotatedWith(Names.named("DrawPhase"))
      .toInstance(de.htwg.DurakApp.model.state.impl.DrawPhaseImpl)
    bind[GamePhase]
      .annotatedWith(Names.named("RoundPhase"))
      .toInstance(de.htwg.DurakApp.model.state.impl.RoundPhaseImpl)
    bind[GamePhase]
      .annotatedWith(Names.named("EndPhase"))
      .toInstance(de.htwg.DurakApp.model.state.impl.EndPhaseImpl)
    bind[GamePhases].to[GamePhasesImpl].asEagerSingleton()

    bind[CommandFactory.type].toInstance(CommandFactory)

    bind[CardFactory].to[model.impl.CardFactoryImpl]
    bind[PlayerFactory].to[model.impl.PlayerFactoryImpl]
    bind[GameStateFactory].to[model.impl.GameStateFactoryImpl]

    bind[GameStateBuilderFactory].asEagerSingleton()

    bind[GameSetup].to[controller.impl.GameSetupImpl]

    bind[UndoRedoManagerFactory].to[util.impl.UndoRedoManagerFactoryImpl]

  @Provides
  @Singleton
  def provideUndoRedoManager(factory: UndoRedoManagerFactory): UndoRedoManager =
    factory.create()

  @Provides
  def provideGameState(
      builderFactory: GameStateBuilderFactory,
      @Named("SetupPhase") setupPhase: GamePhase
  ): GameState =
    builderFactory.create().withGamePhase(setupPhase).build()

  @Provides
  @Singleton
  def provideController(
      gameState: GameState,
      undoRedoManager: UndoRedoManager,
      commandFactory: CommandFactory.type,
      gameSetup: GameSetup,
      undoRedoManagerFactory: UndoRedoManagerFactory,
      gamePhases: GamePhases
  ): Controller =
    new controller.impl.ControllerImpl(
      gameState,
      undoRedoManager,
      commandFactory,
      gameSetup,
      undoRedoManagerFactory,
      gamePhases
    )
