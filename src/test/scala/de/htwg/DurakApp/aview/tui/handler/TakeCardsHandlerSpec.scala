package de.htwg.DurakApp.aview.tui.handler

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model._
import de.htwg.DurakApp.model.state._
import de.htwg.DurakApp.controller._

class TakeCardsHandlerSpec extends AnyWordSpec with Matchers {

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

  "A TakeCardsHandler" should {
    val takeHandler = new TakeCardsHandler()
    val invalidHandler = new InvalidInputHandler()
    takeHandler.setNext(invalidHandler)

    "handle 'take' command" in {
      takeHandler.handleRequest("take", gameState) should be(TakeCardsAction)
    }

    "pass to next handler for non-take command" in {
      takeHandler.handleRequest("foo", gameState) should be(InvalidAction)
    }

    "return InvalidAction for unhandled command" in {
      val takeHandlerOnly = new TakeCardsHandler()
      takeHandlerOnly.handleRequest("foo", gameState) should be(InvalidAction)
    }
  }
}
