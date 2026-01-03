package de.htwg.DurakApp.controller.command

import de.htwg.DurakApp.testutil.TestHelpers._

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Suit, Rank, GameState, Player}
import de.htwg.DurakApp.model.state.{AttackPhase, GameEvent}
import de.htwg.DurakApp.controller._

class GameCommandSpec extends AnyWordSpec with Matchers {

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
    currentAttackerIndex = Some(0),
    lastAttackerIndex = None
  )

  "GameCommand companion object" should {
    "create PlayCardCommand via createCommand" in {
      val card = Card(Suit.Hearts, Rank.Six)
      val result = GameCommand.createCommand(PlayCardAction(card), gameState)
      
      result.isRight shouldBe true
    }
    
    "create PassCommand via createCommand" in {
      val result = GameCommand.createCommand(PassAction, gameState)
      
      result.isRight shouldBe true
    }
    
    "create TakeCardsCommand via createCommand" in {
      val result = GameCommand.createCommand(TakeCardsAction, gameState)
      
      result.isRight shouldBe true
    }
    
    "return GameEvent for InvalidAction via createCommand" in {
      val result = GameCommand.createCommand(InvalidAction, gameState)
      
      result.isLeft shouldBe true
      result.left.getOrElse(null) shouldBe GameEvent.InvalidMove
    }
    
    "create PlayCardCommand via playCard factory method" in {
      val card = Card(Suit.Hearts, Rank.Six)
      val command = GameCommand.playCard(card)
      
      command should not be null
      val result = command.execute(gameState)
      result should not be null
    }
    
    "create PassCommand via pass factory method" in {
      val command = GameCommand.pass()
      
      command should not be null
    }
    
    "create TakeCardsCommand via takeCards factory method" in {
      val command = GameCommand.takeCards()
      
      command should not be null
    }
    
    "create PhaseChangeCommand via phaseChange factory method" in {
      val command = GameCommand.phaseChange()
      
      command should not be null
      val result = command.execute(gameState)
      result shouldBe gameState
    }
  }
  
  "GameCommand trait default undo" should {
    "return previous game state by default" in {
      val command = new GameCommand {
        override def execute(gameState: GameState): GameState = gameState
      }
      
      val currentState = gameState.copy(attackerIndex = 1)
      val previousState = gameState
      
      val result = command.undo(currentState, previousState)
      
      result shouldBe previousState
      result.attackerIndex shouldBe 0
    }
  }
}
