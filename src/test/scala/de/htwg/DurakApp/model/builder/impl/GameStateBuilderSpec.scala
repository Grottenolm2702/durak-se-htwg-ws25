package de.htwg.DurakApp.model.builder.impl

import de.htwg.DurakApp.testutil.TestHelpers._
import de.htwg.DurakApp.testutil.{TestGamePhases, TestGamePhasesInstance}
import de.htwg.DurakApp.testutil.TestFactories

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Card, Player, Suit, Rank}

class GameStateBuilderSpec extends AnyWordSpec with Matchers {

  def createBuilder() = GameStateBuilder(TestFactories.gameStateFactory, TestFactories.cardFactory, TestGamePhasesInstance)

  "A GameStateBuilder" should {
    "create a default GameState correctly" in {
      val defaultGameState = createBuilder().build()

      defaultGameState.players shouldBe empty
      defaultGameState.deck shouldBe empty
      defaultGameState.table shouldBe empty
      defaultGameState.discardPile shouldBe empty
      defaultGameState.trumpCard shouldBe Card(Suit.Hearts, Rank.Six)
      defaultGameState.attackerIndex shouldBe 0
      defaultGameState.defenderIndex shouldBe 1
      defaultGameState.gamePhase shouldBe TestGamePhases.setupPhase
      defaultGameState.lastEvent shouldBe None
      defaultGameState.passedPlayers shouldBe empty
      defaultGameState.roundWinner shouldBe None
    }

    "create a GameState with custom players" in {
      val player1 = Player("Player1", List.empty)
      val player2 = Player("Player2", List.empty)
      val customGameState = createBuilder()
        .withPlayers(List(player1, player2))
        .build()

      customGameState.players should contain theSameElementsAs List(
        player1,
        player2
      )
    }

    "create a GameState with a custom trump card" in {
      val customTrump = Card(Suit.Spades, Rank.King)
      val customGameState = createBuilder()
        .withTrumpCard(customTrump)
        .build()

      customGameState.trumpCard shouldBe customTrump
    }

    "create a GameState with custom game phase" in {
      val customGameState = createBuilder()
        .withGamePhase(TestGamePhases.attackPhase)
        .build()

      customGameState.gamePhase shouldBe TestGamePhases.attackPhase
    }

    "create a complex GameState with multiple custom parameters" in {
      val player1 = Player("Alice", List(Card(Suit.Clubs, Rank.Seven)))
      val player2 = Player("Bob", List(Card(Suit.Diamonds, Rank.Eight)))
      val deck = List(Card(Suit.Hearts, Rank.Nine))
      val table =
        Map(Card(Suit.Spades, Rank.Ten) -> Some(Card(Suit.Spades, Rank.Jack)))
      val discardPile = List(Card(Suit.Clubs, Rank.Queen))
      val trump = Card(Suit.Diamonds, Rank.King)

      val complexGameState = createBuilder()
        .withPlayers(List(player1, player2))
        .withDeck(deck)
        .withTable(table)
        .withDiscardPile(discardPile)
        .withTrumpCard(trump)
        .withAttackerIndex(0)
        .withDefenderIndex(1)
        .withGamePhase(TestGamePhases.defensePhase)
        .build()

      complexGameState.players should contain theSameElementsAs List(
        player1,
        player2
      )
      complexGameState.deck shouldBe deck
      complexGameState.table shouldBe table
      complexGameState.discardPile shouldBe discardPile
      complexGameState.trumpCard shouldBe trump
      complexGameState.attackerIndex shouldBe 0
      complexGameState.defenderIndex shouldBe 1
      complexGameState.gamePhase shouldBe TestGamePhases.defensePhase
    }

    "ensure immutability of the builder instance" in {
      val initialBuilder = createBuilder()
      val builderWithPlayers =
        initialBuilder.withPlayers(List(Player("Test", List.empty)))

      initialBuilder.build().players shouldBe empty
      builderWithPlayers.build().players should not be empty
    }
  }
}
