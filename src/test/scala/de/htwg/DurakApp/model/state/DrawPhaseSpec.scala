package de.htwg.DurakApp.model.state

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model._

class DrawPhaseSpec extends AnyWordSpec with Matchers {
  "A DrawPhase" should {
    "handle drawing cards for players with less than 6 cards" in {
      val player1 = Player("P1", List(Card(Suit.Clubs, Rank.Six))) // Needs 5 cards
      val player2 = Player("P2", List(Card(Suit.Diamonds, Rank.Seven), Card(Suit.Diamonds, Rank.Eight))) // Needs 4 cards
      val deck = List(
        Card(Suit.Hearts, Rank.Ace),
        Card(Suit.Hearts, Rank.King),
        Card(Suit.Hearts, Rank.Queen),
        Card(Suit.Hearts, Rank.Jack),
        Card(Suit.Hearts, Rank.Ten),
        Card(Suit.Spades, Rank.Nine),
        Card(Suit.Spades, Rank.Eight),
        Card(Suit.Spades, Rank.Seven),
        Card(Suit.Spades, Rank.Six)
      ) // Total 9 cards
      val initialGameState = GameState(
        players = List(player1, player2),
        deck = deck,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = Card(Suit.Clubs, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = DrawPhase,
        roundWinner = Some(0) // Attacker won the round, so defender should draw first.
      )

      val resultState = DrawPhase.handle(initialGameState)

      resultState.players(0).hand.length.shouldBe(6) // P1 (attacker)
      resultState.players(1).hand.length.shouldBe(6) // P2 (defender)
      resultState.deck.length.shouldBe(0) // All cards drawn

      resultState.gamePhase.shouldBe(AttackPhase) // After draw, it goes to RoundPhase, then AttackPhase
      resultState.lastEvent.get.shouldBe(GameEvent.RoundEnd(cleared = true))
    }

    "not change hands if all players have 6 or more cards" in {
      val player1 = Player("P1", List.fill(6)(Card(Suit.Clubs, Rank.Six)))
      val player2 = Player("P2", List.fill(7)(Card(Suit.Diamonds, Rank.Seven)))
      val deck = List(Card(Suit.Hearts, Rank.Ace))
      val initialGameState = GameState(
        players = List(player1, player2),
        deck = deck,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = Card(Suit.Clubs, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = DrawPhase,
        roundWinner = Some(0)
      )

      val resultState = DrawPhase.handle(initialGameState)

      resultState.players(0).hand.length.shouldBe(6)
      resultState.players(1).hand.length.shouldBe(7)
      resultState.deck.length.shouldBe(1) // Deck should be unchanged
      resultState.gamePhase.shouldBe(AttackPhase)
    }
  }
}
