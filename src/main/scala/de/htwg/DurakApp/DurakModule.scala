package de.htwg.DurakApp

import com.google.inject.{AbstractModule, Provides, Singleton}
import net.codingwell.scalaguice.ScalaModule
import de.htwg.DurakApp.controller.Controller
import de.htwg.DurakApp.controller.command.CommandFactory
import de.htwg.DurakApp.model.{GameState, CardFactory, PlayerFactory, GameStateFactory}
import de.htwg.DurakApp.model.builder.{GameStateBuilder, GameStateBuilderFactory}
import de.htwg.DurakApp.model.state.{SetupPhase, PhaseProvider}
import de.htwg.DurakApp.util.UndoRedoManager

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
  
  @Provides
  @Singleton
  def provideUndoRedoManager(): UndoRedoManager =
    UndoRedoManager()
  
  @Provides
  def provideGameState(builderFactory: GameStateBuilderFactory): GameState =
    builderFactory.create().withGamePhase(SetupPhase).build()
  
  @Provides
  def provideController(
      gameState: GameState, 
      undoRedoManager: UndoRedoManager,
      commandFactory: CommandFactory.type
  ): Controller =
    Controller(gameState, undoRedoManager)
