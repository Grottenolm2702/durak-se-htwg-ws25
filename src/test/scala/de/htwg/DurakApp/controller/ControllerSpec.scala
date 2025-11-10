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
