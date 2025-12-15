package de.htwg.DurakApp.model.state.impl

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.ModelInterface.*
import de.htwg.DurakApp.model.ModelInterface.StateInterface.*

import scala.util.Random

class SetupPhaseImplSpec extends AnyWordSpec with Matchers {
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
        Card(Suit.Diamonds, Rank.Nine)
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
      resultState.deck.length shouldBe 0
      resultState.trumpCard.isTrump shouldBe true
      resultState.gamePhase shouldBe AttackPhase
      resultState.attackerIndex should (be >= 0)
      resultState.attackerIndex should (be < resultState.players.size)
      resultState.defenderIndex should (be >= 0)
      resultState.defenderIndex should (be < resultState.players.size)
      resultState.defenderIndex shouldNot (equal(resultState.attackerIndex))
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
        Card(Suit.Hearts, Rank.Six),
        Card(Suit.Hearts, Rank.Seven),
        Card(Suit.Hearts, Rank.Eight),
        Card(Suit.Hearts, Rank.Nine),
        Card(Suit.Hearts, Rank.Ten),
        Card(Suit.Hearts, Rank.Jack),
        Card(Suit.Diamonds, Rank.Six)
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
      resultState.deck.length shouldBe 0
      resultState.trumpCard.isTrump shouldBe true
      resultState.gamePhase shouldBe AttackPhase
      resultState.attackerIndex should (be >= 0)
      resultState.attackerIndex should (be < resultState.players.size)
      resultState.defenderIndex should (be >= 0)
      resultState.defenderIndex should (be < resultState.players.size)
      resultState.defenderIndex shouldNot (equal(resultState.attackerIndex))
    }

    "have a string representation" in {
      SetupPhase.toString should not be empty
    }
  }
}
