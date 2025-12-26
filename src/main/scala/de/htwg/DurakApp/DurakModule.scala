package de.htwg.DurakApp

// Dependency Injection Framework
import com.google.inject.{AbstractModule, Provides, Singleton}
import net.codingwell.scalaguice.ScalaModule

// Controller Component (Traits only, no impl!)
import de.htwg.DurakApp.controller.{Controller, GameSetup}
import de.htwg.DurakApp.controller.command.CommandFactory

// Model Component (Traits + Factories, no impl!)
import de.htwg.DurakApp.model.{GameState, CardFactory, PlayerFactory, GameStateFactory}
import de.htwg.DurakApp.model.builder.{GameStateBuilder, GameStateBuilderFactory}
import de.htwg.DurakApp.model.state.{SetupPhase, PhaseProvider}

// Util Component (Trait only, no impl!)
import de.htwg.DurakApp.util.{UndoRedoManager, UndoRedoManagerFactory}

class DurakModule extends AbstractModule with ScalaModule:
  override def configure(): Unit =
    // Bind object singletons
    bind[CommandFactory.type].toInstance(CommandFactory)
    bind[CardFactory.type].toInstance(CardFactory)
    bind[PlayerFactory.type].toInstance(PlayerFactory)
    bind[GameStateFactory.type].toInstance(GameStateFactory)
    bind[PhaseProvider.type].toInstance(PhaseProvider)
    
    // Bind factory that needs instantiation
    bind[GameStateBuilderFactory].asEagerSingleton()
    
    // Bind GameSetup implementation (impl wird nur hier referenziert!)
    bind[GameSetup].to[controller.impl.GameSetupImpl]
    
    // Bind UndoRedoManagerFactory (impl wird nur hier referenziert!)
    bind[UndoRedoManagerFactory].to[util.impl.UndoRedoManagerFactoryImpl]
  
  @Provides
  @Singleton
  def provideUndoRedoManager(factory: UndoRedoManagerFactory): UndoRedoManager =
    // Create initial UndoRedoManager instance
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
    // Only place where we reference ControllerImpl - through DI configuration
    new controller.impl.ControllerImpl(gameState, undoRedoManager, commandFactory, gameSetup, undoRedoManagerFactory)
