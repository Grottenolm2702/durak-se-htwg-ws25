package de.htwg.DurakApp.controller.command

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model._
import de.htwg.DurakApp.model.state._

class TakeCardsCommandSpec extends AnyWordSpec with Matchers {
  "A TakeCardsCommand" should {
    val attackCard = Card(Suit.Clubs, Rank.Six)
    val defendCard = Card(Suit.Clubs, Rank.Seven)

    val player1 = Player("P1", List(Card(Suit.Hearts, Rank.Ace)))
    val player2 = Player("P2", List(Card(Suit.Diamonds, Rank.Ten))) // Defender

    val initialGameState = GameState(
      players = List(player1, player2),
      deck = List.empty,
      table = Map(attackCard -> Some(defendCard)), // Cards on table
      discardPile = List.empty,
      trumpCard = Card(Suit.Diamonds, Rank.Ace),
      attackerIndex = 0,
      defenderIndex = 1,
      gamePhase = DefensePhase // Defender is active
    )

    "execute correctly by making the defender take cards from the table" in {
      val command = TakeCardsCommand()
      val resultState = command.execute(initialGameState)

      resultState.players(1).hand.should(contain(attackCard))
      resultState.players(1).hand.should(contain(defendCard))
      resultState.table.should(be (empty))
      resultState.gamePhase shouldBe AttackPhase // Should transition to AttackPhase after DrawPhase
      resultState.lastEvent.get shouldBe GameEvent.RoundEnd(cleared = false)
    }
  }
}
