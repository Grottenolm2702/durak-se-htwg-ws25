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
      deck.foreach(_.isTrump shouldBe false)
      deck.distinct.length shouldBe 36
    }

    "create a smaller deck if requested size exceeds available cards" in {
      val deck = Setup.createDeck(100)
      deck.length shouldBe (Suit.values.length * Rank.values.length)
      deck.distinct.length shouldBe (Suit.values.length * Rank.values.length)
    }

    "dealCards should handle handSize == 0 (no cards dealt to any player)" in {
      val players = List(
        Player(
          "A",
          List(Card(Suit.Clubs, Rank.Six))
        ),
        Player("B", List(Card(Suit.Clubs, Rank.Seven))),
        Player("C", List(Card(Suit.Clubs, Rank.Eight)))
      )

      val deck = List(
        Card(Suit.Diamonds, Rank.Ace)
      )

      val initial = GameState(
        players = players,
        deck = deck,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = deck.head,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = SetupPhase
      )

      val result = SetupPhase.handle(initial)

      result.players.foreach(_.hand shouldBe empty)

      result.deck.size shouldBe 0

      result.trumpCard.isTrump shouldBe true

      result.gamePhase should not be SetupPhase
    }

    "setup game correctly with default deck size" in {
      val playerNames = List("Alice", "Bob")
      val gameState = Setup.setupGame(playerNames, 36)

      gameState.players.size shouldBe 2
      gameState.players.foreach(_.name shouldNot be(empty))
      gameState.players.foreach(
        _.hand.length shouldBe 6
      )
      gameState.deck.length shouldBe (36 - 12 - 1)
      gameState.deck.length shouldBe 23

      gameState.trumpCard.isTrump shouldBe true
      gameState.gamePhase shouldBe AttackPhase
      gameState.attackerIndex should (be >= 0)
      gameState.attackerIndex should (be < gameState.players.size)
      gameState.defenderIndex should (be >= 0)
      gameState.defenderIndex should (be < gameState.players.size)
      gameState.defenderIndex shouldNot (equal(gameState.attackerIndex))
      gameState.lastEvent shouldBe Some(
        GameEvent.RoundEnd(cleared = false)
      )
    }

    "handle the remainingDeck.isEmpty branch (all cards dealt)" in {
      val p1 = Player("P1", List.empty)
      val p2 = Player("P2", List.empty)

      val deck = List(
        Card(Suit.Clubs, Rank.Six),
        Card(Suit.Clubs, Rank.Seven),
        Card(Suit.Diamonds, Rank.Six),
        Card(Suit.Diamonds, Rank.Seven)
      )

      val initial = GameState(
        players = List(p1, p2),
        deck = deck,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = deck.head,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = SetupPhase
      )

      val result = SetupPhase.handle(initial)

      result.deck shouldBe empty

      result.players.foreach(_.hand.length shouldBe 2)

      result.trumpCard.isTrump shouldBe true

      result.gamePhase should not be SetupPhase
    }

    "throw IllegalArgumentException for less than two players" in {
      val thrown = the[IllegalArgumentException] thrownBy Setup.setupGame(
        List("Alice"),
        36
      )
      thrown.getMessage should include("Need at least two players.")
    }

    "throw IllegalArgumentException for not enough cards for players" in {
      val thrown = the[IllegalArgumentException] thrownBy Setup.setupGame(
        List("Alice", "Bob"),
        11
      )
      thrown.getMessage should include("Not enough cards for")
    }
  }
}
