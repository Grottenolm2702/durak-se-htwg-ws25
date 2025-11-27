package de.htwg.DurakApp.model.state

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model._

class EndPhaseSpec extends AnyWordSpec with Matchers {
  "An EndPhase" should {
    "handle a game where there is a clear loser" in {
      val winner = Player("Winner", List.empty, isDone = true)
      val loser = Player("Loser", List(Card(Suit.Clubs, Rank.Six)), isDone = false)
      val initialGameState = GameState(
        players = List(winner, loser),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = Card(Suit.Diamonds, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = EndPhase // Already in EndPhase
      )

      val resultState = EndPhase.handle(initialGameState)

      resultState.lastEvent.get.should(be (a[GameEvent.GameOver]))
      resultState.lastEvent.get.asInstanceOf[GameEvent.GameOver].winner.shouldBe(winner)
      resultState.lastEvent.get.asInstanceOf[GameEvent.GameOver].loser.get.shouldBe(loser)
      resultState.gamePhase.shouldBe(EndPhase) // Remains in EndPhase
    }

    "handle a game with no clear loser (draw)" in {
      val player1 = Player("P1", List.empty, isDone = true)
      val player2 = Player("P2", List.empty, isDone = true)
      val initialGameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = Card(Suit.Diamonds, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = EndPhase
      )

      val resultState = EndPhase.handle(initialGameState)

      resultState.lastEvent.get.should(be (a[GameEvent.GameOver]))
      resultState.lastEvent.get.asInstanceOf[GameEvent.GameOver].loser.shouldBe(None)
      resultState.gamePhase.shouldBe(EndPhase)
    }
  }
}
