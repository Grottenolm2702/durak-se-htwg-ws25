package de.htwg.DurakApp.model
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.testutil.TestHelper
import de.htwg.DurakApp.model.impl.{PlayerFactoryImpl, CardImpl}
class PlayerFactorySpec extends AnyWordSpec with Matchers {
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
      val factory = new PlayerFactoryImpl()
      val player = factory("Alice")
      player.name shouldBe "Alice"
      player.hand shouldBe List.empty
      player.isDone shouldBe false
    }
    "use PlayerFactoryImpl directly with all parameters" in {
      val factory = new PlayerFactoryImpl()
      val card = CardImpl(Suit.Hearts, Rank.Ace, isTrump = false)
      val player = factory("Bob", List(card), isDone = true)
      player.name shouldBe "Bob"
      player.hand shouldBe List(card)
      player.isDone shouldBe true
    }
    "test trait default parameters through PlayerFactory interface" in {
      val factory: PlayerFactory = new PlayerFactoryImpl()
      val player1 = factory("Test1")
      player1.hand shouldBe List.empty
      player1.isDone shouldBe false
      val card = CardImpl(Suit.Clubs, Rank.King, isTrump = false)
      val player2 = factory("Test2", List(card))
      player2.isDone shouldBe false
    }
  }
}
