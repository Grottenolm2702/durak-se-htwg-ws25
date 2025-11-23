package de.htwg.DurakApp.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class GameStateSpec extends AnyWordSpec with Matchers {

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
