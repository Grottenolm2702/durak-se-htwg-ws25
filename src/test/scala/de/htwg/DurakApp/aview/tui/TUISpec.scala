package de.htwg.DurakApp.aview.tui

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Player, Suit, Rank}
import de.htwg.DurakApp.model.state._
import de.htwg.DurakApp.testutil.{TestHelper, StubGameSetup, StubUndoRedoManager, SpyController}

class TUISpec extends AnyWordSpec with Matchers {

  "A TUI" should {

    "be created with a controller" in {
      val initialState = TestHelper.createTestGameState()
      val controller = new SpyController(
        initialState,
        new StubUndoRedoManager(),
        new StubGameSetup()
      )
      val tui = new TUI(controller)
      
      tui should not be null
    }

    "build status string for attack phase" in {
      val attacker = Player("Angreifer", List.empty)
      val defender = Player("Verteidiger", List.empty)
      val game = TestHelper.createTestGameState(
        players = List(attacker, defender),
        gamePhase = AttackPhase,
        lastEvent = Some(GameEvent.Attack(Card(Suit.Spades, Rank.Six)))
      )
      
      val controller = new SpyController(game, new StubUndoRedoManager(), new StubGameSetup())
      val tui = new TUI(controller)
      val statusString = tui.buildStatusString(game)
      
      statusString should not be empty
      statusString should include("Angriff")
    }

    "respond to update notification" in {
      val initialState = TestHelper.createTestGameState()
      val controller = new SpyController(
        initialState,
        new StubUndoRedoManager(),
        new StubGameSetup()
      )
      
      val tui = new TUI(controller)
      noException should be thrownBy tui.update
    }
  }
}
