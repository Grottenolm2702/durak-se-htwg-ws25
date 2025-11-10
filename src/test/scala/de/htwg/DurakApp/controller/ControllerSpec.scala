package de.htwg.DurakApp.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model._
import scala.util.Random

class ControllerSpec extends AnyWordSpec with Matchers {

  "A Controller" should {

    "initialize a game correctly" in {
      val controller = new Controller()
      given Random = new Random(0)
      val playerNames = List("Player 1", "Player 2")
      val gameState = controller.initGame(36, playerNames)

      gameState.playerList.length shouldBe 2
      gameState.playerList.head.name shouldBe "Player 1"
      gameState.deck.length shouldBe 24 // 36 - 2 * 6
      gameState.playerList.forall(_.hand.length == 6) shouldBe true
    }

    "handle small deck during initialization" in {
      val controller = new Controller()
      given Random = new Random(0)
      val playerNames = List("Player 1", "Player 2", "Player 3", "Player 4", "Player 5", "Player 6", "Player 7")
      val gameState = controller.initGame(36, playerNames)

      gameState.playerList.length shouldBe 7
      // 36 cards / 7 players = 5 cards each
      gameState.playerList.forall(_.hand.length == 5) shouldBe true
      gameState.deck.length shouldBe 1 // 36 - 7 * 5
    }

    "update finished players" in {
      val controller = new Controller()
      val player1 = Player("Player 1", List(), isDone = false)
      val player2 = Player("Player 2", List(Card(Suit.Clubs, Rank.Ace)), isDone = false)
      val gameState = GameState(List(player1, player2), Nil, Suit.Clubs)
      
      val updatedGame = controller.updateFinishedPlayers(gameState)
      updatedGame.playerList.find(_.name == "Player 1").get.isDone shouldBe true
      updatedGame.playerList.find(_.name == "Player 2").get.isDone shouldBe false
    }

    "handle game end" in {
      val controller = new Controller()
      val player1 = Player("Player 1", List(), isDone = true)
      val player2 = Player("Player 2", List(Card(Suit.Clubs, Rank.Ace)), isDone = false)
      val gameState = GameState(List(player1, player2), Nil, Suit.Clubs)

      controller.handleEnd(gameState)
      controller.status shouldBe "Player 2 ist der Durak!"
    }

    "handle game end with no loser (draw)" in {
      val controller = new Controller()
      val player1 = Player("Player 1", List(), isDone = true)
      val player2 = Player("Player 2", List(), isDone = true)
      val gameState = GameState(List(player1, player2), Nil, Suit.Clubs)

      controller.handleEnd(gameState)
      controller.status shouldBe "Alle fertig — Unentschieden!"
    }

    "attack phase" in {
      val controller = new Controller()
      val attacker = Player("Attacker", List(Card(Suit.Clubs, Rank.Seven), Card(Suit.Clubs, Rank.Eight)), false)
      val defender = Player("Defender", List(Card(Suit.Hearts, Rank.Ace)), false)
      val gameState = GameState(List(attacker, defender), Nil, Suit.Spades)
      
      // Mocking user input
      val inputs = List("0", "pass")
      val mockInput = new MockPlayerInput(inputs)

      val finalState = controller.attack(gameState, 0, mockInput)
      finalState.attackingCards.length shouldBe 1
      finalState.attackingCards.head.rank shouldBe Rank.Seven
      finalState.playerList.head.hand.length shouldBe 1
    }

    "should not allow passing when no cards are on the table" in {
      val controller = new Controller()
      val attacker = Player("Attacker", List(Card(Suit.Clubs, Rank.Seven)), false)
      val defender = Player("Defender", List(Card(Suit.Hearts, Rank.Ace)), false)
      val gameState = GameState(List(attacker, defender), Nil, Suit.Spades, attackingCards = Nil)

      val inputs = List("pass", "0", "dummy", "dummy", "pass") // First pass is invalid, second input is a valid card index, third and fourth are consumed by recursive calls due to invalid input, and the final pass exits the attackLoop
      val mockInput = new MockPlayerInput(inputs)

      controller.attack(gameState, 0, mockInput)
      // After the invalid "pass", the loop continues and then a valid attack is made.
      // The status will reflect the last successful action.
      controller.game.attackingCards.length shouldBe 1
      controller.game.attackingCards.head.rank shouldBe Rank.Seven
      controller.game.playerList.head.hand.length shouldBe 0
    }

    "defend phase - successful defense" in {
      val controller = new Controller()
      val attacker = Player("Attacker", List(), false)
      val defender = Player("Defender", List(Card(Suit.Clubs, Rank.Eight)), false)
      val attackingCard = Card(Suit.Clubs, Rank.Seven)
      val gameState = GameState(List(attacker, defender), Nil, Suit.Spades, attackingCards = List(attackingCard))

      val inputs = List("0")
      val mockInput = new MockPlayerInput(inputs)

      val (finalState, defenderTook) = controller.defend(gameState, 1, mockInput)
      defenderTook shouldBe false
      finalState.defendingCards.length shouldBe 0
      finalState.discardPile.length shouldBe 2
      finalState.attackingCards.isEmpty shouldBe true
    }

    "defend phase - taking cards" in {
      val controller = new Controller()
      val attacker = Player("Attacker", List(), false)
      val defender = Player("Defender", List(Card(Suit.Clubs, Rank.Six)), false)
      val attackingCard = Card(Suit.Clubs, Rank.Seven)
      val gameState = GameState(List(attacker, defender), Nil, Suit.Spades, attackingCards = List(attackingCard))

      val inputs = List("take")
      val mockInput = new MockPlayerInput(inputs)

      val (finalState, defenderTook) = controller.defend(gameState, 1, mockInput)
      defenderTook shouldBe true
      finalState.playerList(1).hand.length shouldBe 2 // own card + attacking card
      finalState.attackingCards.isEmpty shouldBe true
    }

    "draw phase" in {
      val controller = new Controller()
      val player1 = Player("P1", List.fill(4)(Card(Suit.Clubs, Rank.Ace)), false)
      val player2 = Player("P2", List.fill(5)(Card(Suit.Clubs, Rank.Ace)), false)
      val deck = List.fill(5)(Card(Suit.Spades, Rank.King))
      val gameState = GameState(List(player1, player2), deck, Suit.Hearts)

      val finalState = controller.draw(gameState, 0)
      finalState.playerList(0).hand.length shouldBe 6 // needs 2
      finalState.playerList(1).hand.length shouldBe 6 // needs 1
      finalState.deck.length shouldBe 2 // 5 - 2 - 1
    }

    "gameLoop should run a full game until a loser is determined" in {
      val controller = new Controller()
      given Random = new Random(0)

      // Setup a game state that will end quickly
      val attackerCard = Card(Suit.Clubs, Rank.Seven)
      val defenderCard = Card(Suit.Hearts, Rank.Ace)
      val attacker = Player("Alice", List(attackerCard))
      val defender = Player("Bob", List(defenderCard))
      val initialGameState = GameState(
        playerList = List(attacker, defender),
        deck = Nil, // Empty deck
        trump = Suit.Spades
      )

      // Inputs for the game loop:
      // 1. Alice (attacker) chooses to play her only card (index 0).
      // 2. Alice passes.
      // 3. Bob (defender) chooses to take the card.
      val inputs = List("0", "pass", "take")
      val mockInput = new MockPlayerInput(inputs)

      // Start the game loop
      controller.gameLoop(initialGameState, 0, mockInput)

      // Assert the final state
      controller.status shouldBe "Bob ist der Durak!"
      val finalGame = controller.game
      finalGame.playerList.find(_.name == "Alice").get.isDone shouldBe true
      finalGame.playerList.find(_.name == "Bob").get.isDone shouldBe false
      finalGame.playerList.find(_.name == "Bob").get.hand.length should be > 1
    }

    "gameLoop should skip a player that is done and select the next active player as attacker" in {
      val controller = new Controller()
      given Random = new Random(0)

      class TestObserver extends de.htwg.DurakApp.util.Observer {
        var messages: List[String] = Nil
        def update: Unit = {
          messages = controller.status :: messages
        }
      }
      val observer = new TestObserver
      controller.add(observer)


      // Setup a game state that will end quickly
      val attackerCard = Card(Suit.Clubs, Rank.Seven)
      val defenderCard = Card(Suit.Hearts, Rank.Ace)
      val player1 = Player("Alice", List(), isDone = true)
      val player2 = Player("Bob", List(attackerCard))
      val player3 = Player("Charlie", List(defenderCard))

      val initialGameState = GameState(
        playerList = List(player1, player2, player3),
        deck = Nil, // Empty deck
        trump = Suit.Spades
      )

      // Inputs for the game loop:
      // 1. Bob (attacker) chooses to play his only card (index 0).
      // 2. Bob passes.
      // 3. Charlie (defender) chooses to take the card.
      val inputs = List("0", "pass", "take")
      val mockInput = new MockPlayerInput(inputs)

      // Start the game loop with player 1 (Alice) as the initial attacker
      controller.gameLoop(initialGameState, 0, mockInput)

      // Assert that the observer was notified with the correct new round message
      observer.messages.reverse should contain ("Neue Runde — Angreifer: Bob, Verteidiger: Charlie")

      // Assert the final state
      controller.status shouldBe "Charlie ist der Durak!"
      val finalGame = controller.game
      finalGame.playerList.find(_.name == "Alice").get.isDone shouldBe true
      finalGame.playerList.find(_.name == "Bob").get.isDone shouldBe true
      finalGame.playerList.find(_.name == "Charlie").get.isDone shouldBe false
      finalGame.playerList.find(_.name == "Charlie").get.hand.length should be > 0
    }
  }
}

class MockPlayerInput(inputs: List[String]) extends PlayerInput {
  private var remainingInputs = inputs

  override def chooseAttackCard(attacker: Player, game: GameState): String = {
    val input = remainingInputs.head
    remainingInputs = remainingInputs.tail
    input
  }

  override def chooseDefenseCard(defender: Player, attackCard: Card, game: GameState): String = {
    val input = remainingInputs.head
    remainingInputs = remainingInputs.tail
    input
  }
}
