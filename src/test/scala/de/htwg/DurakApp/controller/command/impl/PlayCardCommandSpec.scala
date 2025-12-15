package de.htwg.DurakApp.controller.command.impl

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.ModelInterface.*
import de.htwg.DurakApp.model.ModelInterface.StateInterface.*
import de.htwg.DurakApp.controller.ControllerInterface.*

class PlayCardCommandSpec extends AnyWordSpec with Matchers {
  "A PlayCardCommand" should {
    val player1Card1 = Card(Suit.Clubs, Rank.Six)
    val player1Card2 = Card(Suit.Clubs, Rank.Seven)
    val player2Card = Card(Suit.Hearts, Rank.Ace)

    val player1ForAttack = Player("P1", List(player1Card1, player1Card2))
    val player2ForAttack = Player("P2", List(player2Card))

    val initialGameStateAttack = GameState(
      players = List(player1ForAttack, player2ForAttack),
      deck = List.empty,
      table = Map.empty,
      discardPile = List.empty,
      trumpCard = Card(Suit.Diamonds, Rank.Ace),
      attackerIndex = 0,
      defenderIndex = 1,
      gamePhase = AttackPhase
    )

    val attackCardOnTable = Card(Suit.Spades, Rank.Eight)
    val defendingCard = Card(Suit.Spades, Rank.Nine)
    val player1ForDefense = Player("P1", List.empty)
    val player2ForDefense = Player("P2", List(defendingCard))
    val initialGameStateDefense = GameState(
      players = List(player1ForDefense, player2ForDefense),
      deck = List.empty,
      table = Map(attackCardOnTable -> None),
      discardPile = List.empty,
      trumpCard = Card(Suit.Diamonds, Rank.Ace),
      attackerIndex = 0,
      defenderIndex = 1,
      gamePhase = DefensePhase
    )

    "execute correctly when playing a card in AttackPhase" in {
      val command = PlayCardCommand(player1Card1)
      val resultState = command.execute(initialGameStateAttack)

      resultState.players(0).hand should not contain player1Card1
      resultState.table.keys should contain(player1Card1)
      resultState.gamePhase shouldBe DefensePhase
      resultState.lastEvent.get shouldBe a[GameEvent.Attack]
    }

    "execute correctly when playing a card in DefensePhase" in {
      val gameState = initialGameStateDefense.copy(
        gamePhase = DefensePhase,
        table = Map(attackCardOnTable -> None),
        players = List(player1ForDefense, player2ForDefense)
      )
      val command = PlayCardCommand(defendingCard)
      val resultState = command.execute(gameState)

      resultState.players(1).hand should not contain defendingCard
      resultState.table(attackCardOnTable) should contain(defendingCard)
      resultState.gamePhase shouldBe AttackPhase
      resultState.lastEvent.get shouldBe a[GameEvent.Defend]
    }

    "return InvalidMove when player plays a card not in hand" in {
      val wrongCard = Card(Suit.Hearts, Rank.King)
      val command = PlayCardCommand(wrongCard)
      val resultState = command.execute(initialGameStateAttack)
      resultState.lastEvent.get shouldBe GameEvent.InvalidMove
      resultState shouldBe initialGameStateAttack.copy(lastEvent =
        Some(GameEvent.InvalidMove)
      )
    }
  }
}
