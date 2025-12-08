package de.htwg.DurakApp.aview.tui.handler

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.controller.*
import de.htwg.DurakApp.model.GameState
import de.htwg.DurakApp.model.state.*

import de.htwg.DurakApp.model.builder.GameStateBuilder

class GamePhaseInputHandlerSpec extends AnyWordSpec with Matchers {

  "A GamePhaseInputHandler" when {

    // Helper to create a GameState with a specific phase
    def gameStateWithPhase(phase: GamePhase): GameState =
      GameStateBuilder().withGamePhase(phase).build()

    val nextHandler = new InputHandler {
      override val next: Option[InputHandler] = None
      override def handleRequest(input: String, gameState: GameState): PlayerAction = {
        if (input == "fallback") PlayCardAction(null) // Mock action for testing fallback
        else InvalidAction
      }
    }

    val handler = new GamePhaseInputHandler(Some(nextHandler))
    val handlerNoNext = new GamePhaseInputHandler(None)


    "in SetupPhase or AskPlayerCountPhase" should {
      "return SetPlayerCountAction for valid integer input" in {
        handler.handleRequest("3", gameStateWithPhase(SetupPhase)) should be(SetPlayerCountAction(3))
        handler.handleRequest("2", gameStateWithPhase(AskPlayerCountPhase)) should be(SetPlayerCountAction(2))
      }

      "return InvalidAction for invalid integer input" in {
        handler.handleRequest("abc", gameStateWithPhase(SetupPhase)) should be(InvalidAction)
        handler.handleRequest("", gameStateWithPhase(AskPlayerCountPhase)) should be(InvalidAction)
      }
    }

    "in AskPlayerNamesPhase" should {
      "return AddPlayerNameAction for any string input" in {
        handler.handleRequest("Ronny", gameStateWithPhase(AskPlayerNamesPhase)) should be(AddPlayerNameAction("Ronny"))
        handler.handleRequest("  Anna  ", gameStateWithPhase(AskPlayerNamesPhase)) should be(AddPlayerNameAction("Anna"))
        handler.handleRequest("", gameStateWithPhase(AskPlayerNamesPhase)) should be(AddPlayerNameAction(""))
      }
    }

    "in AskDeckSizePhase" should {
      "return SetDeckSizeAction for valid integer input" in {
        handler.handleRequest("24", gameStateWithPhase(AskDeckSizePhase)) should be(SetDeckSizeAction(24))
        handler.handleRequest("36", gameStateWithPhase(AskDeckSizePhase)) should be(SetDeckSizeAction(36))
      }

      "return InvalidAction for invalid integer input" in {
        handler.handleRequest("xyz", gameStateWithPhase(AskDeckSizePhase)) should be(InvalidAction)
        handler.handleRequest("  ", gameStateWithPhase(AskDeckSizePhase)) should be(InvalidAction)
      }
    }

    "in AskPlayAgainPhase" should {
      "return PlayAgainAction for 'yes' (case-insensitive)" in {
        handler.handleRequest("yes", gameStateWithPhase(AskPlayAgainPhase)) should be(PlayAgainAction)
        handler.handleRequest("YES", gameStateWithPhase(AskPlayAgainPhase)) should be(PlayAgainAction)
        handler.handleRequest(" Yes ", gameStateWithPhase(AskPlayAgainPhase)) should be(PlayAgainAction)
      }

      "return ExitGameAction for 'no' (case-insensitive)" in {
        handler.handleRequest("no", gameStateWithPhase(AskPlayAgainPhase)) should be(ExitGameAction)
        handler.handleRequest("NO", gameStateWithPhase(AskPlayAgainPhase)) should be(ExitGameAction)
        handler.handleRequest(" No ", gameStateWithPhase(AskPlayAgainPhase)) should be(ExitGameAction)
      }

      "return InvalidAction for other inputs" in {
        handler.handleRequest("maybe", gameStateWithPhase(AskPlayAgainPhase)) should be(InvalidAction)
        handler.handleRequest("y", gameStateWithPhase(AskPlayAgainPhase)) should be(InvalidAction)
        handler.handleRequest("", gameStateWithPhase(AskPlayAgainPhase)) should be(InvalidAction)
      }
    }

    "in any other phase" should {
      "delegate to the next handler if present" in {
        handler.handleRequest("fallback", gameStateWithPhase(AttackPhase)) should be(PlayCardAction(null)) // Our mock action
      }

      "return InvalidAction if no next handler is present" in {
        handlerNoNext.handleRequest("some input", gameStateWithPhase(AttackPhase)) should be(InvalidAction)
      }
    }
  }
}
