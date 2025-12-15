package de.htwg.DurakApp.model.state.impl

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.ModelInterface.*
import de.htwg.DurakApp.model.ModelInterface.StateInterface.*

class RoundPhaseImplSpec extends AnyWordSpec with Matchers {
  "A RoundPhase" should {
    "handle a round end by clearing the table and transitioning to AttackPhase when roundWinner is defined" in {
      val player1 = Player("P1", List(Card(Suit.Clubs, Rank.Six)))
      val player2 = Player("P2", List(Card(Suit.Hearts, Rank.Ace)))
      val initialGameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map(
          Card(Suit.Spades, Rank.Ten) -> Some(Card(Suit.Spades, Rank.Jack))
        ),
        discardPile = List.empty,
        trumpCard = Card(Suit.Diamonds, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = RoundPhase,
        roundWinner = Some(0)
      )

      val resultState = RoundPhase.handle(initialGameState)

      resultState.table.should(be(empty))
      resultState.discardPile.length shouldBe 2
      resultState.roundWinner shouldBe None
      resultState.gamePhase shouldBe AttackPhase
      resultState.lastEvent.get shouldBe a[GameEvent.RoundEnd]
      resultState.lastEvent.get
        .asInstanceOf[GameEvent.RoundEnd]
        .cleared shouldBe true
    }

    "handle a round end by moving table cards to discard pile and transitioning to AttackPhase when roundWinner is None" in {
      val player1 = Player("P1", List(Card(Suit.Clubs, Rank.Six)))
      val player2 = Player("P2", List(Card(Suit.Hearts, Rank.Ace)))
      val tableCards =
        Map(Card(Suit.Spades, Rank.Ten) -> Some(Card(Suit.Spades, Rank.Jack)))
      val initialGameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = tableCards,
        discardPile = List.empty,
        trumpCard = Card(Suit.Diamonds, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = RoundPhase,
        roundWinner = None
      )

      val resultState = RoundPhase.handle(initialGameState)

      resultState.table.should(be(empty))
      resultState.discardPile.length shouldBe 0
      resultState.roundWinner shouldBe None
      resultState.gamePhase shouldBe AttackPhase
      resultState.lastEvent.get shouldBe a[GameEvent.RoundEnd]
      resultState.lastEvent.get
        .asInstanceOf[GameEvent.RoundEnd]
        .cleared shouldBe false
    }

    "transition to EndPhase if only one active player remains and deck is empty" in {
      val player1 = Player("P1", List.empty, isDone = true)
      val player2 = Player("P2", List(Card(Suit.Clubs, Rank.Six)))
      val initialGameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = Card(Suit.Diamonds, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = RoundPhase
      )

      val resultState = RoundPhase.handle(initialGameState)
      resultState.gamePhase shouldBe AskPlayAgainPhase
      resultState.lastEvent.get shouldBe a[GameEvent.GameOver]
    }
  }
}
