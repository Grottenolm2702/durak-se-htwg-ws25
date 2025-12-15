package de.htwg.DurakApp.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.ModelInterface._
import de.htwg.DurakApp.model.ModelInterface.StateInterface._

class ModelInterfaceSpec extends AnyWordSpec with Matchers {
  "ModelInterface Card factory" should {
    "create Cards with correct suit and rank" in {
      val heartsAce = Card(Suit.Hearts, Rank.Ace)
      val spadesKing = Card(Suit.Spades, Rank.King)
      
      heartsAce.suit shouldBe Suit.Hearts
      heartsAce.rank shouldBe Rank.Ace
      spadesKing.suit shouldBe Suit.Spades
      spadesKing.rank shouldBe Rank.King
    }
    
    "create Cards with trump flag" in {
      val trump = Card(Suit.Diamonds, Rank.Six, isTrump = true)
      val normal = Card(Suit.Clubs, Rank.Seven, isTrump = false)
      
      trump.isTrump shouldBe true
      normal.isTrump shouldBe false
    }
    
    "create Cards with default isTrump=false" in {
      val card = Card(Suit.Hearts, Rank.Ace)
      card.isTrump shouldBe false
    }
  }
  
  "ModelInterface Player factory" should {
    "create Players with name and hand" in {
      val card = Card(Suit.Hearts, Rank.Ace)
      val player = Player("Alice", List(card))
      
      player.name shouldBe "Alice"
      player.hand should contain(card)
      player.hand should have size 1
    }
    
    "create Players with empty hand by default" in {
      val player = Player("Bob")
      
      player.name shouldBe "Bob"
      player.hand shouldBe empty
      player.isDone shouldBe false
    }
    
    "create Players with isDone flag" in {
      val done = Player("Winner", List.empty, isDone = true)
      val playing = Player("Loser", List.empty, isDone = false)
      
      done.isDone shouldBe true
      playing.isDone shouldBe false
    }
  }
  
  "ModelInterface GameState factory" should {
    "create GameState with all required parameters" in {
      val card = Card(Suit.Hearts, Rank.Ace)
      val player = Player("Alice", List(card))
      val gameState = GameState(
        players = List(player),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = card,
        attackerIndex = 0,
        defenderIndex = 0,
        gamePhase = SetupPhase
      )
      
      gameState.players should have size 1
      gameState.deck shouldBe empty
      gameState.table shouldBe empty
      gameState.gamePhase shouldBe SetupPhase
    }
  }
  
  "ModelInterface GameStateBuilder factory" should {
    "create builder that builds default GameState" in {
      val builder = GameStateBuilder()
      val gameState = builder.build()
      
      gameState.players shouldBe empty
      gameState.deck shouldBe empty
      gameState.gamePhase shouldBe SetupPhase
    }
    
    "create builder that supports fluent API" in {
      val player = Player("Alice")
      val gameState = GameStateBuilder()
        .withPlayers(List(player))
        .withGamePhase(AttackPhase)
        .build()
      
      gameState.players should contain(player)
      gameState.gamePhase shouldBe AttackPhase
    }
  }
  
  "ModelInterface Rank enum" should {
    "provide all card ranks from Six to Ace" in {
      Rank.values should contain allOf (
        Rank.Six, Rank.Seven, Rank.Eight, Rank.Nine, Rank.Ten,
        Rank.Jack, Rank.Queen, Rank.King, Rank.Ace
      )
    }
  }
  
  "ModelInterface Suit enum" should {
    "provide all four suits" in {
      Suit.values should contain allOf (
        Suit.Hearts, Suit.Diamonds, Suit.Clubs, Suit.Spades
      )
    }
  }
}
