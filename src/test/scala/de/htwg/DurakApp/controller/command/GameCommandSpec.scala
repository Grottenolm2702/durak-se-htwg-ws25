package de.htwg.DurakApp.controller.command

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.controller.command.CommandInterface._
import de.htwg.DurakApp.model.ModelInterface._
import de.htwg.DurakApp.model.ModelInterface.StateInterface._

class GameCommandSpec extends AnyWordSpec with Matchers {
  "GameCommand PlayCardCommand" should {
    "be created with a Card parameter" in {
      val card = Card(Suit.Hearts, Rank.Ace)
      val command = PlayCardCommand(card)
      
      command shouldBe a[GameCommand]
    }
    
    "execute on GameState" in {
      val card = Card(Suit.Hearts, Rank.Ace)
      val command = PlayCardCommand(card)
      val player = Player("Alice", List(card))
      val gameState = GameState(
        players = List(player, Player("Bob")),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = card,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = AttackPhase
      )
      
      noException should be thrownBy command.execute(gameState)
    }
  }
  
  "GameCommand PassCommand" should {
    "be created without parameters" in {
      val command = PassCommand()
      
      command shouldBe a[GameCommand]
    }
    
    "execute on GameState" in {
      val command = PassCommand()
      val gameState = GameState(
        players = List(Player("Alice"), Player("Bob")),
        deck = List.empty,
        table = Map(Card(Suit.Hearts, Rank.Ace) -> None),
        discardPile = List.empty,
        trumpCard = Card(Suit.Clubs, Rank.Six),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = AttackPhase
      )
      
      noException should be thrownBy command.execute(gameState)
    }
  }
  
  "GameCommand TakeCardsCommand" should {
    "be created without parameters" in {
      val command = TakeCardsCommand()
      
      command shouldBe a[GameCommand]
    }
    
    "execute on GameState" in {
      val command = TakeCardsCommand()
      val gameState = GameState(
        players = List(Player("Alice"), Player("Bob")),
        deck = List.empty,
        table = Map(Card(Suit.Hearts, Rank.Ace) -> None),
        discardPile = List.empty,
        trumpCard = Card(Suit.Clubs, Rank.Six),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = DefensePhase
      )
      
      noException should be thrownBy command.execute(gameState)
    }
  }
  
  "GameCommand PhaseChangeCommand" should {
    "be created without parameters" in {
      val command = PhaseChangeCommand()
      
      command shouldBe a[GameCommand]
    }
    
    "execute on GameState" in {
      val command = PhaseChangeCommand()
      val gameState = GameState(
        players = List(Player("Alice"), Player("Bob")),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = Card(Suit.Clubs, Rank.Six),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = DrawPhase
      )
      
      noException should be thrownBy command.execute(gameState)
    }
  }
}
