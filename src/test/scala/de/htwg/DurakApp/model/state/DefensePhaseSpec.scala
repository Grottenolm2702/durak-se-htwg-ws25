package de.htwg.DurakApp.model.state

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model._

class DefensePhaseSpec extends AnyWordSpec with Matchers {
  "A DefensePhase" should {
    "handle the game state without changing it by default" in {
      val player1 = Player("P1", List(Card(Suit.Clubs, Rank.Six)))
      val player2 = Player("P2", List(Card(Suit.Diamonds, Rank.Seven)))
      val initialGameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map(Card(Suit.Spades, Rank.Ace) -> None),
        discardPile = List.empty,
        trumpCard = Card(Suit.Hearts, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = DefensePhase
      )
      val resultState = DefensePhase.handle(initialGameState)
      resultState.shouldBe(initialGameState)
    }

    "allow a defender to play a card to beat an attack" in {
      val attackCard = Card(Suit.Clubs, Rank.Six)
      val defenseCard = Card(Suit.Clubs, Rank.Seven)
      val player1 = Player("P1", List(Card(Suit.Clubs, Rank.Six)))
      val player2 = Player("P2", List(defenseCard)) // Defender has beating card
      val initialGameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map(attackCard -> None), // Attack card on table
        discardPile = List.empty,
        trumpCard = Card(Suit.Diamonds, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = DefensePhase
      )

      val resultState = DefensePhase.playCard(defenseCard, 1, initialGameState)

      resultState.players(1).hand.shouldNot(contain(defenseCard))
      resultState.table.get(attackCard).flatten.should(contain(defenseCard))
      resultState.gamePhase.shouldBe(AttackPhase)
      resultState.lastEvent.get.should(be (a[GameEvent.Defend]))
    }

    "not allow a defender to play a non-beating card" in {
      val attackCard = Card(Suit.Clubs, Rank.Six)
      val defenseCard = Card(Suit.Clubs, Rank.Six) // Non-beating card
      val player1 = Player("P1", List(Card(Suit.Clubs, Rank.Eight)))
      val player2 = Player("P2", List(defenseCard))
      val initialGameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map(attackCard -> None),
        discardPile = List.empty,
        trumpCard = Card(Suit.Diamonds, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = DefensePhase
      )

      val resultState = DefensePhase.playCard(defenseCard, 1, initialGameState)
      resultState.shouldBe(initialGameState.copy(lastEvent = Some(GameEvent.InvalidMove)))
    }

    "allow a defender to take cards" in {
      val attackCard = Card(Suit.Clubs, Rank.Six)
      val player1 = Player("P1", List(Card(Suit.Clubs, Rank.Six)))
      val player2 = Player("P2", List(Card(Suit.Hearts, Rank.Seven))) // Defender has some cards
      val initialGameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map(attackCard -> None),
        discardPile = List.empty,
        trumpCard = Card(Suit.Diamonds, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = DefensePhase
      )

      val resultState = DefensePhase.takeCards(1, initialGameState) // Defender takes
      resultState.players(1).hand.should(contain(attackCard))
      resultState.table.should(be (empty))
      resultState.gamePhase.shouldBe(AttackPhase) // RoundPhase -> AttackPhase after handle
      resultState.lastEvent.get.shouldBe(GameEvent.RoundEnd(cleared = false))
    }

    "not allow an attacker to take cards" in {
      val attackCard = Card(Suit.Clubs, Rank.Six)
      val player1 = Player("P1", List(Card(Suit.Clubs, Rank.Six)))
      val player2 = Player("P2", List(Card(Suit.Hearts, Rank.Seven)))
      val initialGameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map(attackCard -> None),
        discardPile = List.empty,
        trumpCard = Card(Suit.Diamonds, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = DefensePhase
      )

      val resultState = DefensePhase.takeCards(0, initialGameState) // Attacker tries to take
      resultState.shouldBe(initialGameState.copy(lastEvent = Some(GameEvent.NotYourTurn)))
    }
  }
}
