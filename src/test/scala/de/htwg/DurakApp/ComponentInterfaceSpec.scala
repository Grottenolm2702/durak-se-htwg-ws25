package de.htwg.DurakApp

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.controller.ControllerInterface.{*, given}
import de.htwg.DurakApp.model.ModelInterface.*
import de.htwg.DurakApp.aview.ViewInterface
import de.htwg.DurakApp.util.{Observable, Observer, UndoRedoManager}

class ComponentInterfaceSpec extends AnyWordSpec with Matchers {

  class MockController(initialState: GameState)
      extends Controller
      with Observable {
    private var _gameState: GameState = initialState
    private val _actionHistory =
      scala.collection.mutable.ListBuffer.empty[PlayerAction]
    private val _undoStack =
      scala.collection.mutable.ListBuffer.empty[GameState]
    private val _redoStack =
      scala.collection.mutable.ListBuffer.empty[GameState]

    var processPlayerActionCalled = false
    var undoCalled = false
    var redoCalled = false

    override def processPlayerAction(action: PlayerAction): GameState = {
      processPlayerActionCalled = true
      _actionHistory += action
      _undoStack += _gameState
      _redoStack.clear()

      val newState = action match {
        case SetPlayerCountAction(count) =>
          _gameState.copy(
            setupPlayerCount = Some(count),
            gamePhase =
              if (count >= 2 && count <= 6) AskPlayerNamesPhase
              else _gameState.gamePhase,
            lastEvent =
              if (count >= 2 && count <= 6) Some(GameEvent.AskPlayerNames)
              else Some(GameEvent.SetupError)
          )
        case AddPlayerNameAction(name) =>
          val newNames = _gameState.setupPlayerNames :+ name
          val expectedCount = _gameState.setupPlayerCount.getOrElse(0)
          _gameState.copy(
            setupPlayerNames = newNames,
            gamePhase =
              if (newNames.length == expectedCount) AskDeckSizePhase
              else _gameState.gamePhase
          )
        case PlayCardAction(card) =>
          _gameState.copy(
            table = _gameState.table + (card -> None),
            gamePhase = DefensePhase
          )
        case PassAction =>
          _gameState.copy(lastEvent = Some(GameEvent.Pass))
        case _ => _gameState
      }

      _gameState = newState
      notifyObservers
      _gameState
    }

    override def undo(): Option[GameState] = {
      undoCalled = true
      if (_undoStack.nonEmpty) {
        _redoStack += _gameState
        _gameState = _undoStack.remove(_undoStack.length - 1)
        Some(_gameState)
      } else None
    }

    override def redo(): Option[GameState] = {
      redoCalled = true
      if (_redoStack.nonEmpty) {
        _undoStack += _gameState
        _gameState = _redoStack.remove(_redoStack.length - 1)
        Some(_gameState)
      } else None
    }

    override def getStatusString(): String = _gameState.gamePhase.toString
    override def gameState: GameState = _gameState

    def actionHistory: List[PlayerAction] = _actionHistory.toList
  }

  class MockView extends ViewInterface {
    var updateCalled = false
    var updateCount = 0

    override def update: Unit = {
      updateCalled = true
      updateCount += 1
    }
  }

  class SpyController(real: Controller) extends Controller with Observable {
    var processPlayerActionCallCount = 0
    var undoCallCount = 0
    var redoCallCount = 0

    override def processPlayerAction(action: PlayerAction): GameState = {
      processPlayerActionCallCount += 1
      real.processPlayerAction(action)
    }

    override def undo(): Option[GameState] = {
      undoCallCount += 1
      real.undo()
    }

    override def redo(): Option[GameState] = {
      redoCallCount += 1
      real.redo()
    }

    override def getStatusString(): String = real.getStatusString()
    override def gameState: GameState = real.gameState

    override def add(observer: Observer): Unit = real.add(observer)
    override def remove(observer: Observer): Unit = real.remove(observer)
    override def notifyObservers: Unit = real.notifyObservers
  }

  object StubGameState {
    def setupStub(): GameState =
      GameStateBuilder().withGamePhase(SetupPhase).build()

    def attackStub(): GameState = {
      val p1 = Player("Alice", List(Card(Suit.Hearts, Rank.Ace)))
      val p2 = Player("Bob", List(Card(Suit.Spades, Rank.Six)))
      GameStateBuilder()
        .withPlayers(List(p1, p2))
        .withGamePhase(AttackPhase)
        .withTrumpCard(Card(Suit.Clubs, Rank.Six, isTrump = true))
        .withAttackerIndex(0)
        .withDefenderIndex(1)
        .build()
    }
  }

  class FakeController extends Controller with Observable {
    private var _state = StubGameState.setupStub()
    private var _undoAvailable = false

    override def processPlayerAction(action: PlayerAction): GameState = {
      _undoAvailable = true
      action match {
        case SetPlayerCountAction(count) if count >= 2 =>
          _state = _state.copy(
            setupPlayerCount = Some(count),
            gamePhase = AskPlayerNamesPhase
          )
        case _ =>
      }
      notifyObservers
      _state
    }

    override def undo(): Option[GameState] =
      if (_undoAvailable) Some(_state) else None
    override def redo(): Option[GameState] = None
    override def getStatusString(): String = "FakeController"
    override def gameState: GameState = _state
  }

