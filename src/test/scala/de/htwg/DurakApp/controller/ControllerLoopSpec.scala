package de.htwg.DurakApp.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model._
import scala.util.Random

class ControllerLoopSpec extends AnyWordSpec with Matchers {

  "A Controller" should {
    "attack phase with various invalid inputs" in {
      val controller = new Controller()
      val attacker = Player("Attacker", List(Card(Suit.Clubs, Rank.Seven), Card(Suit.Clubs, Rank.Eight)), false)
      val defender = Player("Defender", List(Card(Suit.Hearts, Rank.Ace)), false)
      val gameState = GameState(List(attacker, defender), Nil, Suit.Spades)
      
      val inputs = List("pass", "foo", "10", "0", "pass")
      val mockInput = new MockPlayerInput(inputs)

      val finalState = controller.attack(gameState, 0, mockInput)
      finalState.attackingCards.length shouldBe 1
      finalState.playerList.head.hand.length shouldBe 1
      controller.status shouldBe "Attack phase finished."
    }

    "defend phase when no attack is happening" in {
      val controller = new Controller()
      val player = Player("Player", List(), false)
      val gameState = GameState(List(player), Nil, Suit.Clubs, attackingCards = Nil)
      val mockInput = new MockPlayerInput(List())

      val (finalState, defenderTook) = controller.defend(gameState, 0, mockInput)
      
      finalState shouldBe gameState
      defenderTook shouldBe false
    }

    "defend phase with invalid card and invalid input" in {
      val controller = new Controller()
      val defender = Player("Defender", List(Card(Suit.Clubs, Rank.Six)), false) // Can't beat Seven
      val attackingCard = Card(Suit.Clubs, Rank.Seven)
      val gameState = GameState(List(Player("Attacker", Nil), defender), Nil, Suit.Spades, attackingCards = List(attackingCard))

      val inputs = List("foo", "0", "take")
      val mockInput = new MockPlayerInput(inputs)

      val (finalState, defenderTook) = controller.defend(gameState, 1, mockInput)
      defenderTook shouldBe true
      finalState.playerList(1).hand.length shouldBe 2
      controller.status shouldBe "Defender nimmt die Karten."
    }

    "draw phase should not change anything if hands are full" in {
      val controller = new Controller()
      val player1 = Player("P1", List.fill(6)(Card(Suit.Clubs, Rank.Ace)), false)
      val player2 = Player("P2", List.fill(7)(Card(Suit.Clubs, Rank.Ace)), false)
      val deck = List.fill(5)(Card(Suit.Spades, Rank.King))
      val gameState = GameState(List(player1, player2), deck, Suit.Hearts)

      val finalState = controller.draw(gameState, 0)
      finalState.playerList(0).hand.length shouldBe 6
      finalState.playerList(1).hand.length shouldBe 7
      finalState.deck.length shouldBe 5
    }
  }
}
