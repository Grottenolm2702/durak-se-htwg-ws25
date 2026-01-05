package de.htwg.DurakApp

import com.google.inject.{AbstractModule, Provides, Singleton}
import net.codingwell.scalaguice.ScalaModule
import java.io.PrintStream

import de.htwg.DurakApp.controller.{Controller, GameSetup}
import de.htwg.DurakApp.controller.command.CommandFactory

import de.htwg.DurakApp.model.{
  GameState,
  CardFactory,
  PlayerFactory,
  GameStateFactory
}
import de.htwg.DurakApp.model.impl.{CardFactoryImpl, PlayerFactoryImpl, GameStateFactoryImpl}
import de.htwg.DurakApp.model.builder.impl.GameStateBuilder
import de.htwg.DurakApp.model.state.{GamePhase, GamePhases}
import de.htwg.DurakApp.model.state.impl.*
import com.google.inject.name.{Named, Names}

import de.htwg.DurakApp.util.{UndoRedoManager, UndoRedoManagerFactory}

class DurakModule extends AbstractModule with ScalaModule:
  override def configure(): Unit =
    bind[GamePhase]
      .annotatedWith(Names.named("SetupPhase"))
      .toInstance(SetupPhaseImpl)
    bind[GamePhase]
      .annotatedWith(Names.named("AskPlayerCountPhase"))
      .toInstance(AskPlayerCountPhaseImpl)
    bind[GamePhase]
      .annotatedWith(Names.named("AskPlayerNamesPhase"))
      .toInstance(AskPlayerNamesPhaseImpl)
    bind[GamePhase]
      .annotatedWith(Names.named("AskDeckSizePhase"))
      .toInstance(AskDeckSizePhaseImpl)
    bind[GamePhase]
      .annotatedWith(Names.named("AskPlayAgainPhase"))
      .toInstance(AskPlayAgainPhaseImpl)
    bind[GamePhase]
      .annotatedWith(Names.named("GameStartPhase"))
      .toInstance(GameStartPhaseImpl)
    bind[GamePhase]
      .annotatedWith(Names.named("AttackPhase"))
      .toInstance(AttackPhaseImpl)
    bind[GamePhase]
      .annotatedWith(Names.named("DefensePhase"))
      .toInstance(DefensePhaseImpl)
    bind[GamePhase]
      .annotatedWith(Names.named("DrawPhase"))
      .toInstance(DrawPhaseImpl)
    bind[GamePhase]
      .annotatedWith(Names.named("RoundPhase"))
      .toInstance(RoundPhaseImpl)
    bind[GamePhase]
      .annotatedWith(Names.named("EndPhase"))
      .toInstance(EndPhaseImpl)
    bind[GamePhases].to[GamePhasesImpl].asEagerSingleton()

    bind[CommandFactory].asEagerSingleton()

    bind[CardFactory].to[CardFactoryImpl]
    bind[PlayerFactory].to[PlayerFactoryImpl]
    bind[GameStateFactory].to[GameStateFactoryImpl]

    bind[GameSetup].to[controller.impl.GameSetupImpl]

    bind[UndoRedoManagerFactory].to[util.impl.UndoRedoManagerFactoryImpl]

  @Provides
  @Singleton
  def provideUndoRedoManager(factory: UndoRedoManagerFactory): UndoRedoManager =
    factory.create()

  @Provides
  def provideGameState(
      gameStateFactory: GameStateFactory,
      cardFactory: CardFactory,
      gamePhases: GamePhases,
      @Named("SetupPhase") setupPhase: GamePhase
  ): GameState =
    GameStateBuilder(gameStateFactory, cardFactory, gamePhases)
      .withGamePhase(setupPhase)
      .build()

  @Provides
  @Singleton
  def provideController(
      gameState: GameState,
      undoRedoManager: UndoRedoManager,
      commandFactory: CommandFactory,
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

  @Provides
  def providePrintStream(): PrintStream = Console.out
