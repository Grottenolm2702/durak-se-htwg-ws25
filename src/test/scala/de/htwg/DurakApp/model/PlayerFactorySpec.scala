package de.htwg.DurakApp.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import com.google.inject.Guice

class PlayerFactorySpec extends AnyWordSpec with Matchers {

  // Use DI instead of direct instantiation
  private val injector = Guice.createInjector(new de.htwg.DurakApp.DurakModule)

  "PlayerFactory" should {
    val cardFactory: CardFactory = injector.getInstance(classOf[CardFactory])
    val playerFactory: PlayerFactory =
      injector.getInstance(classOf[PlayerFactory])

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
