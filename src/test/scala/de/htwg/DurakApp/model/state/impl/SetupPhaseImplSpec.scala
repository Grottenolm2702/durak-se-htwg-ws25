package de.htwg.DurakApp.model.state.impl

import de.htwg.DurakApp.testutil._
import de.htwg.DurakApp.testutil.RealPhaseTestHelper

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Player, Suit, Rank}
import de.htwg.DurakApp.model.state._

class SetupPhaseImplSpec extends AnyWordSpec with Matchers {
  "SetupPhaseImpl" should {
    "handle initial game setup correctly" in {

      val playerNames = List("Alice", "Bob")
      val initialPlayers = playerNames.map(RealPhaseTestHelper.playerFactory(_))
      val initialDeck = List(
        RealPhaseTestHelper.cardFactory(Suit.Clubs, Rank.Six),
        RealPhaseTestHelper.cardFactory(Suit.Clubs, Rank.Seven),
        RealPhaseTestHelper.cardFactory(Suit.Clubs, Rank.Eight),
        RealPhaseTestHelper.cardFactory(Suit.Clubs, Rank.Nine),
        RealPhaseTestHelper.cardFactory(Suit.Clubs, Rank.Ten),
        RealPhaseTestHelper.cardFactory(Suit.Clubs, Rank.Jack),
        RealPhaseTestHelper.cardFactory(Suit.Clubs, Rank.Queen),
        RealPhaseTestHelper.cardFactory(Suit.Clubs, Rank.King),
        RealPhaseTestHelper.cardFactory(Suit.Clubs, Rank.Ace),
        RealPhaseTestHelper.cardFactory(Suit.Diamonds, Rank.Six),
        RealPhaseTestHelper.cardFactory(Suit.Diamonds, Rank.Seven),
        RealPhaseTestHelper.cardFactory(Suit.Diamonds, Rank.Eight),
        RealPhaseTestHelper.cardFactory(Suit.Diamonds, Rank.Nine)
      )

      val initialGameState = RealPhaseTestHelper.createGameStateWithRealPhases(
        players = initialPlayers,
        deck = initialDeck,
        trumpCard = RealPhaseTestHelper
          .cardFactory(Suit.Clubs, Rank.Six, isTrump = false),
        defenderIndex = 0,
        gamePhase = SetupPhaseImpl
      )

      val resultState = SetupPhaseImpl.handle(initialGameState)

      resultState.players.size shouldBe 2
      resultState.players.foreach(_.hand.length shouldBe 6)
      resultState.deck.length shouldBe 0
      resultState.trumpCard.isTrump shouldBe true
      resultState.gamePhase shouldBe AttackPhaseImpl
      resultState.attackerIndex should (be >= 0)
      resultState.attackerIndex should (be < resultState.players.size)
      resultState.defenderIndex should (be >= 0)
      resultState.defenderIndex should (be < resultState.players.size)
      resultState.defenderIndex shouldNot (equal(resultState.attackerIndex))
      resultState.lastEvent shouldBe Some(GameEvent.RoundEnd(cleared = false))
    }

    "handle setup with a small deck where no cards are left after dealing" in {

      val playerNames = List("Alice", "Bob")
      val initialPlayers = playerNames.map(RealPhaseTestHelper.playerFactory(_))
      val initialDeck = List(
        TestHelper.Card(Suit.Clubs, Rank.Six),
        TestHelper.Card(Suit.Clubs, Rank.Seven),
        TestHelper.Card(Suit.Clubs, Rank.Eight),
        TestHelper.Card(Suit.Clubs, Rank.Nine),
        TestHelper.Card(Suit.Clubs, Rank.Ten),
        TestHelper.Card(Suit.Clubs, Rank.Jack),
        TestHelper.Card(Suit.Hearts, Rank.Six),
        TestHelper.Card(Suit.Hearts, Rank.Seven),
        TestHelper.Card(Suit.Hearts, Rank.Eight),
        TestHelper.Card(Suit.Hearts, Rank.Nine),
        TestHelper.Card(Suit.Hearts, Rank.Ten),
        TestHelper.Card(Suit.Hearts, Rank.Jack),
        TestHelper.Card(Suit.Diamonds, Rank.Six)
      )

      val initialGameState = RealPhaseTestHelper.createGameStateWithRealPhases(
        players = initialPlayers,
        deck = initialDeck,
        trumpCard = TestHelper.Card(Suit.Clubs, Rank.Six, isTrump = false),
        defenderIndex = 0,
        gamePhase = SetupPhaseImpl
      )

      val resultState = SetupPhaseImpl.handle(initialGameState)

      resultState.players.size shouldBe 2
      resultState.players.foreach(_.hand.length shouldBe 6)
      resultState.deck.length shouldBe 0
      resultState.trumpCard.isTrump shouldBe true
      resultState.gamePhase shouldBe AttackPhaseImpl
      resultState.attackerIndex should (be >= 0)
      resultState.attackerIndex should (be < resultState.players.size)
      resultState.defenderIndex should (be >= 0)
      resultState.defenderIndex should (be < resultState.players.size)
      resultState.defenderIndex shouldNot (equal(resultState.attackerIndex))
    }

    "have a string representation" in {
      SetupPhaseImpl.toString should not be empty
    }

    "handle deck where handSize is 0 due to more players than cards" in {
      val playerNames = List("P1", "P2", "P3", "P4", "P5", "P6")
      val initialPlayers = playerNames.map(RealPhaseTestHelper.playerFactory(_))
      val initialDeck = List(
        TestHelper.Card(Suit.Clubs, Rank.Six),
        TestHelper.Card(Suit.Clubs, Rank.Seven),
        TestHelper.Card(Suit.Diamonds, Rank.Six)
      )

      val initialGameState = RealPhaseTestHelper.createGameStateWithRealPhases(
        players = initialPlayers,
        deck = initialDeck,
        trumpCard = TestHelper.Card(Suit.Clubs, Rank.Six, isTrump = false),
        defenderIndex = 0,
        gamePhase = SetupPhaseImpl
      )

      val resultState = SetupPhaseImpl.handle(initialGameState)

      resultState.players.size shouldBe 6
      resultState.players.foreach(_.hand shouldBe empty)
      resultState.trumpCard.isTrump shouldBe true
    }

    "handle deck with exactly 1 card per player" in {
      val playerNames = List("Alice", "Bob")
      val initialPlayers = playerNames.map(RealPhaseTestHelper.playerFactory(_))
      val initialDeck = List(
        TestHelper.Card(Suit.Clubs, Rank.Six),
        TestHelper.Card(Suit.Diamonds, Rank.Seven),
        TestHelper.Card(Suit.Hearts, Rank.Eight)
      )

      val initialGameState = RealPhaseTestHelper.createGameStateWithRealPhases(
        players = initialPlayers,
        deck = initialDeck,
        trumpCard = TestHelper.Card(Suit.Clubs, Rank.Six, isTrump = false),
        defenderIndex = 0,
        gamePhase = SetupPhaseImpl
      )

      val resultState = SetupPhaseImpl.handle(initialGameState)

      resultState.players.size shouldBe 2
      resultState.players.foreach(_.hand.length shouldBe 1)
      resultState.trumpCard.isTrump shouldBe true
    }
  }
}
