package de.htwg.DurakApp.controller.command.impl
import de.htwg.DurakApp.testutil._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Player, Suit, Rank}
import de.htwg.DurakApp.model.state._
import de.htwg.DurakApp.model.state.impl._

class PlayCardCommandSpec extends AnyWordSpec with Matchers {
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
  val gameStateBuilderFactory =
    new de.htwg.DurakApp.model.builder.impl.GameStateBuilderFactoryImpl(
      gamePhases
    )
  "A PlayCardCommand" should {
    val player1Card1 = Card(Suit.Clubs, Rank.Six)
    val player1Card2 = Card(Suit.Clubs, Rank.Seven)
    val player2Card = Card(Suit.Hearts, Rank.Ace)
    val player1ForAttack = Player("P1", List(player1Card1, player1Card2))
    val player2ForAttack = Player("P2", List(player2Card))
    val initialGameStateAttack = TestHelper.GameState(
      players = List(player1ForAttack, player2ForAttack),
      deck = List.empty,
      table = Map.empty,
      discardPile = List.empty,
      trumpCard = Card(Suit.Hearts, Rank.Ace, true),
      mainAttackerIndex = 0,
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
    val attackCardOnTable = Card(Suit.Spades, Rank.Eight)
    val defendingCard = Card(Suit.Spades, Rank.Nine)
    val player1ForDefense = Player("P1", List.empty)
    val player2ForDefense = Player("P2", List(defendingCard))
    val initialGameStateDefense = TestHelper.GameState(
      players = List(player1ForDefense, player2ForDefense),
      deck = List.empty,
      table = Map(attackCardOnTable -> None),
      discardPile = List.empty,
      trumpCard = Card(Suit.Clubs, Rank.Ace, true),
      mainAttackerIndex = 0,
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
      val wrongCard = Card(Suit.Hearts, Rank.King)
      val command = PlayCardCommand(wrongCard, gamePhases)
      val resultState = command.execute(initialGameStateAttack)
      resultState.lastEvent.get shouldBe GameEvent.InvalidMove
      resultState shouldBe initialGameStateAttack.copy(lastEvent =
        Some(GameEvent.InvalidMove)
      )
    }
    "use attackerIndex when currentAttackerIndex is None in attack phase" in {
      val gameState = initialGameStateAttack.copy(
        currentAttackerIndex = None
      )
      val command = PlayCardCommand(player1Card1, gamePhases)
      val resultState = command.execute(gameState)
      resultState.players(0).hand should not contain player1Card1
      resultState.table.keys should contain(player1Card1)
      resultState.gamePhase shouldBe gamePhases.defensePhase
    }
    "use currentAttackerIndex when it is defined in attack phase" in {
      val player3Card = Card(Suit.Diamonds, Rank.Ten)
      val player3 = Player("P3", List(player3Card))
      val gameState = TestHelper.GameState(
        players = List(player1ForAttack, player2ForAttack, player3),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = Card(Suit.Hearts, Rank.Ace, true),
        mainAttackerIndex = 0,
        defenderIndex = 1,
        gamePhase = gamePhases.attackPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = Some(2),
        lastAttackerIndex = None
      )
      val command = PlayCardCommand(player3Card, gamePhases)
      val resultState = command.execute(gameState)
      resultState.players(2).hand should not contain player3Card
      resultState.table.keys should contain(player3Card)
    }
    "use defenderIndex in defense phase regardless of currentAttackerIndex" in {
      val gameState = initialGameStateDefense.copy(
        currentAttackerIndex = Some(0)
      )
      val command = PlayCardCommand(defendingCard, gamePhases)
      val resultState = command.execute(gameState)
      resultState.players(1).hand should not contain defendingCard
      resultState.table(attackCardOnTable) should contain(defendingCard)
    }
    "explicitly use gameState.mainAttackerIndex when currentAttackerIndex is None" in {
      val player1 = Player("P1", List(player1Card1))
      val player2 = Player("P2", List(player2Card))
      val gameState = TestHelper.GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = Card(Suit.Hearts, Rank.Ace, true),
        mainAttackerIndex = 0,
        defenderIndex = 1,
        gamePhase = gamePhases.attackPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      gameState.currentAttackerIndex shouldBe None
      gameState.mainAttackerIndex shouldBe 0
      val command = PlayCardCommand(player1Card1, gamePhases)
      val resultState = command.execute(gameState)
      resultState.players(0).hand should not contain player1Card1
    }
  }
}
