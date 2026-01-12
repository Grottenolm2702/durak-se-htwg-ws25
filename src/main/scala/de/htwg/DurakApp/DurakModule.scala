package de.htwg.DurakApp

import com.google.inject.{AbstractModule, Provides, Singleton}
import net.codingwell.scalaguice.ScalaModule
import java.io.PrintStream

import de.htwg.DurakApp.controller.{Controller, GameSetup}
import de.htwg.DurakApp.controller.command.CommandFactory
import de.htwg.DurakApp.controller.command.impl.CommandFactoryImpl

import de.htwg.DurakApp.model.GameState
import de.htwg.DurakApp.model.builder.{GameStateBuilderFactory}
import de.htwg.DurakApp.model.builder.impl.GameStateBuilderFactoryImpl
import de.htwg.DurakApp.model.state.*
import de.htwg.DurakApp.model.state.impl.*

import de.htwg.DurakApp.util.{UndoRedoManager, UndoRedoManagerFactory}

class DurakModule extends AbstractModule with ScalaModule:
  override def configure(): Unit =
    bind[SetupPhase].toInstance(SetupPhaseImpl)
    bind[AskPlayerCountPhase].toInstance(AskPlayerCountPhaseImpl)
    bind[AskPlayerNamesPhase].toInstance(AskPlayerNamesPhaseImpl)
    bind[AskDeckSizePhase].toInstance(AskDeckSizePhaseImpl)
    bind[AskPlayAgainPhase].toInstance(AskPlayAgainPhaseImpl)
    bind[GameStartPhase].toInstance(GameStartPhaseImpl)
    bind[AttackPhase].toInstance(AttackPhaseImpl)
    bind[DefensePhase].toInstance(DefensePhaseImpl)
    bind[DrawPhase].toInstance(DrawPhaseImpl)
    bind[RoundPhase].toInstance(RoundPhaseImpl)
    bind[EndPhase].toInstance(EndPhaseImpl)
    bind[GamePhases].to[GamePhasesImpl].asEagerSingleton()

    bind[CommandFactory].to[CommandFactoryImpl].asEagerSingleton()

    bind[GameStateBuilderFactory].to[GameStateBuilderFactoryImpl]

    bind[GameSetup].to[controller.impl.GameSetupImpl]

    bind[UndoRedoManagerFactory].to[util.impl.UndoRedoManagerFactoryImpl]

    bind[Controller].to[controller.impl.ControllerImpl].in(classOf[Singleton])

  @Provides
  @Singleton
  def provideUndoRedoManager(factory: UndoRedoManagerFactory): UndoRedoManager =
    factory.create()

  @Provides
  def provideGameState(
      gameStateBuilderFactory: GameStateBuilderFactory,
      setupPhase: SetupPhase
  ): GameState =
    gameStateBuilderFactory
      .create()
      .withGamePhase(setupPhase)
      .build()
      .get

  @Provides
  def providePrintStream(): PrintStream = Console.out
