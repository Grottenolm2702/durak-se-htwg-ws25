package de.htwg.DurakApp.model.builder

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.ModelInterface.*

class GameStateBuilderSpec extends AnyWordSpec with Matchers {

  "A GameStateBuilder" should {
    "create a default GameState correctly" in {
      val defaultGameState = GameStateBuilder().build()

      defaultGameState.players shouldBe empty
      defaultGameState.deck shouldBe empty
      defaultGameState.table shouldBe empty
      defaultGameState.discardPile shouldBe empty
      defaultGameState.trumpCard shouldBe Card(Suit.Hearts, Rank.Six)
      defaultGameState.attackerIndex shouldBe 0
      defaultGameState.defenderIndex shouldBe 1
      defaultGameState.gamePhase shouldBe SetupPhase
      defaultGameState.lastEvent shouldBe None
      defaultGameState.passedPlayers shouldBe empty
      defaultGameState.roundWinner shouldBe None
    }

    "create a GameState with custom players" in {
      val player1 = Player("Player1", List.empty)
      val player2 = Player("Player2", List.empty)
      val customGameState = GameStateBuilder()
        .withPlayers(List(player1, player2))
        .build()

      customGameState.players should contain theSameElementsAs List(
        player1,
        player2
      )
    }

    "create a GameState with a custom trump card" in {
      val customTrump = Card(Suit.Spades, Rank.King)
      val customGameState = GameStateBuilder()
        .withTrumpCard(customTrump)
        .build()

      customGameState.trumpCard shouldBe customTrump
    }

    "create a GameState with custom game phase" in {
      val customGameState = GameStateBuilder()
        .withGamePhase(AttackPhase)
        .build()

      customGameState.gamePhase shouldBe AttackPhase
    }

    "create a complex GameState with multiple custom parameters" in {
      val player1 = Player("Alice", List(Card(Suit.Clubs, Rank.Seven)))
      val player2 = Player("Bob", List(Card(Suit.Diamonds, Rank.Eight)))
      val deck = List(Card(Suit.Hearts, Rank.Nine))
      val table =
        Map(Card(Suit.Spades, Rank.Ten) -> Some(Card(Suit.Spades, Rank.Jack)))
      val discardPile = List(Card(Suit.Clubs, Rank.Queen))
      val trump = Card(Suit.Diamonds, Rank.King)

      val complexGameState = GameStateBuilder()
        .withPlayers(List(player1, player2))
        .withDeck(deck)
        .withTable(table)
        .withDiscardPile(discardPile)
        .withTrumpCard(trump)
        .withAttackerIndex(0)
        .withDefenderIndex(1)
        .withGamePhase(DefensePhase)
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
      complexGameState.gamePhase shouldBe DefensePhase
    }

    "ensure immutability of the builder instance" in {
      val initialBuilder = GameStateBuilder()
      val builderWithPlayers =
        initialBuilder.withPlayers(List(Player("Test", List.empty)))

      initialBuilder.players shouldBe empty
      builderWithPlayers.players should not be empty
    }
  }
}
