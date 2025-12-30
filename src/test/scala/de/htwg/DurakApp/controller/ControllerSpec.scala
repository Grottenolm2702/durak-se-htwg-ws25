package de.htwg.DurakApp.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Player, GameState, Suit, Rank}
import de.htwg.DurakApp.model.state._
import de.htwg.DurakApp.testutil.{TestHelper, StubGameSetup, StubUndoRedoManager, SpyController}
import de.htwg.DurakApp.util.Observer
import com.google.inject.Guice

class ControllerSpec extends AnyWordSpec with Matchers {

  val injector = Guice.createInjector(new de.htwg.DurakApp.DurakModule)

  "Controller interface" should {

    "provide access to game state" in {
      val controller = injector.getInstance(classOf[Controller])
      controller.gameState should not be null
    }

    "process player actions" in {
      val controller = injector.getInstance(classOf[Controller])
      val initialState = controller.gameState
      
      controller.processPlayerAction(SetPlayerCountAction(2))
      controller.gameState should not be initialState
    }

    "support undo operation" in {
      val controller = injector.getInstance(classOf[Controller])
      
      controller.processPlayerAction(SetPlayerCountAction(2))
      val stateAfterAction = controller.gameState
      
      controller.undo()
      controller.gameState.lastEvent shouldBe defined
    }

    "support redo operation" in {
      val controller = injector.getInstance(classOf[Controller])
      
      controller.processPlayerAction(SetPlayerCountAction(2))
      controller.undo()
      
      controller.redo()
      controller.gameState.lastEvent shouldBe defined
    }

    "provide status string" in {
      val controller = injector.getInstance(classOf[Controller])
      controller.getStatusString() should not be empty
    }

    "support observer pattern" in {
      val controller = injector.getInstance(classOf[Controller])
      var updateCalled = false
      
      val observer = new Observer {
        def update: Unit = updateCalled = true
      }
      
      controller.add(observer)
      controller.processPlayerAction(SetPlayerCountAction(2))
      
      updateCalled shouldBe true
      
      controller.remove(observer)
    }
  }

  "Controller with test doubles" should {

    "track processed actions with spy" in {
      val initialState = TestHelper.createTestGameState()
      val spy = new SpyController(
        initialState,
        new StubUndoRedoManager(),
        new StubGameSetup()
      )
      
      spy.processPlayerAction(PassAction)
      spy.processPlayerAction(TakeCardsAction)
      
      spy.processedActions should have size 2
      spy.processedActions should contain(PassAction)
      spy.processedActions should contain(TakeCardsAction)
    }

    "notify observers when state changes" in {
      val initialState = TestHelper.createTestGameState()
      val spy = new SpyController(
        initialState,
        new StubUndoRedoManager(),
        new StubGameSetup()
      )
      
      var notificationCount = 0
      val observer = new Observer {
        def update: Unit = notificationCount += 1
      }
      
      spy.add(observer)
      spy.processPlayerAction(PassAction)
      spy.processPlayerAction(TakeCardsAction)
      
      notificationCount shouldBe 2
    }

    "handle undo with stub undo manager" in {
      val state1 = TestHelper.createTestGameState(
        players = List(Player("P1")),
        lastEvent = Some(GameEvent.Pass)
      )
      val state2 = TestHelper.createTestGameState(
        players = List(Player("P2")),
        lastEvent = Some(GameEvent.Take)
      )
      
      val undoMgr = new StubUndoRedoManager()
      val undoMgr2 = undoMgr.save(null, state1)
      
      val result = undoMgr2.undo(state2)
      result shouldBe defined
      result.get._2 shouldBe state1
    }

    "handle redo with stub undo manager" in {
      val state1 = TestHelper.createTestGameState(
        players = List(Player("P1"))
      )
      val state2 = TestHelper.createTestGameState(
        players = List(Player("P2"))
      )
      
      val undoMgr = new StubUndoRedoManager()
      val undoMgr2 = undoMgr.save(null, state1)
      val (undoMgr3, prevState) = undoMgr2.undo(state2).get
      
      val result = undoMgr3.redo(prevState)
      result shouldBe defined
      result.get._2 shouldBe state2
    }

    "setup game with valid parameters using stub" in {
      val setup = new StubGameSetup()
      val result = setup.setupGame(List("Alice", "Bob"), 36)
      
      result shouldBe defined
      result.get.players should have size 2
      result.get.players.head.name shouldBe "Alice"
      result.get.players(1).name shouldBe "Bob"
    }

    "reject invalid player count in stub setup" in {
      val setup = new StubGameSetup()
      
      setup.setupGame(List("Alice"), 36) shouldBe None
      setup.setupGame(List("A", "B", "C", "D", "E", "F", "G"), 36) shouldBe None
    }

    "reject invalid deck size in stub setup" in {
      val setup = new StubGameSetup()
      
      setup.setupGame(List("Alice", "Bob"), 1) shouldBe None
      setup.setupGame(List("Alice", "Bob"), 37) shouldBe None
    }
  }

  "Controller with real implementation" should {

    "handle SetPlayerCountAction" in {
      val controller = injector.getInstance(classOf[Controller])
      
      controller.processPlayerAction(SetPlayerCountAction(2))
      val state = controller.gameState
      
      state.setupPlayerCount shouldBe defined
    }

    "handle AddPlayerNameAction" in {
      val controller = injector.getInstance(classOf[Controller])
      
      controller.processPlayerAction(SetPlayerCountAction(2))
      controller.processPlayerAction(AddPlayerNameAction("Alice"))
      
      val state = controller.gameState
      state.setupPlayerNames should not be empty
    }

    "handle SetDeckSizeAction" in {
      val controller = injector.getInstance(classOf[Controller])
      
      controller.processPlayerAction(SetPlayerCountAction(2))
      controller.processPlayerAction(AddPlayerNameAction("Alice"))
      controller.processPlayerAction(AddPlayerNameAction("Bob"))
      controller.processPlayerAction(SetDeckSizeAction(36))
      
      val state = controller.gameState
      state.lastEvent shouldBe defined
    }

    "reject invalid actions with appropriate error events" in {
      val controller = injector.getInstance(classOf[Controller])
      
      controller.processPlayerAction(SetPlayerCountAction(1))
      controller.gameState.lastEvent shouldBe defined
      
      controller.processPlayerAction(SetPlayerCountAction(7))
      controller.gameState.lastEvent shouldBe defined
    }

    "handle ExitGameAction" in {
      val controller = injector.getInstance(classOf[Controller])
      
      controller.processPlayerAction(ExitGameAction)
      controller.gameState.lastEvent shouldBe defined
    }
  }
}
