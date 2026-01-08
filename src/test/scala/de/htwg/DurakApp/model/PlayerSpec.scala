package de.htwg.DurakApp.model
import de.htwg.DurakApp.testutil._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
class PlayerSpec extends AnyWordSpec with Matchers {
  "A Player" should {
    "store its name and hand correctly" in {
      val hand = List(
        TestHelper.Card(Suit.Hearts, Rank.Six, isTrump = false),
        TestHelper.Card(Suit.Clubs, Rank.Jack, isTrump = true)
      )
      val player = TestHelper.Player("Lucifer", hand, false)
      player.name.shouldBe("Lucifer")
      player.hand.shouldBe(hand)
    }
    "initialize hand and isDone correctly with minimal parameters" in {
      val player = TestHelper.Player("Michael")
      player.name.shouldBe("Michael")
      player.hand.shouldBe(List())
      player.isDone.shouldBe(false)
    }
    "support copy with default isDone" in {
      val hand = List(TestHelper.Card(Suit.Hearts, Rank.Six))
      val player = TestHelper.Player("Gabriel", hand, isDone = true)
      val copied = player.copy(name = "Raphael")
      copied.name shouldBe "Raphael"
      copied.hand shouldBe hand
      copied.isDone shouldBe true
    }
    "use default parameter for hand when creating with only name" in {
      val player = TestHelper.Player("Solo")
      player.name shouldBe "Solo"
      player.hand shouldBe List.empty
      player.isDone shouldBe false
    }
    "use default parameter for isDone when creating with name and hand" in {
      val hand = List(TestHelper.Card(Suit.Hearts, Rank.Six))
      val player = TestHelper.Player("Hero", hand)
      player.name shouldBe "Hero"
      player.hand shouldBe hand
      player.isDone shouldBe false
    }
  }
  "Player copy method" should {
    val card1 = TestHelper.Card(Suit.Hearts, Rank.Ace)
    val card2 = TestHelper.Card(Suit.Diamonds, Rank.King)
    val originalPlayer = TestHelper.Player("Alice", List(card1), isDone = true)
    "copy with only name changed" in {
      val copied = originalPlayer.copy(name = "Bob")
      copied.name shouldBe "Bob"
      copied.hand shouldBe List(card1)
      copied.isDone shouldBe true
    }
    "copy with only hand changed" in {
      val newHand = List(card2)
      val copied = originalPlayer.copy(hand = newHand)
      copied.name shouldBe "Alice"
      copied.hand shouldBe newHand
      copied.isDone shouldBe true
    }
    "copy with only isDone changed" in {
      val copied = originalPlayer.copy(isDone = false)
      copied.name shouldBe "Alice"
      copied.hand shouldBe List(card1)
      copied.isDone shouldBe false
    }
    "copy with name and hand changed" in {
      val newHand = List(card1, card2)
      val copied = originalPlayer.copy(name = "Charlie", hand = newHand)
      copied.name shouldBe "Charlie"
      copied.hand shouldBe newHand
      copied.isDone shouldBe true
    }
    "copy with name and isDone changed" in {
      val copied = originalPlayer.copy(name = "David", isDone = false)
      copied.name shouldBe "David"
      copied.hand shouldBe List(card1)
      copied.isDone shouldBe false
    }
    "copy with hand and isDone changed" in {
      val newHand = List(card2)
      val copied = originalPlayer.copy(hand = newHand, isDone = false)
      copied.name shouldBe "Alice"
      copied.hand shouldBe newHand
      copied.isDone shouldBe false
    }
    "copy with all parameters changed" in {
      val newHand = List(card1, card2)
      val copied =
        originalPlayer.copy(name = "Eve", hand = newHand, isDone = false)
      copied.name shouldBe "Eve"
      copied.hand shouldBe newHand
      copied.isDone shouldBe false
    }
    "copy with no parameters (returns same values)" in {
      val copied = originalPlayer.copy()
      copied.name shouldBe originalPlayer.name
      copied.hand shouldBe originalPlayer.hand
      copied.isDone shouldBe originalPlayer.isDone
    }
  }
  "PlayerFactory" should {
    val cardFactory = TestHelper.cardFactory
    val playerFactory = TestHelper.playerFactory
    "create a player with name and empty hand" in {
      val player = playerFactory("Alice")
      player.name shouldBe "Alice"
      player.hand shouldBe List.empty
      player.isDone shouldBe false
    }
    "create a player with name and hand" in {
      val hand = List(cardFactory(Suit.Hearts, Rank.Six))
      val player = playerFactory("Bob", hand)
      player.name shouldBe "Bob"
      player.hand shouldBe hand
      player.isDone shouldBe false
    }
    "create a player with isDone status" in {
      val player = playerFactory("Charlie", List.empty, isDone = true)
      player.name shouldBe "Charlie"
      player.isDone shouldBe true
    }
    "use PlayerFactoryImpl directly with default parameters" in {
      val factory = new de.htwg.DurakApp.model.impl.PlayerFactoryImpl()
      val player = factory("Alice")
      player.name shouldBe "Alice"
      player.hand shouldBe List.empty
      player.isDone shouldBe false
    }
    "use PlayerFactoryImpl directly with all parameters" in {
      val factory = new de.htwg.DurakApp.model.impl.PlayerFactoryImpl()
      val card = de.htwg.DurakApp.model.impl
        .CardImpl(Suit.Hearts, Rank.Ace, isTrump = false)
      val player = factory("Bob", List(card), isDone = true)
      player.name shouldBe "Bob"
      player.hand shouldBe List(card)
      player.isDone shouldBe true
    }
    "test trait default parameters through PlayerFactory interface" in {
      val factory: PlayerFactory =
        new de.htwg.DurakApp.model.impl.PlayerFactoryImpl()
      val player1 = factory("Test1")
      player1.hand shouldBe List.empty
      player1.isDone shouldBe false
      val card = de.htwg.DurakApp.model.impl
        .CardImpl(Suit.Clubs, Rank.King, isTrump = false)
      val player2 = factory("Test2", List(card))
      player2.isDone shouldBe false
    }
    "use default hand=List.empty when not specified" in {
      val player = playerFactory("Alice")
      player.hand shouldBe List.empty
    }
    "use default isDone=false when not specified" in {
      val player = playerFactory("Bob")
      player.isDone shouldBe false
    }
    "use default hand and isDone when only name specified" in {
      val player = playerFactory("Charlie")
      player.name shouldBe "Charlie"
      player.hand shouldBe List.empty
      player.isDone shouldBe false
    }
    "respect explicit hand parameter" in {
      val hand = List(TestHelper.Card(Suit.Hearts, Rank.Ace))
      val player = playerFactory("David", hand)
      player.hand shouldBe hand
    }
    "respect explicit isDone parameter" in {
      val player = playerFactory("Eve", List.empty, isDone = true)
      player.isDone shouldBe true
    }
    "respect all explicit parameters" in {
      val hand = List(TestHelper.Card(Suit.Clubs, Rank.King))
      val player = playerFactory("Frank", hand, isDone = true)
      player.name shouldBe "Frank"
      player.hand shouldBe hand
      player.isDone shouldBe true
    }
  }
}
