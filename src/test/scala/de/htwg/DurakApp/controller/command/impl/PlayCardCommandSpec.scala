package de.htwg.DurakApp.controller.command.impl

import de.htwg.DurakApp.testutil.TestHelpers._
import de.htwg.DurakApp.testutil.TestGamePhases

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Player, Suit, Rank}
import de.htwg.DurakApp.model.state._
import de.htwg.DurakApp.testutil.TestHelper

class PlayCardCommandSpec extends AnyWordSpec with Matchers {
  "A PlayCardCommand" should {
    val player1Card1 = Card(Suit.Clubs, Rank.Six)
    val player1Card2 = Card(Suit.Clubs, Rank.Seven)
    val player2Card = Card(Suit.Hearts, Rank.Ace)

    val player1ForAttack = Player("P1", List(player1Card1, player1Card2))
    val player2ForAttack = Player("P2", List(player2Card))

    val initialGameStateAttack = TestHelper.createTestGameState(
      players = List(player1ForAttack, player2ForAttack),
      gamePhase = TestGamePhases.attackPhase
    )

    val attackCardOnTable = Card(Suit.Spades, Rank.Eight)
    val defendingCard = Card(Suit.Spades, Rank.Nine)
    val player1ForDefense = Player("P1", List.empty)
    val player2ForDefense = Player("P2", List(defendingCard))
    val initialGameStateDefense = TestHelper.createTestGameState(
      players = List(player1ForDefense, player2ForDefense),
      table = Map(attackCardOnTable -> None),
      gamePhase = TestGamePhases.defensePhase
    )

    "execute correctly when playing a card in TestGamePhases.attackPhase" in {
      val command = PlayCardCommand(player1Card1)
      val resultState = command.execute(initialGameStateAttack)

      resultState.players(0).hand should not contain player1Card1
      resultState.table.keys should contain(player1Card1)
      resultState.gamePhase shouldBe TestGamePhases.defensePhase
      resultState.lastEvent.get shouldBe a[GameEvent.Attack]
    }

    "execute correctly when playing a card in TestGamePhases.defensePhase" in {
      val gameState = initialGameStateDefense.copy(
        gamePhase = TestGamePhases.defensePhase,
        table = Map(attackCardOnTable -> None),
        players = List(player1ForDefense, player2ForDefense)
      )
      val command = PlayCardCommand(defendingCard)
      val resultState = command.execute(gameState)

      resultState.players(1).hand should not contain defendingCard
      resultState.table(attackCardOnTable) should contain(defendingCard)
      resultState.gamePhase shouldBe TestGamePhases.attackPhase
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
