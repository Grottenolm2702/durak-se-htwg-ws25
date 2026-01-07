package de.htwg.DurakApp.model.impl
import de.htwg.DurakApp.testutil._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.{Player, Card, Suit, Rank}
class PlayerImplSpec extends AnyWordSpec with Matchers {
  "PlayerImpl" should {
    "be created through Player factory" in {
      val player =
        TestHelper.Player("Alice", List(TestHelper.Card(Suit.Hearts, Rank.Ace)))
      player.name shouldBe "Alice"
      player.hand should have size 1
    }
    "be created directly with PlayerImpl" in {
      val card = CardImpl(Suit.Hearts, Rank.Ace, isTrump = false)
      val player = PlayerImpl("Alice", List(card), isDone = false)
      player.name shouldBe "Alice"
      player.hand shouldBe List(card)
      player.isDone shouldBe false
    }
    "support copy with name change" in {
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace)
      val player = TestHelper.Player("Alice", List(card), isDone = true)
      val copied = player.copy(name = "Bob")
      copied.name shouldBe "Bob"
      copied.hand shouldBe List(card)
      copied.isDone shouldBe true
    }
    "support copy with hand change" in {
      val card1 = TestHelper.Card(Suit.Hearts, Rank.Ace)
      val card2 = TestHelper.Card(Suit.Spades, Rank.King)
      val player = TestHelper.Player("Alice", List(card1))
      val copied = player.copy(hand = List(card2))
      copied.name shouldBe "Alice"
      copied.hand shouldBe List(card2)
      copied.isDone shouldBe false
    }
    "support copy with isDone change" in {
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace)
      val player = TestHelper.Player("Alice", List(card), isDone = false)
      val copied = player.copy(isDone = true)
      copied.name shouldBe "Alice"
      copied.hand shouldBe List(card)
      copied.isDone shouldBe true
    }
    "support copy with all parameters changed" in {
      val card1 = TestHelper.Card(Suit.Hearts, Rank.Ace)
      val card2 = TestHelper.Card(Suit.Diamonds, Rank.Queen)
      val player = TestHelper.Player("Alice", List(card1), isDone = false)
      val copied =
        player.copy(name = "Charlie", hand = List(card2), isDone = true)
      copied.name shouldBe "Charlie"
      copied.hand shouldBe List(card2)
      copied.isDone shouldBe true
    }
    "support copy with no parameters" in {
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace)
      val player = TestHelper.Player("Alice", List(card), isDone = true)
      val copied = player.copy()
      copied.name shouldBe player.name
      copied.hand shouldBe player.hand
      copied.isDone shouldBe player.isDone
    }
    "support direct PlayerImpl copy" in {
      val card = CardImpl(Suit.Hearts, Rank.Ace, isTrump = false)
      val player = PlayerImpl("Alice", List(card), isDone = false)
      val copied = player.copy()
      copied shouldBe a[PlayerImpl]
      copied.name shouldBe "Alice"
      copied.hand shouldBe List(card)
      copied.isDone shouldBe false
    }
  }
}
