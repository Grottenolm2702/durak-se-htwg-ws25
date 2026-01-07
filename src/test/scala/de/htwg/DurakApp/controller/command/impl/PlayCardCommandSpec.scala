package de.htwg.DurakApp.controller.command.impl

import de.htwg.DurakApp.testutil._

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Player, Suit, Rank}
import de.htwg.DurakApp.model.state._
import de.htwg.DurakApp.model.state.impl._
import de.htwg.DurakApp.model.impl._

class PlayCardCommandSpec extends AnyWordSpec with Matchers {
  val cardFactory = new CardFactoryImpl
  val playerFactory = new PlayerFactoryImpl
  
  val gamePhases = new GamePhasesImpl(
    SetupPhaseImpl,
    AskPlayerCountPhaseImpl,
    AskPlayerNamesPhaseImpl,
    AskDeckSizePhaseImpl,
    AskPlayAgainPhaseImpl,
    GameStartPhaseImpl,
    AttackPhaseImpl,
    DefensePhaseImpl,
    DrawPhaseImpl,
    RoundPhaseImpl,
    EndPhaseImpl
  )
  
  val gameStateFactory = new GameStateFactoryImpl(gamePhases, cardFactory, playerFactory)
  
  "A PlayCardCommand" should {
    val player1Card1 = cardFactory(Suit.Clubs, Rank.Six)
    val player1Card2 = cardFactory(Suit.Clubs, Rank.Seven)
    val player2Card = cardFactory(Suit.Hearts, Rank.Ace)

    val player1ForAttack = playerFactory("P1", List(player1Card1, player1Card2))
    val player2ForAttack = playerFactory("P2", List(player2Card))

    val initialGameStateAttack = gameStateFactory(
      players = List(player1ForAttack, player2ForAttack),
      deck = List.empty,
      table = Map.empty,
      discardPile = List.empty,
      trumpCard = cardFactory(Suit.Hearts, Rank.Ace, true),
      attackerIndex = 0,
      defenderIndex = 1,
      gamePhase = gamePhases.attackPhase,
      lastEvent = None,
      passedPlayers = Set.empty,
      roundWinner = None,
      setupPlayerCount = None,
      setupPlayerNames = List.empty,
      setupDeckSize = None,
      currentAttackerIndex = Some(0),
      lastAttackerIndex = None
    )

    val attackCardOnTable = cardFactory(Suit.Spades, Rank.Eight)
    val defendingCard = cardFactory(Suit.Spades, Rank.Nine)
    val player1ForDefense = playerFactory("P1", List.empty)
    val player2ForDefense = playerFactory("P2", List(defendingCard))
    val initialGameStateDefense = gameStateFactory(
      players = List(player1ForDefense, player2ForDefense),
      deck = List.empty,
      table = Map(attackCardOnTable -> None),
      discardPile = List.empty,
      trumpCard = cardFactory(Suit.Clubs, Rank.Ace, true),
      attackerIndex = 0,
      defenderIndex = 1,
      gamePhase = gamePhases.defensePhase,
      lastEvent = None,
      passedPlayers = Set.empty,
      roundWinner = None,
      setupPlayerCount = None,
      setupPlayerNames = List.empty,
      setupDeckSize = None,
      currentAttackerIndex = None,
      lastAttackerIndex = None
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
      val wrongCard = cardFactory(Suit.Hearts, Rank.King)
      val command = PlayCardCommand(wrongCard, gamePhases)
      val resultState = command.execute(initialGameStateAttack)
      resultState.lastEvent.get shouldBe GameEvent.InvalidMove
      resultState shouldBe initialGameStateAttack.copy(lastEvent =
        Some(GameEvent.InvalidMove)
      )
    }
  }
}
