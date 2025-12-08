package de.htwg.DurakApp.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.state.{GamePhase, SetupPhase}

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

  }
}
