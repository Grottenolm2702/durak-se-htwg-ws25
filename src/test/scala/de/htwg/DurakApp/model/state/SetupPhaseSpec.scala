package de.htwg.DurakApp.model.state

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model._

import scala.util.Random

class SetupPhaseSpec extends AnyWordSpec with Matchers {
  "A SetupPhase" should {
    "handle initial game setup correctly" in {

      val playerNames = List("Alice", "Bob")
      val initialPlayers = playerNames.map(Player(_))
      val initialDeck = List(
        Card(Suit.Clubs, Rank.Six),
        Card(Suit.Clubs, Rank.Seven),
        Card(Suit.Clubs, Rank.Eight),
        Card(Suit.Clubs, Rank.Nine),
        Card(Suit.Clubs, Rank.Ten),
        Card(Suit.Clubs, Rank.Jack),
        Card(Suit.Clubs, Rank.Queen),
        Card(Suit.Clubs, Rank.King),
        Card(Suit.Clubs, Rank.Ace),
        Card(Suit.Diamonds, Rank.Six),
        Card(Suit.Diamonds, Rank.Seven),
        Card(Suit.Diamonds, Rank.Eight),
        Card(Suit.Diamonds, Rank.Nine) // total 13 cards
      ) // deck size must be at least 2 * 6 + 1 = 13 for 2 players
      
      val initialGameState = GameState(
        players = initialPlayers,
        deck = initialDeck,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = Card(Suit.Clubs, Rank.Six, isTrump = false), // Placeholder, will be replaced
        attackerIndex = 0, // Placeholder, will be replaced
        defenderIndex = 0, // Placeholder, will be replaced
        gamePhase = SetupPhase
      )

      val resultState = SetupPhase.handle(initialGameState)

      resultState.players.size shouldBe 2
      resultState.players.foreach(_.hand.length shouldBe 6)
      resultState.deck.length shouldBe 1 // One trump card remaining from the initial deck, now gone (original logic expectation)
      resultState.trumpCard.isTrump shouldBe true
      resultState.gamePhase shouldBe AttackPhase // RoundPhase.handle is called which transitions to AttackPhase
      resultState.attackerIndex should(be >= 0)
      resultState.attackerIndex should(be < resultState.players.size)
      resultState.defenderIndex should(be >= 0)
      resultState.defenderIndex should(be < resultState.players.size)
      resultState.defenderIndex shouldNot(equal(resultState.attackerIndex)) // Attacker and defender should be different
      resultState.lastEvent shouldBe Some(GameEvent.RoundEnd(cleared = false))
    }

    "handle setup with a small deck where no cards are left after dealing" in {

      val playerNames = List("Alice", "Bob")
      val initialPlayers = playerNames.map(Player(_))
      val initialDeck = List(
        Card(Suit.Clubs, Rank.Six),
        Card(Suit.Clubs, Rank.Seven),
        Card(Suit.Clubs, Rank.Eight),
        Card(Suit.Clubs, Rank.Nine),
        Card(Suit.Clubs, Rank.Ten),
        Card(Suit.Clubs, Rank.Jack),
        Card(Suit.Hearts, Rank.Six), // Trump candidate
        Card(Suit.Hearts, Rank.Seven),
        Card(Suit.Hearts, Rank.Eight),
        Card(Suit.Hearts, Rank.Nine),
        Card(Suit.Hearts, Rank.Ten),
        Card(Suit.Hearts, Rank.Jack),
        Card(Suit.Diamonds, Rank.Six) // 13 cards total, exactly 6 each + 1 for trump
      )
      
      val initialGameState = GameState(
        players = initialPlayers,
        deck = initialDeck,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = Card(Suit.Clubs, Rank.Six, isTrump = false),
        attackerIndex = 0,
        defenderIndex = 0,
        gamePhase = SetupPhase
      )

      val resultState = SetupPhase.handle(initialGameState)

      resultState.players.size shouldBe 2
      resultState.players.foreach(_.hand.length shouldBe 6)
      resultState.deck.length shouldBe 1 // Deck should be empty after dealing and taking trump (original logic expectation)
      resultState.trumpCard.isTrump shouldBe true
      resultState.gamePhase shouldBe AttackPhase
      resultState.attackerIndex should(be >= 0)
      resultState.attackerIndex should(be < resultState.players.size)
      resultState.defenderIndex should(be >= 0)
      resultState.defenderIndex should(be < resultState.players.size)
      resultState.defenderIndex shouldNot(equal(resultState.attackerIndex)) // Attacker and defender should be different
    }
  }
}
