package de.htwg.DurakApp.controller
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Player, GameState, Suit, Rank}
import de.htwg.DurakApp.model.state._
import de.htwg.DurakApp.testutil._
import de.htwg.DurakApp.util.Observer
class ControllerSpec extends AnyWordSpec with Matchers {
  def createController(): Controller = {
    val initialState = TestHelper.createTestGameState()
    new SpyController(initialState, new StubUndoRedoManager())
  }
  "Controller interface" should {
    "provide access to game state" in {
      val controller = createController()
      controller.gameState should not be null
    }
    "process player actions" in {
      val controller = createController()
      val initialState = controller.gameState
      controller.processPlayerAction(SetPlayerCountAction(2))
      controller.gameState should not be initialState
    }
    "support undo operation" in {
      val controller = createController()
      controller.processPlayerAction(SetPlayerCountAction(2))
      val stateAfterAction = controller.gameState
      controller.undo()
      controller.gameState.lastEvent shouldBe defined
    }
    "support redo operation" in {
      val controller = createController()
      controller.processPlayerAction(SetPlayerCountAction(2))
      controller.undo()
      controller.redo()
      controller.gameState.lastEvent shouldBe defined
    }
    "provide status string" in {
      val controller = createController()
      controller.getStatusString() should not be empty
    }
    "support observer pattern" in {
      val controller = createController()
      val observer = new SpyObserver()
      controller.add(observer)
      controller.processPlayerAction(SetPlayerCountAction(2))
      observer.wasCalled shouldBe true
      controller.remove(observer)
    }
  }
  "Controller with test doubles" should {
    "track processed actions with spy" in {
      val initialState = TestHelper.createTestGameState()
      val spy = new SpyController(initialState, new StubUndoRedoManager())
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
        new StubUndoRedoManager()
      )
      val observer = new SpyObserver()
      spy.add(observer)
      spy.processPlayerAction(PassAction)
      spy.processPlayerAction(TakeCardsAction)
      observer.updateCount shouldBe 2
    }
    "handle undo with stub undo manager" in {
      val state1 = TestHelper.createTestGameState(
        players = List(TestHelper.Player("P1")),
        lastEvent = Some(GameEvent.Pass)
      )
      val state2 = TestHelper.createTestGameState(
        players = List(TestHelper.Player("P2")),
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
        players = List(TestHelper.Player("P1"))
      )
      val state2 = TestHelper.createTestGameState(
        players = List(TestHelper.Player("P2"))
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
}
