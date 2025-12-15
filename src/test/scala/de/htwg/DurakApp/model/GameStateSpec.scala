package de.htwg.DurakApp.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.ModelInterface.*
import de.htwg.DurakApp.model.ModelInterface.StateInterface.*

class GameStateSpec extends AnyWordSpec with Matchers {

  "A Game" should {
    "store its players, deck, table and trump correctly" in {
      val players = List(
        Player("Lucifer", List.empty, isDone = false),
        Player("Michael", List.empty, isDone = false)
      )
      val deck = List(
        Card(Suit.Spades, Rank.Ace, isTrump = false),
        Card(Suit.Diamonds, Rank.Ten, isTrump = true)
      )
      val trumpSuit = Suit.Hearts
      val trumpCard = Card(trumpSuit, Rank.Six, isTrump = true)

      val gameState = GameState(
        players = players,
        deck = deck,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = SetupPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None
      )
      gameState.players.shouldBe(players)
      gameState.deck.shouldBe(deck)
      gameState.trumpCard.shouldBe(trumpCard)
    }
    
    "support copy operations" in {
      val players = List(Player("P1"), Player("P2"))
      val trumpCard = Card(Suit.Hearts, Rank.Ace)
      val gameState = GameState(
        players = players,
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = SetupPhase
      )
      
      val newDeck = List(Card(Suit.Clubs, Rank.Six))
      val copied = gameState.copy(deck = newDeck)
      
      copied.deck.shouldBe(newDeck)
      copied.players.shouldBe(players)
    }
    
    "use default parameters when not explicitly provided" in {
      val players = List(Player("P1"), Player("P2"))
      val trumpCard = Card(Suit.Hearts, Rank.Ace)
      
      val gameState = GameState(
        players = players,
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = SetupPhase
      )
      
      gameState.lastEvent shouldBe None
      gameState.passedPlayers shouldBe Set.empty
      gameState.roundWinner shouldBe None
      gameState.setupPlayerCount shouldBe None
      gameState.setupPlayerNames shouldBe List.empty
      gameState.setupDeckSize shouldBe None
      gameState.currentAttackerIndex shouldBe None
      gameState.lastAttackerIndex shouldBe None
    }
    
    "use default parameter for lastEvent" in {
      val players = List(Player("P1"), Player("P2"))
      val gameState = GameState(
        players, List.empty, Map.empty, List.empty,
        Card(Suit.Hearts, Rank.Ace), 0, 1, SetupPhase
      )
      gameState.lastEvent shouldBe None
    }
    
    "use default parameter for passedPlayers" in {
      val players = List(Player("P1"), Player("P2"))
      val gameState = GameState(
        players, List.empty, Map.empty, List.empty,
        Card(Suit.Hearts, Rank.Ace), 0, 1, SetupPhase,
        lastEvent = Some(GameEvent.Pass)
      )
      gameState.passedPlayers shouldBe Set.empty
    }
    
    "use default parameter for roundWinner" in {
      val players = List(Player("P1"), Player("P2"))
      val gameState = GameState(
        players, List.empty, Map.empty, List.empty,
        Card(Suit.Hearts, Rank.Ace), 0, 1, SetupPhase,
        lastEvent = None, passedPlayers = Set.empty
      )
      gameState.roundWinner shouldBe None
    }
    
    "use default parameter for setupPlayerCount" in {
      val players = List(Player("P1"), Player("P2"))
      val gameState = GameState(
        players, List.empty, Map.empty, List.empty,
        Card(Suit.Hearts, Rank.Ace), 0, 1, SetupPhase,
        None, Set.empty, None
      )
      gameState.setupPlayerCount shouldBe None
    }
    
    "use default parameter for setupPlayerNames" in {
      val players = List(Player("P1"), Player("P2"))
      val gameState = GameState(
        players, List.empty, Map.empty, List.empty,
        Card(Suit.Hearts, Rank.Ace), 0, 1, SetupPhase,
        None, Set.empty, None, None
      )
      gameState.setupPlayerNames shouldBe List.empty
    }
    
    "use default parameter for setupDeckSize" in {
      val players = List(Player("P1"), Player("P2"))
      val gameState = GameState(
        players, List.empty, Map.empty, List.empty,
        Card(Suit.Hearts, Rank.Ace), 0, 1, SetupPhase,
        None, Set.empty, None, None, List.empty
      )
      gameState.setupDeckSize shouldBe None
    }
    
    "use default parameter for currentAttackerIndex" in {
      val players = List(Player("P1"), Player("P2"))
      val gameState = GameState(
        players, List.empty, Map.empty, List.empty,
        Card(Suit.Hearts, Rank.Ace), 0, 1, SetupPhase,
        None, Set.empty, None, None, List.empty, None
      )
      gameState.currentAttackerIndex shouldBe None
    }
    
    "use default parameter for lastAttackerIndex" in {
      val players = List(Player("P1"), Player("P2"))
      val gameState = GameState(
        players, List.empty, Map.empty, List.empty,
        Card(Suit.Hearts, Rank.Ace), 0, 1, SetupPhase,
        None, Set.empty, None, None, List.empty, None, None
      )
      gameState.lastAttackerIndex shouldBe None
    }

  }
}
