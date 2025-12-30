package de.htwg.DurakApp.aview.tui.handler

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Suit, Rank, GameState, Player}
import de.htwg.DurakApp.model.state.AttackPhase
import de.htwg.DurakApp.controller.{UndoAction, InvalidAction, Controller}
import com.google.inject.Guice

class UndoHandlerSpec extends AnyWordSpec with Matchers {

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
    gamePhase = AttackPhase,
    lastEvent = None,
    passedPlayers = Set.empty,
    roundWinner = None,
    setupPlayerCount = None,
    setupPlayerNames = List.empty,
    setupDeckSize = None,
    currentAttackerIndex = None,
    lastAttackerIndex = None
  )

  "An UndoHandler" should {
    "handle undo command" in {
      val handler = UndoHandler(controller)
      val result = handler.handleRequest("undo", gameState)
      
      result shouldBe UndoAction
    }
    
    "handle z shortcut for undo" in {
      val handler = UndoHandler(controller)
      val result = handler.handleRequest("z", gameState)
      
      result shouldBe UndoAction
    }
    
    "handle u shortcut for undo" in {
      val handler = UndoHandler(controller)
      val result = handler.handleRequest("u", gameState)
      
      result shouldBe UndoAction
    }
    
    "handle uppercase UNDO command" in {
      val handler = UndoHandler(controller)
      val result = handler.handleRequest("UNDO", gameState)
      
      result shouldBe UndoAction
    }
    
    "delegate to next handler for non-undo command" in {
      val nextHandler = InvalidInputHandler()
      val handler = UndoHandler(controller, Some(nextHandler))
      val result = handler.handleRequest("pass", gameState)
      
      result shouldBe InvalidAction
    }
  }
}
