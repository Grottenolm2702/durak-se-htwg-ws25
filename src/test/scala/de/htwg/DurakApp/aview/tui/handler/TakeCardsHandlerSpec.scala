package de.htwg.DurakApp.aview.tui.handler

import de.htwg.DurakApp.testutil.TestHelpers._

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Suit, Rank, GameState, Player}
import de.htwg.DurakApp.model.state.DefensePhase
import de.htwg.DurakApp.controller.{TakeCardsAction, InvalidAction}

class TakeCardsHandlerSpec extends AnyWordSpec with Matchers {

  val player1 = Player("Alice", List(Card(Suit.Hearts, Rank.Six)))
  val player2 = Player("Bob", List(Card(Suit.Diamonds, Rank.Seven)))
  val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)
  
  val gameState = GameState(
    players = List(player1, player2),
    deck = List.empty,
    table = Map(Card(Suit.Hearts, Rank.Six) -> None),
    discardPile = List.empty,
    trumpCard = trumpCard,
    attackerIndex = 0,
    defenderIndex = 1,
    gamePhase = DefensePhase,
    lastEvent = None,
    passedPlayers = Set.empty,
    roundWinner = None,
    setupPlayerCount = None,
    setupPlayerNames = List.empty,
    setupDeckSize = None,
    currentAttackerIndex = None,
    lastAttackerIndex = None
  )

  "A TakeCardsHandler" should {
    "handle take command" in {
      val handler = TakeCardsHandler()
      val result = handler.handleRequest("take", gameState)
      
      result shouldBe TakeCardsAction
    }
    
    "handle uppercase TAKE command" in {
      val handler = TakeCardsHandler()
      val result = handler.handleRequest("TAKE", gameState)
      
      result shouldBe TakeCardsAction
    }
    
    "handle take with extra whitespace" in {
      val handler = TakeCardsHandler()
      val result = handler.handleRequest("  take  ", gameState)
      
      result shouldBe TakeCardsAction
    }
    
    "delegate to next handler for non-take command" in {
      val nextHandler = InvalidInputHandler()
      val handler = TakeCardsHandler(Some(nextHandler))
      val result = handler.handleRequest("pass", gameState)
      
      result shouldBe InvalidAction
    }
  }
}
