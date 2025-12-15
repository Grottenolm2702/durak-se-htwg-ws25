package de.htwg.DurakApp.controller.command

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.controller.command.CommandInterface._
import de.htwg.DurakApp.model.ModelInterface._

class GameCommandSpec extends AnyWordSpec with Matchers {
  "GameCommand" should {
    "be implemented by PlayCardCommand" in {
      val command: GameCommand = PlayCardCommand(Card(Suit.Hearts, Rank.Ace))
      command shouldBe a[GameCommand]
    }
    
    "be implemented by PassCommand" in {
      val command: GameCommand = PassCommand()
      command shouldBe a[GameCommand]
    }
    
    "be implemented by TakeCardsCommand" in {
      val command: GameCommand = TakeCardsCommand()
      command shouldBe a[GameCommand]
    }
  }
}
