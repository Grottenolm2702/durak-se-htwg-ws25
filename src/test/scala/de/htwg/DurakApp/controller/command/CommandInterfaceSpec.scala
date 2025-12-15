package de.htwg.DurakApp.controller.command

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.ModelInterface.*
import de.htwg.DurakApp.model.ModelInterface.StateInterface.*
import de.htwg.DurakApp.controller.ControllerInterface.*
import de.htwg.DurakApp.controller.command.CommandInterface.*

class CommandInterfaceSpec extends AnyWordSpec with Matchers {

  "CommandInterface" should {
    val card = Card(Suit.Clubs, Rank.Six)
    val player1 = Player("P1", List(card))
    val player2 = Player("P2", List.empty)

    val gameState = GameState(
      players = List(player1, player2),
      deck = List.empty,
      table = Map.empty,
      discardPile = List.empty,
      trumpCard = Card(Suit.Diamonds, Rank.Ace),
      attackerIndex = 0,
      defenderIndex = 1,
      gamePhase = AttackPhase
    )

    "create PlayCardCommand through interface" in {
      val command = PlayCardCommand(card)
      command shouldBe a[GameCommand]
    }

    "create PassCommand through interface" in {
      val command = PassCommand()
      command shouldBe a[GameCommand]
    }

    "create TakeCardsCommand through interface" in {
      val command = TakeCardsCommand()
      command shouldBe a[GameCommand]
    }

    "create PhaseChangeCommand through interface" in {
      val command = PhaseChangeCommand()
      command shouldBe a[GameCommand]
    }

    "create commands via CommandFactory for valid PlayCardAction" in {
      val playAction = PlayCardAction(card)
      val commandResult = CommandFactory.createCommand(playAction, gameState)
      commandResult shouldBe a[Right[?, ?]]
    }

    "create commands via CommandFactory for PassAction" in {
      val commandResult = CommandFactory.createCommand(PassAction, gameState)
      commandResult shouldBe a[Right[?, ?]]
    }

    "create commands via CommandFactory for TakeCardsAction" in {
      val defenseState = gameState.copy(gamePhase = DefensePhase)
      val commandResult =
        CommandFactory.createCommand(TakeCardsAction, defenseState)
      commandResult shouldBe a[Right[?, ?]]
    }
  }
}
