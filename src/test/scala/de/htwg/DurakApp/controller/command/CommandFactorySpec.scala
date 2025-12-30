package de.htwg.DurakApp.controller.command

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Suit, Rank, GameState, Player}
import de.htwg.DurakApp.model.state.{AttackPhase, GameEvent}
import de.htwg.DurakApp.controller._

class CommandFactorySpec extends AnyWordSpec with Matchers {

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

  "CommandFactory" should {
    "create PlayCardCommand from PlayCardAction" in {
      val card = Card(Suit.Hearts, Rank.Six)
      val result = CommandFactory.createCommand(PlayCardAction(card), gameState)
      
      result.isRight shouldBe true
    }
    
    "create PassCommand from PassAction" in {
      val result = CommandFactory.createCommand(PassAction, gameState)
      
      result.isRight shouldBe true
    }
    
    "create TakeCardsCommand from TakeCardsAction" in {
      val result = CommandFactory.createCommand(TakeCardsAction, gameState)
      
      result.isRight shouldBe true
    }
    
    "return InvalidMove for InvalidAction" in {
      val result = CommandFactory.createCommand(InvalidAction, gameState)
      
      result.isLeft shouldBe true
      result.left.getOrElse(null) shouldBe GameEvent.InvalidMove
    }
    
    "return InvalidMove for UndoAction" in {
      val result = CommandFactory.createCommand(UndoAction, gameState)
      
      result.isLeft shouldBe true
      result.left.getOrElse(null) shouldBe GameEvent.InvalidMove
    }
    
    "return InvalidMove for RedoAction" in {
      val result = CommandFactory.createCommand(RedoAction, gameState)
      
      result.isLeft shouldBe true
      result.left.getOrElse(null) shouldBe GameEvent.InvalidMove
    }
    
    "return InvalidMove for SetPlayerCountAction" in {
      val result = CommandFactory.createCommand(SetPlayerCountAction(3), gameState)
      
      result.isLeft shouldBe true
      result.left.getOrElse(null) shouldBe GameEvent.InvalidMove
    }
    
    "return InvalidMove for AddPlayerNameAction" in {
      val result = CommandFactory.createCommand(AddPlayerNameAction("Test"), gameState)
      
      result.isLeft shouldBe true
      result.left.getOrElse(null) shouldBe GameEvent.InvalidMove
    }
    
    "return InvalidMove for SetDeckSizeAction" in {
      val result = CommandFactory.createCommand(SetDeckSizeAction(36), gameState)
      
      result.isLeft shouldBe true
      result.left.getOrElse(null) shouldBe GameEvent.InvalidMove
    }
    
    "return InvalidMove for PlayAgainAction" in {
      val result = CommandFactory.createCommand(PlayAgainAction, gameState)
      
      result.isLeft shouldBe true
      result.left.getOrElse(null) shouldBe GameEvent.InvalidMove
    }
    
    "return InvalidMove for ExitGameAction" in {
      val result = CommandFactory.createCommand(ExitGameAction, gameState)
      
      result.isLeft shouldBe true
      result.left.getOrElse(null) shouldBe GameEvent.InvalidMove
    }
    
    "create PlayCardCommand via factory method" in {
      val card = Card(Suit.Hearts, Rank.Six)
      val command = CommandFactory.playCard(card)
      
      command should not be null
    }
    
    "create PassCommand via factory method" in {
      val command = CommandFactory.pass()
      
      command should not be null
    }
    
    "create TakeCardsCommand via factory method" in {
      val command = CommandFactory.takeCards()
      
      command should not be null
    }
    
    "create PhaseChangeCommand via factory method" in {
      val command = CommandFactory.phaseChange()
      
      command should not be null
    }
  }
}
