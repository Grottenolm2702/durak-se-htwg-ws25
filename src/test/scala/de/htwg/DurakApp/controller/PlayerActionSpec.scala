package de.htwg.DurakApp.controller

import de.htwg.DurakApp.testutil._

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.controller._
import de.htwg.DurakApp.model.{Card, Player, GameState, Suit, Rank}
import de.htwg.DurakApp.model.state._

class PlayerActionSpec extends AnyWordSpec with Matchers {
  "PlayerAction PlayCardAction" should {
    "be created with a Card parameter" in {
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace)
      val action = PlayCardAction(card)

      action shouldBe a[PlayCardAction]
      action shouldBe a[PlayerAction]
    }

    "work with different cards" in {
      val action1 = PlayCardAction(TestHelper.Card(Suit.Spades, Rank.Six))
      val action2 = PlayCardAction(TestHelper.Card(Suit.Diamonds, Rank.King))

      action1 shouldBe a[PlayCardAction]
      action2 shouldBe a[PlayCardAction]
    }
  }

  "PlayerAction PassAction" should {
    "be a singleton object" in {
      val action1: PlayerAction = PassAction
      val action2: PlayerAction = PassAction

      action1 shouldBe PassAction
      action2 shouldBe PassAction
      action1 shouldBe action2
    }
  }

  "PlayerAction TakeCardsAction" should {
    "be a singleton object" in {
      val action1: PlayerAction = TakeCardsAction
      val action2: PlayerAction = TakeCardsAction

      action1 shouldBe TakeCardsAction
      action2 shouldBe TakeCardsAction
      action1 shouldBe action2
    }
  }

  "PlayerAction SetPlayerCountAction" should {
    "be created with a count parameter" in {
      val action = SetPlayerCountAction(3)

      action shouldBe a[SetPlayerCountAction]
      action shouldBe a[PlayerAction]
    }
  }

  "PlayerAction AddPlayerNameAction" should {
    "be created with a name parameter" in {
      val action = AddPlayerNameAction("Alice")

      action shouldBe a[AddPlayerNameAction]
      action shouldBe a[PlayerAction]
    }
  }

  "PlayerAction UndoAction and RedoAction" should {
    "be singleton objects" in {
      UndoAction shouldBe a[PlayerAction]
      RedoAction shouldBe a[PlayerAction]
      UndoAction should not be RedoAction
    }
  }
}
