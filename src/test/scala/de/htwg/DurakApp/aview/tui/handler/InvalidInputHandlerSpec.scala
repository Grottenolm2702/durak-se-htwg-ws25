package de.htwg.DurakApp.aview.tui.handler

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model._
import de.htwg.DurakApp.model.state._
import de.htwg.DurakApp.controller._

class InvalidInputHandlerSpec extends AnyWordSpec with Matchers {

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

  "An InvalidInputHandler" should {
    val invalidHandler = new InvalidInputHandler()

    "always return InvalidAction" in {
      invalidHandler.handleRequest("anything", gameState) should be(InvalidAction)
    }
  }
}
