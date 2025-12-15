package de.htwg.DurakApp.aview.tui.handler

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.ModelInterface.*
import de.htwg.DurakApp.model.ModelInterface.StateInterface.*
import de.htwg.DurakApp.controller.ControllerInterface.*

class PassHandlerSpec extends AnyWordSpec with Matchers {

  val gameState = GameState(
    players = List.empty,
    deck = List.empty,
    table = Map.empty,
    discardPile = List.empty,
    trumpCard = Card(Suit.Clubs, Rank.Seven, isTrump = true),
    attackerIndex = 0,
    defenderIndex = 1,
    gamePhase = AttackPhase,
    lastEvent = None,
    passedPlayers = Set.empty,
    roundWinner = None
  )

  "A PassHandler" should {
    val takeHandler = new TakeCardsHandler()
    val passHandler = new PassHandler(Some(takeHandler))

    "handle 'pass' command" in {
      passHandler.handleRequest("pass", gameState) should be(PassAction)
    }

    "pass to next handler for non-pass command" in {
      passHandler.handleRequest("take", gameState) should be(TakeCardsAction)
    }

    "return InvalidAction for unhandled command" in {
      val passHandlerOnly = new PassHandler()
      passHandlerOnly.handleRequest("foo", gameState) should be(InvalidAction)
    }
  }
}
