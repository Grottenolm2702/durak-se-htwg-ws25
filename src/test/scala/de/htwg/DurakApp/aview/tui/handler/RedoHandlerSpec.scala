package de.htwg.DurakApp.aview.tui.handler

import de.htwg.DurakApp.testutil.TestHelpers._

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Suit, Rank, GameState, Player}
import de.htwg.DurakApp.testutil.TestGamePhases
import de.htwg.DurakApp.controller.{RedoAction, InvalidAction, Controller}
import com.google.inject.Guice

class RedoHandlerSpec extends AnyWordSpec with Matchers {

  val injector = Guice.createInjector(new de.htwg.DurakApp.DurakModule)
  val controller = injector.getInstance(classOf[Controller])
  
  val player1 = Player("Alice", List(Card(Suit.Hearts, Rank.Six)))
  val player2 = Player("Bob", List(Card(Suit.Diamonds, Rank.Seven)))
  val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)
  
  val gameState = GameState(
    players = List(player1, player2),
    deck = List.empty,
    table = Map.empty,
    discardPile = List.empty,
    trumpCard = trumpCard,
    attackerIndex = 0,
    defenderIndex = 1,
    gamePhase = TestGamePhases.setupPhase,
        lastEvent = None,
    passedPlayers = Set.empty,
    roundWinner = None,
    setupPlayerCount = None,
    setupPlayerNames = List.empty,
    setupDeckSize = None,
    currentAttackerIndex = None,
    lastAttackerIndex = None
  )

  "A RedoHandler" should {
    "handle redo command" in {
      val handler = RedoHandler(controller)
      val result = handler.handleRequest("redo", gameState)
      
      result shouldBe RedoAction
    }
    
    "handle y shortcut for redo" in {
      val handler = RedoHandler(controller)
      val result = handler.handleRequest("y", gameState)
      
      result shouldBe RedoAction
    }
    
    "handle r shortcut for redo" in {
      val handler = RedoHandler(controller)
      val result = handler.handleRequest("r", gameState)
      
      result shouldBe RedoAction
    }
    
    "handle uppercase REDO command" in {
      val handler = RedoHandler(controller)
      val result = handler.handleRequest("REDO", gameState)
      
      result shouldBe RedoAction
    }
    
    "delegate to next handler for non-redo command" in {
      val nextHandler = InvalidInputHandler()
      val handler = RedoHandler(controller, Some(nextHandler))
      val result = handler.handleRequest("pass", gameState)
      
      result shouldBe InvalidAction
    }
  }
}