  "A MockController through ControllerInterface" should {

    "track method calls" in {
      val mock = new MockController(StubGameState.setupStub())

      mock.processPlayerActionCalled should be(false)
      mock.processPlayerAction(SetPlayerCountAction(2))
      mock.processPlayerActionCalled should be(true)
    }

    "record action history" in {
      val mock = new MockController(StubGameState.setupStub())

      mock.processPlayerAction(SetPlayerCountAction(2))
      mock.processPlayerAction(AddPlayerNameAction("Alice"))

      mock.actionHistory should have length 2
      mock.actionHistory.head shouldBe a[SetPlayerCountAction]
    }

    "simulate state changes" in {
      val mock = new MockController(StubGameState.setupStub())

      val result = mock.processPlayerAction(SetPlayerCountAction(3))

      result.setupPlayerCount should be(Some(3))
      result.gamePhase should be(AskPlayerNamesPhase)
    }

    "support undo/redo tracking" in {
      val mock = new MockController(StubGameState.setupStub())

      mock.processPlayerAction(SetPlayerCountAction(2))
      mock.undoCalled should be(false)

      mock.undo()
      mock.undoCalled should be(true)
    }
  }

  "A MockView through ViewInterface" should {

    "track update calls" in {
      val mockView = new MockView()

      mockView.updateCalled should be(false)
      mockView.updateCount should be(0)

      mockView.update

      mockView.updateCalled should be(true)
      mockView.updateCount should be(1)
    }

    "count multiple updates" in {
      val mockView = new MockView()

      mockView.update
      mockView.update
      mockView.update

      mockView.updateCount should be(3)
    }
  }

  "A SpyController through ControllerInterface" should {

    "count processPlayerAction calls" in {
      val realController =
        Controller(StubGameState.setupStub(), UndoRedoManager())
      val spy = new SpyController(realController)

      spy.processPlayerActionCallCount should be(0)

      spy.processPlayerAction(SetPlayerCountAction(2))
      spy.processPlayerAction(AddPlayerNameAction("Alice"))

      spy.processPlayerActionCallCount should be(2)
    }

    "count undo calls" in {
      val realController =
        Controller(StubGameState.setupStub(), UndoRedoManager())
      val spy = new SpyController(realController)

      spy.processPlayerAction(SetPlayerCountAction(2))
      spy.undoCallCount should be(0)

      spy.undo()
      spy.undoCallCount should be(1)
    }

    "delegate to real controller" in {
      val realController =
        Controller(StubGameState.setupStub(), UndoRedoManager())
      val spy = new SpyController(realController)

      val result = spy.processPlayerAction(SetPlayerCountAction(2))

      result.setupPlayerCount should be(Some(2))
      spy.gameState.setupPlayerCount should be(Some(2))
    }
  }

  "A FakeController through ControllerInterface" should {

    "provide simplified behavior" in {
      val fake = new FakeController()

      val result = fake.processPlayerAction(SetPlayerCountAction(3))

      result.gamePhase should be(AskPlayerNamesPhase)
    }

    "simulate undo availability" in {
      val fake = new FakeController()

      fake.undo() should be(None)

      fake.processPlayerAction(SetPlayerCountAction(2))

      fake.undo() should be(defined)
    }
  }

  "Observable pattern through interface" should {

    "notify mock view on controller change" in {
      val mock = new MockController(StubGameState.setupStub())
      val mockView = new MockView()

      mock.add(mockView)

      mockView.updateCalled should be(false)

      mock.processPlayerAction(SetPlayerCountAction(2))

      mockView.updateCalled should be(true)
    }

    "notify multiple mock views" in {
      val mock = new MockController(StubGameState.setupStub())
      val view1 = new MockView()
      val view2 = new MockView()

      mock.add(view1)
      mock.add(view2)

      mock.processPlayerAction(SetPlayerCountAction(2))

      view1.updateCount should be(1)
      view2.updateCount should be(1)
    }
  }

  "Real Controller through ControllerInterface" should {

    "be testable via interface" in {
      val controller: Controller = Controller(
        StubGameState.setupStub(),
        UndoRedoManager()
      )

      val result = controller.processPlayerAction(SetPlayerCountAction(2))

      result.setupPlayerCount should be(Some(2))
      controller.gameState.gamePhase should be(AskPlayerNamesPhase)
    }

    "support observer pattern" in {
      val controller: Controller = Controller(
        StubGameState.setupStub(),
        UndoRedoManager()
      ).asInstanceOf[Controller]

      val mockView = new MockView()
      controller.add(mockView)

      controller.processPlayerAction(SetPlayerCountAction(2))

      mockView.updateCalled should be(true)
    }
  }

  "Integration test with all test doubles" should {

    "work together through interfaces" in {
      val mock = new MockController(StubGameState.setupStub())
      val spy = new SpyController(
        Controller(StubGameState.setupStub(), UndoRedoManager())
      )
      val fake = new FakeController()
      val view = new MockView()

      // All implement the same interface
      val controllers: List[Controller] = List(mock, spy, fake)

      controllers.foreach { controller =>
        controller.processPlayerAction(SetPlayerCountAction(2))
        controller.gameState.setupPlayerCount should be(Some(2))
      }
    }
  }
}
