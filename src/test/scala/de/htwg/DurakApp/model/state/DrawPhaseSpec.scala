package de.htwg.DurakApp.model.state

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model._

class DrawPhaseSpec extends AnyWordSpec with Matchers {
  "A DrawPhase" should {
    "handle drawing cards for players with less than 6 cards" in {
      val player1 = Player("P1", List(Card(Suit.Clubs, Rank.Six)))
      val player2 = Player(
        "P2",
        List(Card(Suit.Diamonds, Rank.Seven), Card(Suit.Diamonds, Rank.Eight))
      )
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
      )
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
      resultState.players(1).hand.length.shouldBe(6)
      resultState.deck.length.shouldBe(0)

      resultState.gamePhase.shouldBe(AttackPhase)
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
      resultState.deck.length.shouldBe(1)
      resultState.gamePhase.shouldBe(AttackPhase)
    }

    "skip players that are in passedPlayers when drawing" in {
      val attacker = Player("P0", List(Card(Suit.Clubs, Rank.Six)))
      val defender = Player("P1", List.fill(6)(Card(Suit.Diamonds, Rank.Seven)))
      val passive1 = Player(
        "P2",
        List(Card(Suit.Hearts, Rank.Six), Card(Suit.Hearts, Rank.Seven))
      )
      val passedPlayer = Player("P3", List.empty)

      val deck = List(
        Card(Suit.Spades, Rank.Ace),
        Card(Suit.Spades, Rank.King),
        Card(Suit.Spades, Rank.Queen),
        Card(Suit.Spades, Rank.Jack),
        Card(Suit.Spades, Rank.Ten),
        Card(Suit.Clubs, Rank.Nine),
        Card(Suit.Clubs, Rank.Eight),
        Card(Suit.Clubs, Rank.Seven),
        Card(Suit.Clubs, Rank.Six)
      )

      val initialGameState = GameState(
        players = List(attacker, defender, passive1, passedPlayer),
        deck = deck,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = Card(Suit.Clubs, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = DrawPhase,
        roundWinner = Some(0),
        passedPlayers = Set(3)
      )

      val result = DrawPhase.handle(initialGameState)

      result.players(0).hand.length.shouldBe(6)
      result.players(2).hand.length.shouldBe(6)

      result.players(1).hand.length.shouldBe(6)

      result.players(3).hand.length.shouldBe(0)

      result.deck.length.shouldBe(0)

      result.gamePhase shouldBe AttackPhase
    }
  }
}
