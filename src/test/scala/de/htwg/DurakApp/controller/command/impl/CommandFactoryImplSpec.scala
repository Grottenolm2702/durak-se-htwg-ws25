package de.htwg.DurakApp.controller.command.impl
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Suit, Rank}
import de.htwg.DurakApp.model.state.GameEvent
import de.htwg.DurakApp.controller._
import de.htwg.DurakApp.testutil._
class CommandFactoryImplSpec extends AnyWordSpec with Matchers {
  "CommandFactoryImpl" should {
    "create PlayCardCommand with card and gamePhases" in {
      val gamePhases = new StubGamePhasesImpl()
      val factory = new CommandFactoryImpl(gamePhases)
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace)
      val command = factory.playCard(card)
      command shouldBe a[PlayCardCommand]
    }
    "create PassCommand with gamePhases" in {
      val gamePhases = new StubGamePhasesImpl()
      val factory = new CommandFactoryImpl(gamePhases)
      val command = factory.pass()
      command shouldBe a[PassCommand]
    }
    "create TakeCardsCommand" in {
      val gamePhases = new StubGamePhasesImpl()
      val factory = new CommandFactoryImpl(gamePhases)
      val command = factory.takeCards()
      command shouldBe a[TakeCardsCommand]
    }
    "create PhaseChangeCommand" in {
      val gamePhases = new StubGamePhasesImpl()
      val factory = new CommandFactoryImpl(gamePhases)
      val command = factory.phaseChange()
      command shouldBe a[PhaseChangeCommand]
    }
    "create multiple commands independently" in {
      val gamePhases = new StubGamePhasesImpl()
      val factory = new CommandFactoryImpl(gamePhases)
      val card1 = TestHelper.Card(Suit.Hearts, Rank.Ace)
      val card2 = TestHelper.Card(Suit.Diamonds, Rank.King)
      val command1 = factory.playCard(card1)
      val command2 = factory.playCard(card2)
      val command3 = factory.pass()
      val command4 = factory.takeCards()
      command1 should not be command2
      command1 shouldBe a[PlayCardCommand]
      command2 shouldBe a[PlayCardCommand]
      command3 shouldBe a[PassCommand]
      command4 shouldBe a[TakeCardsCommand]
    }
    "createCommand returns Right for PlayCardAction" in {
      val gamePhases = new StubGamePhasesImpl()
      val factory = new CommandFactoryImpl(gamePhases)
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace)
      val gameState = TestHelper.GameState(
        players = List(),
        deck = List(),
        table = Map(),
        discardPile = List(),
        trumpCard = card,
        mainAttackerIndex = 0,
        defenderIndex = 1,
        gamePhase = StubGamePhases.attackPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      val result = factory.createCommand(PlayCardAction(card), gameState)
      result.isRight shouldBe true
      result.toOption.get shouldBe a[PlayCardCommand]
    }
    "createCommand returns Right for PassAction" in {
      val gamePhases = new StubGamePhasesImpl()
      val factory = new CommandFactoryImpl(gamePhases)
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace)
      val gameState = TestHelper.GameState(
        players = List(),
        deck = List(),
        table = Map(),
        discardPile = List(),
        trumpCard = card,
        mainAttackerIndex = 0,
        defenderIndex = 1,
        gamePhase = StubGamePhases.attackPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      val result = factory.createCommand(PassAction, gameState)
      result.isRight shouldBe true
      result.toOption.get shouldBe a[PassCommand]
    }
    "createCommand returns Right for TakeCardsAction" in {
      val gamePhases = new StubGamePhasesImpl()
      val factory = new CommandFactoryImpl(gamePhases)
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace)
      val gameState = TestHelper.GameState(
        players = List(),
        deck = List(),
        table = Map(),
        discardPile = List(),
        trumpCard = card,
        mainAttackerIndex = 0,
        defenderIndex = 1,
        gamePhase = StubGamePhases.attackPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      val result = factory.createCommand(TakeCardsAction, gameState)
      result.isRight shouldBe true
      result.toOption.get shouldBe a[TakeCardsCommand]
    }
    "createCommand returns Left for InvalidAction" in {
      val gamePhases = new StubGamePhasesImpl()
      val factory = new CommandFactoryImpl(gamePhases)
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace)
      val gameState = TestHelper.GameState(
        players = List(),
        deck = List(),
        table = Map(),
        discardPile = List(),
        trumpCard = card,
        mainAttackerIndex = 0,
        defenderIndex = 1,
        gamePhase = StubGamePhases.attackPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      val result = factory.createCommand(InvalidAction, gameState)
      result.isLeft shouldBe true
      result.swap.toOption.get shouldBe GameEvent.InvalidMove
    }
    "createCommand returns Left for UndoAction" in {
      val gamePhases = new StubGamePhasesImpl()
      val factory = new CommandFactoryImpl(gamePhases)
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace)
      val gameState = TestHelper.GameState(
        players = List(),
        deck = List(),
        table = Map(),
        discardPile = List(),
        trumpCard = card,
        mainAttackerIndex = 0,
        defenderIndex = 1,
        gamePhase = StubGamePhases.attackPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      val result = factory.createCommand(UndoAction, gameState)
      result.isLeft shouldBe true
      result.swap.toOption.get shouldBe GameEvent.InvalidMove
    }
    "createCommand returns Left for RedoAction" in {
      val gamePhases = new StubGamePhasesImpl()
      val factory = new CommandFactoryImpl(gamePhases)
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace)
      val gameState = TestHelper.GameState(
        players = List(),
        deck = List(),
        table = Map(),
        discardPile = List(),
        trumpCard = card,
        mainAttackerIndex = 0,
        defenderIndex = 1,
        gamePhase = StubGamePhases.attackPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      val result = factory.createCommand(RedoAction, gameState)
      result.isLeft shouldBe true
      result.swap.toOption.get shouldBe GameEvent.InvalidMove
    }
    "createCommand returns Left for SetPlayerCountAction" in {
      val gamePhases = new StubGamePhasesImpl()
      val factory = new CommandFactoryImpl(gamePhases)
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace)
      val gameState = TestHelper.GameState(
        players = List(),
        deck = List(),
        table = Map(),
        discardPile = List(),
        trumpCard = card,
        mainAttackerIndex = 0,
        defenderIndex = 1,
        gamePhase = StubGamePhases.attackPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      val result = factory.createCommand(SetPlayerCountAction(2), gameState)
      result.isLeft shouldBe true
    }
    "createCommand returns Left for AddPlayerNameAction" in {
      val gamePhases = new StubGamePhasesImpl()
      val factory = new CommandFactoryImpl(gamePhases)
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace)
      val gameState = TestHelper.GameState(
        players = List(),
        deck = List(),
        table = Map(),
        discardPile = List(),
        trumpCard = card,
        mainAttackerIndex = 0,
        defenderIndex = 1,
        gamePhase = StubGamePhases.attackPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      val result =
        factory.createCommand(AddPlayerNameAction("Alice"), gameState)
      result.isLeft shouldBe true
    }
    "createCommand returns Left for SetDeckSizeAction" in {
      val gamePhases = new StubGamePhasesImpl()
      val factory = new CommandFactoryImpl(gamePhases)
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace)
      val gameState = TestHelper.GameState(
        players = List(),
        deck = List(),
        table = Map(),
        discardPile = List(),
        trumpCard = card,
        mainAttackerIndex = 0,
        defenderIndex = 1,
        gamePhase = StubGamePhases.attackPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      val result = factory.createCommand(SetDeckSizeAction(20), gameState)
      result.isLeft shouldBe true
    }
    "createCommand returns Left for PlayAgainAction" in {
      val gamePhases = new StubGamePhasesImpl()
      val factory = new CommandFactoryImpl(gamePhases)
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace)
      val gameState = TestHelper.GameState(
        players = List(),
        deck = List(),
        table = Map(),
        discardPile = List(),
        trumpCard = card,
        mainAttackerIndex = 0,
        defenderIndex = 1,
        gamePhase = StubGamePhases.attackPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      val result = factory.createCommand(PlayAgainAction, gameState)
      result.isLeft shouldBe true
    }
    "createCommand returns Left for ExitGameAction" in {
      val gamePhases = new StubGamePhasesImpl()
      val factory = new CommandFactoryImpl(gamePhases)
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace)
      val gameState = TestHelper.GameState(
        players = List(),
        deck = List(),
        table = Map(),
        discardPile = List(),
        trumpCard = card,
        mainAttackerIndex = 0,
        defenderIndex = 1,
        gamePhase = StubGamePhases.attackPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = None,
        setupPlayerNames = List.empty,
        setupDeckSize = None,
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      val result = factory.createCommand(ExitGameAction, gameState)
      result.isLeft shouldBe true
    }
  }
}
