package de.htwg.DurakApp.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.impl.{CardFactoryImpl, PlayerFactoryImpl}

class PlayerFactorySpec extends AnyWordSpec with Matchers {
  
  "PlayerFactory" should {
    val cardFactory: CardFactory = new CardFactoryImpl()
    val playerFactory: PlayerFactory = new PlayerFactoryImpl()
    
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
  }
}
