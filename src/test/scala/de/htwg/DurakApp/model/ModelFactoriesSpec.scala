package de.htwg.DurakApp.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class ModelFactoriesSpec extends AnyWordSpec with Matchers {

  "CardFactory" should {
    "create card with all parameters" in {
      val card = CardFactory(Suit.Hearts, Rank.Ace, isTrump = true)
      
      card.suit shouldBe Suit.Hearts
      card.rank shouldBe Rank.Ace
      card.isTrump shouldBe true
    }
    
    "create card with default isTrump" in {
      val card = CardFactory(Suit.Diamonds, Rank.King)
      
      card.suit shouldBe Suit.Diamonds
      card.rank shouldBe Rank.King
      card.isTrump shouldBe false
    }
  }

  "PlayerFactory" should {
    "create player with all parameters" in {
      val cards = List(Card(Suit.Hearts, Rank.Six))
      val player = PlayerFactory("Alice", cards, isDone = true)
      
      player.name shouldBe "Alice"
      player.hand shouldBe cards
      player.isDone shouldBe true
    }
    
    "create player with default hand" in {
      val player = PlayerFactory("Bob")
      
      player.name shouldBe "Bob"
      player.hand shouldBe empty
      player.isDone shouldBe false
    }
    
    "create player with default isDone" in {
      val cards = List(Card(Suit.Hearts, Rank.Six))
      val player = PlayerFactory("Charlie", cards)
      
      player.name shouldBe "Charlie"
      player.hand shouldBe cards
      player.isDone shouldBe false
    }
  }

  "GameStateFactory" should {
    "create game state with all parameters" in {
      val player1 = Player("Alice", List(Card(Suit.Hearts, Rank.Six)))
      val player2 = Player("Bob", List(Card(Suit.Diamonds, Rank.Seven)))
      val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)
      val table = Map(Card(Suit.Hearts, Rank.Six) -> None)
      
      val gameState = GameStateFactory(
        players = List(player1, player2),
        deck = List.empty,
        table = table,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = state.AttackPhase,
        lastEvent = Some(state.GameEvent.Attack(Card(Suit.Hearts, Rank.Six))),
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = Some(2),
        setupPlayerNames = List("Alice", "Bob"),
        setupDeckSize = Some(36),
        currentAttackerIndex = Some(0),
        lastAttackerIndex = None
      )
      
      gameState.players shouldBe List(player1, player2)
      gameState.deck shouldBe empty
      gameState.table shouldBe table
      gameState.trumpCard shouldBe trumpCard
      gameState.attackerIndex shouldBe 0
      gameState.defenderIndex shouldBe 1
      gameState.setupPlayerCount shouldBe Some(2)
    }
  }
}
