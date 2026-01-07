package de.htwg.DurakApp
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import com.google.inject.Guice
import com.google.inject.name.Names
import de.htwg.DurakApp.controller.{Controller, GameSetup}
import de.htwg.DurakApp.controller.command.CommandFactory
import de.htwg.DurakApp.model.{
  GameState,
  CardFactory,
  PlayerFactory,
  GameStateFactory
}
import de.htwg.DurakApp.model.builder.GameStateBuilderFactory
import de.htwg.DurakApp.model.state.{GamePhase, GamePhases}
import de.htwg.DurakApp.util.{UndoRedoManager, UndoRedoManagerFactory}
import java.io.PrintStream
class DurakModuleSpec extends AnyWordSpec with Matchers {
  "A DurakModule" should {
    val injector = Guice.createInjector(new DurakModule)
    "provide CardFactory" in {
      val cardFactory = injector.getInstance(classOf[CardFactory])
      cardFactory should not be null
    }
    "provide PlayerFactory" in {
      val playerFactory = injector.getInstance(classOf[PlayerFactory])
      playerFactory should not be null
    }
    "provide GameStateFactory" in {
      val gameStateFactory = injector.getInstance(classOf[GameStateFactory])
      gameStateFactory should not be null
    }
    "provide GameStateBuilderFactory" in {
      val builderFactory =
        injector.getInstance(classOf[GameStateBuilderFactory])
      builderFactory should not be null
    }
    "provide GamePhases" in {
      val gamePhases = injector.getInstance(classOf[GamePhases])
      gamePhases should not be null
      gamePhases.setupPhase should not be null
      gamePhases.askPlayerCountPhase should not be null
      gamePhases.askPlayerNamesPhase should not be null
      gamePhases.askDeckSizePhase should not be null
      gamePhases.askPlayAgainPhase should not be null
      gamePhases.gameStartPhase should not be null
      gamePhases.attackPhase should not be null
      gamePhases.defensePhase should not be null
      gamePhases.drawPhase should not be null
      gamePhases.roundPhase should not be null
      gamePhases.endPhase should not be null
    }
    "provide all named GamePhase instances" in {
      val setupPhase = injector.getInstance(
        com.google.inject.Key.get(classOf[GamePhase], Names.named("SetupPhase"))
      )
      setupPhase should not be null
      val askPlayerCountPhase = injector.getInstance(
        com.google.inject.Key
          .get(classOf[GamePhase], Names.named("AskPlayerCountPhase"))
      )
      askPlayerCountPhase should not be null
      val askPlayerNamesPhase = injector.getInstance(
        com.google.inject.Key
          .get(classOf[GamePhase], Names.named("AskPlayerNamesPhase"))
      )
      askPlayerNamesPhase should not be null
      val askDeckSizePhase = injector.getInstance(
        com.google.inject.Key
          .get(classOf[GamePhase], Names.named("AskDeckSizePhase"))
      )
      askDeckSizePhase should not be null
      val askPlayAgainPhase = injector.getInstance(
        com.google.inject.Key
          .get(classOf[GamePhase], Names.named("AskPlayAgainPhase"))
      )
      askPlayAgainPhase should not be null
      val gameStartPhase = injector.getInstance(
        com.google.inject.Key
          .get(classOf[GamePhase], Names.named("GameStartPhase"))
      )
      gameStartPhase should not be null
      val attackPhase = injector.getInstance(
        com.google.inject.Key
          .get(classOf[GamePhase], Names.named("AttackPhase"))
      )
      attackPhase should not be null
      val defensePhase = injector.getInstance(
        com.google.inject.Key
          .get(classOf[GamePhase], Names.named("DefensePhase"))
      )
      defensePhase should not be null
      val drawPhase = injector.getInstance(
        com.google.inject.Key.get(classOf[GamePhase], Names.named("DrawPhase"))
      )
      drawPhase should not be null
      val roundPhase = injector.getInstance(
        com.google.inject.Key.get(classOf[GamePhase], Names.named("RoundPhase"))
      )
      roundPhase should not be null
      val endPhase = injector.getInstance(
        com.google.inject.Key.get(classOf[GamePhase], Names.named("EndPhase"))
      )
      endPhase should not be null
    }
    "provide CommandFactory" in {
      val commandFactory = injector.getInstance(classOf[CommandFactory])
      commandFactory should not be null
    }
    "provide GameSetup" in {
      val gameSetup = injector.getInstance(classOf[GameSetup])
      gameSetup should not be null
    }
    "provide UndoRedoManagerFactory" in {
      val undoRedoManagerFactory =
        injector.getInstance(classOf[UndoRedoManagerFactory])
      undoRedoManagerFactory should not be null
    }
    "provide UndoRedoManager" in {
      val undoRedoManager = injector.getInstance(classOf[UndoRedoManager])
      undoRedoManager should not be null
      undoRedoManager.undoStack shouldBe empty
      undoRedoManager.redoStack shouldBe empty
    }
    "provide Controller as Singleton" in {
      val controller1 = injector.getInstance(classOf[Controller])
      val controller2 = injector.getInstance(classOf[Controller])
      controller1 should not be null
      controller2 should not be null
      controller1 shouldBe controller2
    }
    "provide GameState" in {
      val gameState = injector.getInstance(classOf[GameState])
      gameState should not be null
      gameState.players should not be null
      gameState.deck should not be null
      gameState.gamePhase should not be null
    }
    "provide PrintStream" in {
      val printStream = injector.getInstance(classOf[PrintStream])
      printStream should not be null
      printStream shouldBe Console.out
    }
    "create functional CardFactory" in {
      val cardFactory = injector.getInstance(classOf[CardFactory])
      val card = cardFactory(
        de.htwg.DurakApp.model.Suit.Hearts,
        de.htwg.DurakApp.model.Rank.Ace
      )
      card should not be null
      card.suit shouldBe de.htwg.DurakApp.model.Suit.Hearts
      card.rank shouldBe de.htwg.DurakApp.model.Rank.Ace
    }
    "create functional PlayerFactory" in {
      val playerFactory = injector.getInstance(classOf[PlayerFactory])
      val player = playerFactory("TestPlayer", List.empty)
      player should not be null
      player.name shouldBe "TestPlayer"
      player.hand shouldBe empty
    }
    "create functional GameSetup" in {
      val gameSetup = injector.getInstance(classOf[GameSetup])
      val result = gameSetup.setupGame(List("Alice", "Bob"), 36)
      result shouldBe defined
      result.get.players should have size 2
      result.get.players.map(_.name) should contain allOf ("Alice", "Bob")
    }
    "ensure all GamePhases are different instances" in {
      val gamePhases = injector.getInstance(classOf[GamePhases])
      val allPhases = List(
        gamePhases.setupPhase,
        gamePhases.askPlayerCountPhase,
        gamePhases.askPlayerNamesPhase,
        gamePhases.askDeckSizePhase,
        gamePhases.askPlayAgainPhase,
        gamePhases.gameStartPhase,
        gamePhases.attackPhase,
        gamePhases.defensePhase,
        gamePhases.drawPhase,
        gamePhases.roundPhase,
        gamePhases.endPhase
      )
      allPhases.map(_.toString).distinct should have size 11
    }
    "ensure GamePhases is eagerly initialized" in {
      val gamePhases = injector.getInstance(classOf[GamePhases])
      gamePhases should not be null
    }
    "ensure CommandFactory is eagerly initialized" in {
      val commandFactory = injector.getInstance(classOf[CommandFactory])
      commandFactory should not be null
    }
  }
}
