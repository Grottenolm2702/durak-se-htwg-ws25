package de.htwg.DurakApp.model.builder.impl
import de.htwg.DurakApp.testutil._
import de.htwg.DurakApp.testutil.{
  TestHelper,
  StubGamePhases,
  StubGamePhasesImpl
}
import de.htwg.DurakApp.testutil.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Player, Suit, Rank}
class GameStateBuilderSpec extends AnyWordSpec with Matchers {
  def createBuilder() = {
    GameStateBuilder(new StubGamePhasesImpl())
  }
  "A GameStateBuilder" should {
    "create a default GameState correctly" in {
      val defaultGameState = createBuilder().build().get
      defaultGameState.players shouldBe empty
      defaultGameState.deck shouldBe empty
      defaultGameState.table shouldBe empty
      defaultGameState.discardPile shouldBe empty
      defaultGameState.trumpCard shouldBe TestHelper.Card(Suit.Hearts, Rank.Six)
      defaultGameState.mainAttackerIndex shouldBe 0
      defaultGameState.defenderIndex shouldBe 1
      defaultGameState.gamePhase shouldBe StubGamePhases.setupPhase
      defaultGameState.lastEvent shouldBe None
      defaultGameState.passedPlayers shouldBe empty
      defaultGameState.roundWinner shouldBe None
    }
    "create a GameState with custom players" in {
      val player1 = TestHelper.Player("Player1", List.empty)
      val player2 = TestHelper.Player("Player2", List.empty)
      val customGameState = createBuilder()
        .withPlayers(List(player1, player2))
        .build()
        .get
      customGameState.players should contain theSameElementsAs List(
        player1,
        player2
      )
    }
    "create a GameState with a custom trump card" in {
      val customTrump = TestHelper.Card(Suit.Spades, Rank.King)
      val customGameState = createBuilder()
        .withTrumpCard(customTrump)
        .build()
        .get
      customGameState.trumpCard shouldBe customTrump
    }
    "create a GameState with custom game phase" in {
      val customGameState = createBuilder()
        .withGamePhase(StubGamePhases.attackPhase)
        .build()
        .get
      customGameState.gamePhase shouldBe StubGamePhases.attackPhase
    }
    "create a complex GameState with multiple custom parameters" in {
      val player1 = TestHelper.Player(
        "Alice",
        List(TestHelper.Card(Suit.Clubs, Rank.Seven))
      )
      val player2 = TestHelper.Player(
        "Bob",
        List(TestHelper.Card(Suit.Diamonds, Rank.Eight))
      )
      val deck = List(TestHelper.Card(Suit.Hearts, Rank.Nine))
      val table =
        Map(
          TestHelper.Card(Suit.Spades, Rank.Ten) -> Some(
            TestHelper.Card(Suit.Spades, Rank.Jack)
          )
        )
      val discardPile = List(TestHelper.Card(Suit.Clubs, Rank.Queen))
      val trump = TestHelper.Card(Suit.Diamonds, Rank.King)
      val complexGameState = createBuilder()
        .withPlayers(List(player1, player2))
        .withDeck(deck)
        .withTable(table)
        .withDiscardPile(discardPile)
        .withTrumpCard(trump)
        .withMainAttackerIndex(0)
        .withDefenderIndex(1)
        .withGamePhase(StubGamePhases.defensePhase)
        .build()
        .get
      complexGameState.players should contain theSameElementsAs List(
        player1,
        player2
      )
      complexGameState.deck shouldBe deck
      complexGameState.table shouldBe table
      complexGameState.discardPile shouldBe discardPile
      complexGameState.trumpCard shouldBe trump
      complexGameState.mainAttackerIndex shouldBe 0
      complexGameState.defenderIndex shouldBe 1
      complexGameState.gamePhase shouldBe StubGamePhases.defensePhase
    }
    "ensure immutability of the builder instance" in {
      val initialBuilder = createBuilder()
      val builderWithPlayers =
        initialBuilder.withPlayers(List(TestHelper.Player("Test", List.empty)))
      initialBuilder.build().get.players shouldBe empty
      builderWithPlayers.build().get.players should not be empty
    }
    "set currentAttackerIndex correctly" in {
      val builder = createBuilder()
      val gameState = builder
        .withCurrentAttackerIndex(Some(2))
        .build()
        .get
      gameState.currentAttackerIndex shouldBe Some(2)
    }
    "set currentAttackerIndex to None" in {
      val builder = createBuilder()
      val gameState = builder
        .withCurrentAttackerIndex(Some(1))
        .withCurrentAttackerIndex(None)
        .build()
        .get
      gameState.currentAttackerIndex shouldBe None
    }
    "set lastAttackerIndex correctly" in {
      val builder = createBuilder()
      val gameState = builder
        .withLastAttackerIndex(Some(3))
        .build()
        .get
      gameState.lastAttackerIndex shouldBe Some(3)
    }
    "set lastAttackerIndex to None" in {
      val builder = createBuilder()
      val gameState = builder
        .withLastAttackerIndex(Some(2))
        .withLastAttackerIndex(None)
        .build()
        .get
      gameState.lastAttackerIndex shouldBe None
    }
    "set both currentAttackerIndex and lastAttackerIndex" in {
      val builder = createBuilder()
      val gameState = builder
        .withCurrentAttackerIndex(Some(1))
        .withLastAttackerIndex(Some(0))
        .build()
        .get
      gameState.currentAttackerIndex shouldBe Some(1)
      gameState.lastAttackerIndex shouldBe Some(0)
    }
    "maintain builder immutability with currentAttackerIndex" in {
      val builder1 = createBuilder()
      val builder2 = builder1.withCurrentAttackerIndex(Some(5))
      val builder3 = builder1.withCurrentAttackerIndex(Some(10))

      builder2.build().get.currentAttackerIndex shouldBe Some(5)
      builder3.build().get.currentAttackerIndex shouldBe Some(10)
    }
    "maintain builder immutability with lastAttackerIndex" in {
      val builder1 = createBuilder()
      val builder2 = builder1.withLastAttackerIndex(Some(7))
      val builder3 = builder1.withLastAttackerIndex(Some(14))

      builder2.build().get.lastAttackerIndex shouldBe Some(7)
      builder3.build().get.lastAttackerIndex shouldBe Some(14)
    }
  }
}
