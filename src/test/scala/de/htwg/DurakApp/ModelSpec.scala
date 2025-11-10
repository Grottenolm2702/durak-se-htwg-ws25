package de.htwg.DurakApp

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.should.Matchers._
import de.htwg.DurakApp.model._

class ModelSpec extends AnyWordSpec with Matchers {

  "A Card" should {
    "store its suit, rank and trumpBool correctly" in {
      val card = Card(Suit.Hearts, Rank.Ace, isTrump = true)
      card.suit shouldBe Suit.Hearts
      card.rank shouldBe Rank.Ace
    }
  }
  "A Rank" should {
    "extend its value correctly" in {
      Rank.Six.value shouldBe 6
      Rank.Seven.value shouldBe 7
      Rank.Eight.value shouldBe 8
      Rank.Nine.value shouldBe 9
      Rank.Ten.value shouldBe 10
      Rank.Jack.value shouldBe 11
      Rank.Queen.value shouldBe 12
      Rank.King.value shouldBe 13
      Rank.Ace.value shouldBe 14
    }
  }
  "A Player" should {
    "store its name and hand correctly" in {
      val hand = List(
        Card(Suit.Hearts, Rank.Six, isTrump = false),
        Card(Suit.Clubs, Rank.Jack, isTrump = true)
      )
      val player = Player("Lucifer", hand, false)
      player.name shouldBe "Lucifer"
      player.hand shouldBe hand
    }
    "initialize hand and isDone correctly with minimal parameters" in {
      val player = Player("Michael") // nur Name angegeben
      player.name shouldBe "Michael"
      player.hand shouldBe List() // Defaultwert
      player.isDone shouldBe false // Defaultwert
    }
  }
  "A Game" should {
    "store its players, deck, table and trump correctly" in {
      val players = List(
        Player("Lucifer", List(), false),
        Player("Michael", List(), false)
      )
      val deck = List(
        Card(Suit.Spades, Rank.Ace, isTrump = false),
        Card(Suit.Diamonds, Rank.Ten, isTrump = true)
      )
      val trump = Suit.Hearts
      val gameState = GameState(players, deck, trump = trump)
      gameState.playerList shouldBe players
      gameState.deck shouldBe deck
      gameState.trump shouldBe trump
    }
  }
}
