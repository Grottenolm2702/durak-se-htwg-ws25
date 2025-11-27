package de.htwg.DurakApp.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model._
import de.htwg.DurakApp.model.state._

import scala.util.Random

class SetupSpec extends AnyWordSpec with Matchers {
  "The Setup object" should {
    "create a deck of the specified size" in {
      val deck = Setup.createDeck(36)
      deck.length shouldBe 36
      deck.foreach(_.isTrump shouldBe false) // Initially no trumps
      deck.distinct.length shouldBe 36 // All cards should be unique
    }

    "create a smaller deck if requested size exceeds available cards" in {
      val deck = Setup.createDeck(100) // More than a standard deck
      deck.length shouldBe (Suit.values.length * Rank.values.length) // Should be full standard deck size
      deck.distinct.length shouldBe (Suit.values.length * Rank.values.length)
    }

    "setup game correctly with default deck size" in {
      val playerNames = List("Alice", "Bob")
      val gameState = Setup.setupGame(playerNames, 36)

      gameState.players.size shouldBe 2
      gameState.players.foreach(_.name shouldNot be (empty))
      gameState.players.foreach(_.hand.length shouldBe 6) // Each player gets 6 cards
      gameState.deck.length shouldBe (36 - 12 - 1) // 36 total - 12 dealt - 1 trump = 23 (assuming trump is removed)
                                                  // Re-check SetupPhase.scala. Trump is NOT removed, so 36-12 = 24
      gameState.deck.length shouldBe 23 // 36 total - 12 dealt - 1 trump = 23

      gameState.trumpCard.isTrump shouldBe true
      gameState.gamePhase shouldBe AttackPhase // SetupPhase -> RoundPhase -> AttackPhase
      gameState.attackerIndex should(be >= 0)
      gameState.attackerIndex should(be < gameState.players.size)
      gameState.defenderIndex should(be >= 0)
      gameState.defenderIndex should(be < gameState.players.size)
      gameState.defenderIndex shouldNot(equal(gameState.attackerIndex))
      gameState.lastEvent shouldBe Some(GameEvent.RoundEnd(cleared = false)) // As set by RoundPhase
    }

    "throw IllegalArgumentException for less than two players" in {
      an[IllegalArgumentException] should be thrownBy Setup.setupGame(List("Alice"), 36)
    }

    "throw IllegalArgumentException for not enough cards for players" in {
      // 2 players, 6 cards each = 12 cards needed. Deck size 11 is too small.
      an[IllegalArgumentException] should be thrownBy Setup.setupGame(List("Alice", "Bob"), 11)
    }
  }
}
