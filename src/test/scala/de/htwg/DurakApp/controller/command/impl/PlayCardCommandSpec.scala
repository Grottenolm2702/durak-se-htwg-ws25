package de.htwg.DurakApp.controller.command.impl

import de.htwg.DurakApp.testutil._

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Player, Suit, Rank}
import de.htwg.DurakApp.model.state._
import com.google.inject.Guice

class PlayCardCommandSpec extends AnyWordSpec with Matchers {
  val injector = Guice.createInjector(new de.htwg.DurakApp.DurakModule)
  val gamePhases = injector.getInstance(classOf[GamePhases])
  
  "A PlayCardCommand" should {
    val player1Card1 = TestHelper.Card(Suit.Clubs, Rank.Six)
    val player1Card2 = TestHelper.Card(Suit.Clubs, Rank.Seven)
    val player2Card = TestHelper.Card(Suit.Hearts, Rank.Ace)

    val player1ForAttack = TestHelper.Player("P1", List(player1Card1, player1Card2))
    val player2ForAttack = TestHelper.Player("P2", List(player2Card))

    val initialGameStateAttack = TestHelper.createTestGameState(
      players = List(player1ForAttack, player2ForAttack),
      gamePhase = gamePhases.attackPhase
    )

    val attackCardOnTable = TestHelper.Card(Suit.Spades, Rank.Eight)
    val defendingCard = TestHelper.Card(Suit.Spades, Rank.Nine)
    val player1ForDefense = TestHelper.Player("P1", List.empty)
    val player2ForDefense = TestHelper.Player("P2", List(defendingCard))
    val initialGameStateDefense = TestHelper.createTestGameState(
      players = List(player1ForDefense, player2ForDefense),
      table = Map(attackCardOnTable -> None),
      gamePhase = gamePhases.defensePhase
    )

    "execute correctly when playing a card in attack phase" in {
      val command = PlayCardCommand(player1Card1, gamePhases)
      val resultState = command.execute(initialGameStateAttack)

      resultState.players(0).hand should not contain player1Card1
      resultState.table.keys should contain(player1Card1)
      resultState.gamePhase shouldBe gamePhases.defensePhase
      resultState.lastEvent.get shouldBe a[GameEvent.Attack]
    }

    "execute correctly when playing a card in defense phase" in {
      val gameState = initialGameStateDefense.copy(
        gamePhase = gamePhases.defensePhase,
        table = Map(attackCardOnTable -> None),
        players = List(player1ForDefense, player2ForDefense)
      )
      val command = PlayCardCommand(defendingCard, gamePhases)
      val resultState = command.execute(gameState)

      resultState.players(1).hand should not contain defendingCard
      resultState.table(attackCardOnTable) should contain(defendingCard)
      resultState.gamePhase shouldBe gamePhases.attackPhase
      resultState.lastEvent.get shouldBe a[GameEvent.Defend]
    }

    "return InvalidMove when player plays a card not in hand" in {
      val wrongCard = TestHelper.Card(Suit.Hearts, Rank.King)
      val command = PlayCardCommand(wrongCard, gamePhases)
      val resultState = command.execute(initialGameStateAttack)
      resultState.lastEvent.get shouldBe GameEvent.InvalidMove
      resultState shouldBe initialGameStateAttack.copy(lastEvent =
        Some(GameEvent.InvalidMove)
      )
    }
  }
}
