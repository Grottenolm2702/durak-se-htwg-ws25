package de.htwg.DurakApp.controller.impl
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.impl._
import de.htwg.DurakApp.model.builder.impl.GameStateBuilderFactoryImpl
import de.htwg.DurakApp.model.state.impl._
class GameSetupImplSpec extends AnyWordSpec with Matchers {
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
  val gameStateFactory =
    new GameStateFactoryImpl(gamePhases, cardFactory, playerFactory)
  val gameStateBuilderFactory =
    new GameStateBuilderFactoryImpl(gameStateFactory, cardFactory, gamePhases)
  val gameSetup = new GameSetupImpl(
    gameStateFactory,
    playerFactory,
    cardFactory,
    gamePhases,
    gameStateBuilderFactory
  )
  "A GameSetupImpl" should {
    "setup game with valid parameters" in {
      val playerNames = List("Alice", "Bob")
      val deckSize = 36
      val result = gameSetup.setupGame(playerNames, deckSize)
      result shouldBe defined
      result.get.players.size shouldBe 2
      result.get.players.map(_.name) shouldBe playerNames
      result.get.players.forall(_.hand.size == 6) shouldBe true
    }
    "setup game with minimum players" in {
      val playerNames = List("Alice", "Bob")
      val deckSize = 36
      val result = gameSetup.setupGame(playerNames, deckSize)
      result shouldBe defined
    }
    "setup game with maximum standard deck" in {
      val playerNames = List("Alice", "Bob", "Charlie")
      val deckSize = 36
      val result = gameSetup.setupGame(playerNames, deckSize)
      result shouldBe defined
      result.get.deck.size should be > 0
    }
    "return None with insufficient players" in {
      val playerNames = List("Alice")
      val deckSize = 36
      val result = gameSetup.setupGame(playerNames, deckSize)
      result shouldBe None
    }
    "return None with empty player list" in {
      val playerNames = List.empty
      val deckSize = 36
      val result = gameSetup.setupGame(playerNames, deckSize)
      result shouldBe None
    }
    "return None with insufficient cards" in {
      val playerNames = List("Alice", "Bob", "Charlie", "Dave")
      val deckSize = 2
      val result = gameSetup.setupGame(playerNames, deckSize)
      result shouldBe None
    }
    "create deck with requested size" in {
      val playerNames = List("Alice", "Bob")
      val deckSize = 20
      val result = gameSetup.setupGame(playerNames, deckSize)
      result shouldBe defined
    }
    "handle large player count" in {
      val playerNames = List("Alice", "Bob", "Charlie", "Dave", "Eve", "Frank")
      val deckSize = 36
      val result = gameSetup.setupGame(playerNames, deckSize)
      result shouldBe defined
      result.get.players.size shouldBe 6
    }
    "shuffle deck randomly" in {
      val playerNames = List("Alice", "Bob")
      val deckSize = 36
      val result1 = gameSetup.setupGame(playerNames, deckSize)
      val result2 = gameSetup.setupGame(playerNames, deckSize)
      result1 shouldBe defined
      result2 shouldBe defined
    }
  }
}
